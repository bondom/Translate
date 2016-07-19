package ua.translate;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import ua.translate.handler.CustomSuccessHandler;

@EnableWebSecurity
@Configuration
@ComponentScan(basePackages = {"ua.translate"})
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	CustomSuccessHandler customSuccessHandler;

	@Autowired
	@Qualifier("customAccessDeniedHandler")
	AccessDeniedHandler accessDeniedHandler;
	
	@Autowired
	DataSource dataSource;
	
    @Autowired
    PersistentTokenRepository tokenRepository;
    
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
	
	
}
