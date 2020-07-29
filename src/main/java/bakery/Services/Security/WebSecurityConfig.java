package bakery.Services.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private RequestFilter jwtRequestFilter;
	
	@Override
    protected void configure(HttpSecurity httpSecurity)throws Exception {
    	httpSecurity.csrf().disable()
    	.authorizeRequests().antMatchers("/customer/createAccount")
    	.permitAll()
    	.anyRequest().authenticated()
    	.and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
    	.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    	.and().addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    
	}
	

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}
