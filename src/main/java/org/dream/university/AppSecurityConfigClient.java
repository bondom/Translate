package org.dream.university;


import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@EnableWebSecurity
@Configuration
@Order(2)
public class AppSecurityConfigClient extends AppSecurityConfig{
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http    
				.authorizeRequests()
				.antMatchers("/client/registration*","/bulbular*").anonymous()
				.antMatchers("/client/**").hasRole("CLIENT")
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/index","/translators","/orders","/client/login*").permitAll()
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
			/**
			 * Доделать saved request url!!!
			 */
				.rememberMe().tokenRepository(tokenRepository)
				.tokenValiditySeconds(86400)
			.and()
				.csrf()
			.and()
				.exceptionHandling()
				.accessDeniedHandler(accessDeniedHandler);
	}
		
	
	
}
