package com.innomind.msffinance.configuration;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.runner.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter{

    private final RequestMatcher requireCsrfProtectionMatcher = new DefaultRequiresCsrfMatcher();
    private final AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandlerImpl();
	    
	@Autowired
	protected void configureGlobal(AuthenticationManagerBuilder auth) 
			throws Exception{
		auth.inMemoryAuthentication().withUser("tyoke007")
			.password("@tyoke007").roles("USER");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.sessionFixation().none()
			.and()
			.authorizeRequests()
				.antMatchers("/index/**").permitAll().anyRequest().authenticated()
				.antMatchers("/index.html").permitAll().anyRequest().authenticated()
				.antMatchers(HttpMethod.OPTIONS,"/api/**").permitAll()
				.antMatchers("/api/**").access("hasRole('ROLE_USER')")
			.and()
			.csrf()
			.disable()
			.addFilterBefore(csrfHeaderFilter(), CsrfFilter.class)
			.httpBasic();
	}

	private Filter csrfHeaderFilter() { 
	   return new OncePerRequestFilter() { 
		    @Override 
		    protected void doFilterInternal(HttpServletRequest request, 
		            HttpServletResponse response, FilterChain filterChain) 
		      throws ServletException, IOException { 
		    	 if (requireCsrfProtectionMatcher.matches(request)) {
		            final String csrfTokenValue = request.getHeader("X-XSRF-TOKEN");
		            final Cookie[] cookies = request.getCookies();
		            String csrfCookieValue = null;
		            if (cookies != null) {
		                for (Cookie cookie : cookies) {
		                    if (cookie.getName().equals("XSRF-TOKEN")) {
		                        csrfCookieValue = cookie.getValue();
		                    }
		                }
		            }
		            if (csrfTokenValue == null || !csrfTokenValue.equals(csrfCookieValue)) {
		                accessDeniedHandler.handle(request, response, new AccessDeniedException(
		                        "Missing or non-matching XSRF-token"));
		                return;
		            }
		        } else if(request.getMethod().equals("GET")) {
		        	String token = UUID.randomUUID().toString();
		            Cookie cookie = new Cookie("XSRF-TOKEN", token);
		            cookie.setPath("/");
		            response.addCookie(cookie);
		            response.setHeader("XSRF-TOKEN", token);
		        }
	            filterChain.doFilter(request, response);
		    } 
	   }; 
	}
	
	public static final class DefaultRequiresCsrfMatcher implements RequestMatcher {
        private final Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");

        @Override
        public boolean matches(HttpServletRequest request) {
            return !allowedMethods.matcher(request.getMethod()).matches();
        }
    }
}
