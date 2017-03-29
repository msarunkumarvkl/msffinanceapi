package com.innomind.msffinance.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.innomind.msffinance.web.util.CustomObjectMapper;

@EnableWebMvc
@Configuration
@ComponentScan({"com.innomind.msffinance.web.*"})
@Import({WebSecurityConfiguration.class, HibernateConfiguration.class, SwaggerConfig.class})
@EnableAspectJAutoProxy
public class AppConfiguration extends WebMvcConfigurerAdapter{
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(createJacksonConvertors());
		super.configureMessageConverters(converters);
	}

	private HttpMessageConverter<?> createJacksonConvertors() {
		MappingJackson2HttpMessageConverter convertor = 
				new MappingJackson2HttpMessageConverter();
		CustomObjectMapper customMapper = new CustomObjectMapper();
		convertor.setObjectMapper(customMapper);
		return convertor;
	}
	
	@Bean
	protected InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver
                          = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/pages/");
		viewResolver.setSuffix(".html");
		return viewResolver;
	}
	
	/**
	 * Configuration to set JSON as response for all methods.
	 * @return RequestMappingHandlerAdapter
	 */
	@Bean
	protected RequestMappingHandlerAdapter getAdapter() {
		RequestMappingHandlerAdapter requestMapper = new RequestMappingHandlerAdapter();
		List<HttpMessageConverter<?>> converters = new ArrayList<>();
		converters.add(new MappingJackson2HttpMessageConverter());
		requestMapper.setMessageConverters(converters);
		return requestMapper;
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html")
			.addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/");
		registry.addResourceHandler("/styles/**")
			.addResourceLocations("/WEB-INF/styles/");
		registry.addResourceHandler("/scripts/**")
			.addResourceLocations("/WEB-INF/scripts/");
		registry.addResourceHandler("/bower_components/**")
		.addResourceLocations("/WEB-INF/bower_components/");
		registry.addResourceHandler("/images/**")
		.addResourceLocations("/WEB-INF/images/");
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
	    registry.addMapping("/api/**")
	        .allowedOrigins("*")
	        .allowedMethods("PUT", "DELETE" , "POST", "GET", "OPTIONS", "HEAD")
	        .allowedHeaders("Origin", "X-Requested-With", "Content-Type", "Accept", 
	        		"Authorization", "withCredentials", "X-XSRF-TOKEN")
	        .exposedHeaders("XSRF-TOKEN")
	        .allowCredentials(true).maxAge(3600);
	}
	
	/*@Bean
	public DispatcherServletBeanPostProcessor dispatcherServletBeanPostProcessor() {
	    return new DispatcherServletBeanPostProcessor();
	}

	public static class DispatcherServletBeanPostProcessor implements BeanPostProcessor {
	    @Override
	    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
	        if (bean instanceof DispatcherServlet) {
	            ((DispatcherServlet) bean).setDispatchOptionsRequest(true);
	        }
	        return bean;
	    }

	    @Override
	    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
	        return bean;
	    }
	}*/
	
	@Bean
	public DispatcherServlet dispatcherServlet() {
	    return new DispatcherServlet();
	}
	
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
	    configurer.enable();
	}

}
