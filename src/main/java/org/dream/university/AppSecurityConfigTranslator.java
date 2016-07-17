package org.dream.university;


import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@EnableWebSecurity
@Configuration
@Order(1)
public class AppSecurityConfigTranslator extends AppSecurityConfig {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.antMatcher("/translator/**")
				.authorizeRequests()
				.antMatchers("/translator/registration*","/bulbular*").anonymous()
				.antMatchers("/translator/index","/translator/login*").permitAll()
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
				.accessDeniedHandler(accessDeniedHandler);
				
	}
}
