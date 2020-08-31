package bakery.Services.Security;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenUtil implements Serializable {

	private static final long serialVersionUID = -2550185165626007488L;
	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	//temporary secret code
	private static final String secret = "secret";

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
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
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
		JwtBuilder token = Jwts.builder().setClaims(claims).setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis()+ JWT_TOKEN_VALIDITY*1000))
				.signWith(SignatureAlgorithm.HS512, secret);

		return token.compact();
	}

	public Boolean validateToken(String token) {
		final String username = getUsernameFromToken(token);
		return (!isTokenExpired(token));
	}
}
