package bakery.Services.Security;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Limits repeated requests to sensitive auth endpoints per IP address.
 * Allows MAX_REQUESTS attempts within WINDOW_SECONDS before returning 429.
 */
@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitFilter.class);

    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_SECONDS = 60;

    // IP -> [attempt count, window start epoch second]
    private final Map<String, long[]> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Only rate-limit the auth endpoints
        return !(path.equals("/customer/signIn")
                || path.equals("/customer/signUp")
                || path.equals("/customer/verifyToken"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String ip = getClientIp(request);
        long now = Instant.now().getEpochSecond();

        requestCounts.compute(ip, (key, val) -> {
            if (val == null || now - val[1] >= WINDOW_SECONDS) {
                return new long[]{1, now};
            }
            val[0]++;
            return val;
        });

        long[] entry = requestCounts.get(ip);
        if (entry[0] > MAX_REQUESTS) {
            LOGGER.warn("Rate limit exceeded for IP: {}", ip);
            response.setStatus(429);
            response.getWriter().write("Too many requests — please try again later.");
            return;
        }

        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
