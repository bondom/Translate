package org.dream.university;


import javax.sql.DataSource;

import org.dream.university.handler.CustomSavedAwareHandler;
import org.dream.university.handler.CustomSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;



@EnableWebSecurity
@Configuration
@ComponentScan(basePackages = {"org.dream.university"})
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	CustomSuccessHandler customSuccessHandler;
		
	@Autowired
	DataSource dataSource;
	
    @Autowired
    PersistentTokenRepository tokenRepository;
    /*
    @Qualifier("detailsService")
    UserDetailsService userDetailsService;*/
    
	@Override
	public void configure(WebSecurity web){
		web 
			.ignoring()
			.antMatchers(new String[]{"/resources/**"});
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth,@Qualifier("detailsService") UserDetailsService uds) throws Exception{
		auth.userDetailsService(uds)
			.passwordEncoder(bcryptEncoder());
		
	}
		
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.antMatchers("/client/registration*","/client/login*","/bulbular*").anonymous()
				.antMatchers("/client/**").hasRole("CLIENT")
				.antMatchers("/index").permitAll()
				.antMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated()
			.and()
				.formLogin()
					//.successHandler(customSavedRequestAwareAuthenticationSuccessHandler())
					  //.successHandler(savedRequestAwareAuthenticationSuccessHandler())
				.loginPage("/client/login")
				.permitAll()
				//.successHandler(customSuccessHandler)
				.defaultSuccessUrl("/client/profile")
				.failureUrl("/client/login?error")
				.usernameParameter("username")
				.passwordParameter("password")
				.loginProcessingUrl("/j_spring_security_check")
			.and()
					.logout().deleteCookies("JSESSIONID")
							.logoutUrl("/client/logout")
							.logoutSuccessUrl("/client/login?logout")
			.and()
				.rememberMe().tokenRepository(tokenRepository)
				.tokenValiditySeconds(86400)
			.and()
				.csrf();
				
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
	
	@Autowired
	@Bean
    public PersistentTokenBasedRememberMeServices getPersistentTokenBasedRememberMeServices(@Qualifier("detailsService") UserDetailsService uds) {
        PersistentTokenBasedRememberMeServices tokenBasedservice = new PersistentTokenBasedRememberMeServices(
                "remember-me", uds, tokenRepository);
        return tokenBasedservice;
    }
 
	
	@Bean
	public SavedRequestAwareAuthenticationSuccessHandler 
                savedRequestAwareAuthenticationSuccessHandler() {
		
               SavedRequestAwareAuthenticationSuccessHandler auth 
                    = new SavedRequestAwareAuthenticationSuccessHandler();
		auth.setTargetUrlParameter("targetUrl");
		return auth;
	}	
	/*@Bean
	public CustomSavedAwareHandler 
                customSavedRequestAwareAuthenticationSuccessHandler() {
		
               CustomSavedAwareHandler auth 
                    = new CustomSavedAwareHandler();
		auth.setTargetUrlParameter("targetUrl");
		return auth;
	}	*/
}
