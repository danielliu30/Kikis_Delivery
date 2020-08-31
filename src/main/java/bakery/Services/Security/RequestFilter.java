package bakery.Services.Security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonToken;
import com.google.gson.Gson;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import bakery.Models.JwtRequest;

@Service
public class RequestFilter extends OncePerRequestFilter {

	
	@Autowired
	private TokenUtil jwtTokenUtil;

	private static final Gson gson = new Gson();
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		//Need to get username from the jwt token. Consult tut for it
		//read body(user) from incoming request. 
		//can consider adding level of authority
		//JwtRequest person = gson.fromJson(new BufferedReader(new InputStreamReader(request.getInputStream())), JwtRequest.class);
		
		//the arraylist holds the level of authorization. Right now its empty
		UserDetails userDetails = new User("test","test",new ArrayList<>());
		if(!(request.getHeader("Authorization")==null)) {
			if (jwtTokenUtil.validateToken(request.getHeader("Authorization"))) {
			
		
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				// After setting the Authentication in the context, we specify
				// that the current user is authenticated. So it passes the
				// Spring Security Configurations successfully.
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
			
		}
		filterChain.doFilter(request, response);
	}

}
