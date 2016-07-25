package ua.translate;


import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ua.translate")
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class AppConfig extends WebMvcConfigurerAdapter{
	
	@Value("${db.driver}") private String dbDriver;
	@Value("${db.url}") private String dbUrl;
	@Value("${db.username}") private String dbUsername;
	@Value("${db.password}") private String dbPassword;
	
	@Value("${hibernate.show_sql}") private String hibernateShowSql;
	@Value("${hibernate.dialect}") private String hibernateDialect;
	@Value("${hibernate.hbm2ddl.auto}") private String hibernateHbm2ddlAuto;
	@Value("${hibernate.id.new_generator_mappings}") private String hibernateIdGeneratorMap;
	
	@Value("${scan_packages}") private String scanPackages;
	
	@Value("${mailserver.host}") private String host;
	@Value("${mailserver.port}") private Integer port;
	@Value("${mailserver.protocol}") private String protocol;
	@Value("${mailserver.username}") private String username;
	@Value("${mailserver.password}") private String password;
	@Value("${mail.smtp.starttls.enable}") private String smtpStarttlsEnable;
	@Value("${mail.smtp.auth}") private String smtpAuth;
	@Value("${mail.smtp.ssl.trust}") private String smtpSslTrust;
	
	@Bean(name = "viewResolver")
	public FreeMarkerViewResolver getViewResolver() {
		FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
	    viewResolver.setCache(true);
		viewResolver.setPrefix("");
	    viewResolver.setSuffix(".ftl");
	    return viewResolver;
	}
	
	@Bean(name = "freemarkerConfig")
	public FreeMarkerConfigurer freemarkerConfigurer(){
		FreeMarkerConfigurer freemarkerConfigurer = new FreeMarkerConfigurer();
		freemarkerConfigurer.setTemplateLoaderPath("/WEB-INF/");
		return freemarkerConfigurer;
	}
	
	@Bean(name = "dataSource")
	public DataSource getDataSource() {
	    BasicDataSource dataSource = new BasicDataSource();
	    dataSource.setDriverClassName(dbDriver);
	    dataSource.setUrl(dbUrl);
	    dataSource.setUsername(dbUsername);
	    dataSource.setPassword(dbPassword);
	    return dataSource;
	}
	
	@Autowired
	@Bean(name = "sessionFactory")
	public SessionFactory getSessionFactory(DataSource dataSource) {
	 
	    LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
	    sessionBuilder.scanPackages(scanPackages);
	    sessionBuilder.addProperties(getHibernateProperties());
	    return sessionBuilder.buildSessionFactory();
	}
	
	private Properties getHibernateProperties() {
	    Properties properties = new Properties();
	    properties.put("hibernate.show_sql", hibernateShowSql);
	    properties.put("hibernate.dialect", hibernateDialect);
	    properties.put("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);
	    properties.put("hibernate.id.new_generator_mappings",hibernateIdGeneratorMap);
	    return properties;
	}
	
	@Autowired
	@Bean(name = "transactionManager")
	public HibernateTransactionManager getTransactionManager(
	        SessionFactory sessionFactory) {
	    HibernateTransactionManager transactionManager = new HibernateTransactionManager(
	            sessionFactory);
	 
	    return transactionManager;
	}
	
	@Bean(name = "messageSource")
	public MessageSource getMessageSource(){
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("/WEB-INF/propertyFiles/messagesource");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}
	
	@Bean(name = "multipartResolver")
	public MultipartResolver getMultipartResolver(){
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(100000);
		return multipartResolver;
	}
	
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}
	
	@Bean
    public AuthenticationTrustResolver getAuthenticationTrustResolver() {
        return new AuthenticationTrustResolverImpl();
    }
	
	@Bean
	public JavaMailSender mailSender(){
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		mailSender.setPort(port);
		mailSender.setUsername(username);
		mailSender.setPassword(password);
		mailSender.setProtocol(protocol);
		Properties props = new Properties();
		props.put("mail.smtp.starttls.enable",smtpStarttlsEnable);
		props.put("mail.smtp.auth", smtpAuth);
		props.put("mail.smtp.ssl.trust", smtpSslTrust);
		mailSender.setJavaMailProperties(props);
		return mailSender;
	}
	
	@Bean 
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
