package ua.translate;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Configuration
	@Order(1)
	public static class AdminSecurityConfig extends BaseSecurityConfig{
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http    
					.antMatcher("/bulbular/**")
					.authorizeRequests()
					.antMatchers("/bulbular/login*").permitAll()
					.anyRequest().hasRole("ADMIN")
				.and()
					.formLogin()
					.loginPage("/bulbular/login")
					.permitAll()
					.successHandler(customSuccessHandler)
					.failureUrl("/bulbular/login?error")
					.usernameParameter("username")
					.passwordParameter("password")
					.loginProcessingUrl("/j_spring_security_check")
				.and()
						.logout().deleteCookies("JSESSIONID")
								.logoutUrl("/bulbular/logout")
								.logoutSuccessUrl("/bulbular/login?logout")
				.and()
					.csrf()
				.and()
					.exceptionHandling()
					.accessDeniedHandler(accessDeniedHandler);
		}
	}
	@Configuration
	@Order(3)
	public static class AppSecurityConfigClient extends BaseSecurityConfig{
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http    
					
					.authorizeRequests()
					.antMatchers("/client/registration*").anonymous()
					.antMatchers("/index","/translators","/orders","/client/login*","/client/confirmation").permitAll()
					.antMatchers("/client/**").hasRole("CLIENT")
				.and()
					.formLogin()
					.loginPage("/client/login")
					.permitAll()
					.successHandler(customSuccessHandler)
					.failureUrl("/client/login?error")
					.usernameParameter("username")
					.passwordParameter("password")
					.loginProcessingUrl("/j_spring_security_check")
				.and()
						.logout().deleteCookies("JSESSIONID")
								.logoutUrl("/client/logout")
								.logoutSuccessUrl("/client/login?logout")
				.and()
				
				 /*!!!!Доделать saved request url!!!!*/
				
					.rememberMe().tokenRepository(tokenRepository)
					.tokenValiditySeconds(86400)
				.and()
					.csrf()
				.and()
					.exceptionHandling()
					.accessDeniedHandler(accessDeniedHandler);
		}
		
	}
	
	@Configuration
	@Order(2)
	public static class AppSecurityConfigTranslator extends BaseSecurityConfig {
		
			  
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
					.antMatcher("/translator/**")
					.authorizeRequests()
					.antMatchers("/translator/registration*").anonymous()
					.antMatchers("/translator/index","/translator/login*",
							"/translator/confirmation","/translator/download/**").permitAll()
					.antMatchers("/translator/**").hasRole("TRANSLATOR")
					.anyRequest().authenticated()
				.and()
					.formLogin()
					.loginPage("/translator/login")
					.permitAll()
					.successHandler(customSuccessHandler)
					.failureUrl("/translator/login?error")
					.usernameParameter("username")
					.passwordParameter("password")
					.loginProcessingUrl("/j_spring_security_check")
				.and()
						.logout().deleteCookies("JSESSIONID")
								.logoutUrl("/translator/logout")
								.logoutSuccessUrl("/translator/login?logout")
				.and()
				/**
				 * Доделать saved request url!!!
				 */
					.rememberMe().tokenRepository(tokenRepository)
					.tokenValiditySeconds(86400)
				.and()
					.csrf()
				.and()
					.exceptionHandling()
					.accessDeniedHandler(accessDeniedHandler)
				.and()
					.userDetailsService(uds);
		}
		
	}

}
