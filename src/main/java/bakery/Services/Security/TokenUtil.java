package bakery.Services.Security;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenUtil implements Serializable {

	private static final long serialVersionUID = -2550185165626007488L;
	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	@Value("${jwt.secret}")
	private String secret;

	// jjwt <= 0.9 treated a String signing key as base64-encoded; keep that
	// interpretation so existing secrets and tokens remain valid
	private SecretKey signingKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
	}

	// retrieve username from jwt token
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	//this block along with the following three methods may consider removing when trying to validate the token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload();
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	public String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, userName);
	}

	private String doGenerateToken(Map<String, Object> claims, String subject) {
		JwtBuilder token = Jwts.builder().claims(claims).subject(subject)
				.issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis()+ JWT_TOKEN_VALIDITY*1000))
				.signWith(signingKey(), Jwts.SIG.HS512);

		return token.compact();
	}

	//need to introduce userDetails 
	public Boolean validateToken(String token) {
		final String username = getUsernameFromToken(token);
		return (!isTokenExpired(token));
	}
}
