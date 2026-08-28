package bakery.Services.Security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;


@Service
public class RequestFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestFilter.class);

	private final TokenUtil jwtTokenUtil;
	private final UserDetailService userDetailService;

	@Autowired
	public RequestFilter(TokenUtil jwtTokenUtil, UserDetailService userDetailService) {
		this.jwtTokenUtil = jwtTokenUtil;
		this.userDetailService = userDetailService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String requestToken = request.getHeader("Authorization");
		if (requestToken != null) {
			try {
				String userName = jwtTokenUtil.getUsernameFromToken(requestToken);
				if (userName != null && jwtTokenUtil.validateToken(requestToken)
						&& SecurityContextHolder.getContext().getAuthentication() == null) {

					// Verify the user actually exists in DynamoDB before granting access
					UserDetails userDetails = userDetailService.loadUserByUsername(userName);

					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			} catch (UsernameNotFoundException e) {
				LOGGER.warn("JWT references unknown user, rejecting: {}", e.getMessage());
			} catch (Exception e) {
				LOGGER.error("JWT validation failed", e);
			}
		}
		filterChain.doFilter(request, response);
	}
}
