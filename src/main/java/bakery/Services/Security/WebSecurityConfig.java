package bakery.Services.Security;


//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@Configuration
//public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//
//	@Autowired
//	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//
//	@Autowired
//	private RequestFilter jwtRequestFilter;
//	
//	@Override
//    protected void configure(HttpSecurity httpSecurity)throws Exception {
//    	httpSecurity.csrf().disable()
//    	.authorizeRequests().antMatchers("/customer/createAccount")
//    	.permitAll()
//    	.anyRequest().authenticated()
//    	.and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
//    	.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//    	.and().addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//    
//	}
//	
//
//	@Bean
//	@Override
//	public AuthenticationManager authenticationManagerBean() throws Exception {
//		return super.authenticationManagerBean();
//	}
//}
