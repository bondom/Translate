package ua.translate;


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
				.antMatchers("/client/registration*","/bulbular*","/client/confirmation").anonymous()
				.antMatchers("/index","/translators","/orders","/client/login*").permitAll()
				.antMatchers("/client/**").hasRole("CLIENT")
				.antMatchers("/admin/**").hasRole("ADMIN")
			.and()
				.formLogin()
				.loginPage("/client/login")
				.permitAll()
				.successHandler(customSuccessHandler)
				.failureUrl("/client/login?error=1")
				.usernameParameter("username")
				.passwordParameter("password")
				.loginProcessingUrl("/j_spring_security_check")
			.and()
					.logout().deleteCookies("JSESSIONID")
							.logoutUrl("/client/logout")
							.logoutSuccessUrl("/client/login?logout=1")
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
