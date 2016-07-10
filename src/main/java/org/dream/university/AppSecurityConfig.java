package org.dream.university;

import java.util.ArrayList;
import java.util.List;

import org.dream.university.handler.CustomSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;



@EnableWebSecurity
@Configuration
@ComponentScan(basePackages = {"org.dream.university.service","org.dream.university.handler"})
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	CustomSuccessHandler customSuccessHandler;
		
	@Override
	public void configure(WebSecurity web){
		web 
			.ignoring()
			.antMatchers(new String[]{"/resources/**"});
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, UserDetailsService uds) throws Exception{
		auth.userDetailsService(uds)
			.passwordEncoder(bcryptEncoder());
		
	}
		
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers("/about","search*","/registration*","/login*","/bulbular*").permitAll()
			.antMatchers("/admin/**").hasRole("ADMIN")
			.antMatchers("/personal/**").hasRole("USER")
			.anyRequest().authenticated()
				.and()
			.formLogin()
			.loginPage("/login")
			.permitAll()
			.successHandler(customSuccessHandler)
			.failureUrl("/login?error")
			.usernameParameter("username")
			.passwordParameter("password")
			.loginProcessingUrl("/j_spring_security_check")
				.and()
			.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login")
					.and()
				.csrf()
					.disable();
				
	}
		
	/*@Bean(name = "authenticationManager")
	@Autowired
	public ProviderManager getProviderManager(DaoAuthenticationProvider daoAuthenticationProvider){
		List<AuthenticationProvider> providers = new ArrayList<>();
		providers.add(daoAuthenticationProvider);
		ProviderManager providerManager = new ProviderManager(providers);
		return providerManager;
	}
		
	@Bean
	@Autowired
	public DaoAuthenticationProvider daoAuthenticationProvider(
			UserDetailsService userDetailsService){
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(new BCryptPasswordEncoder());
		return provider;
	}
		*/
	@Bean
	public PasswordEncoder bcryptEncoder(){
		return new BCryptPasswordEncoder();
	}
}
