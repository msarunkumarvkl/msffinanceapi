package com.innomind.msffinance.configuration;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.innomind.msffinance.web.controller"))
				.paths(PathSelectors.any())
				.build()
				.apiInfo(apiInfo())
				.globalResponseMessage(RequestMethod.GET, new ArrayList<ResponseMessage>(
						Arrays.asList(
								new ResponseMessageBuilder()
								.code(500)
								.message("Internal Server Error")
								.build(),
								new ResponseMessageBuilder()
								.code(401)
								.message("UnAuthorized Usage")
								.build(),
								new ResponseMessageBuilder()
								.code(404)
								.message("Not Found")
								.build(),
								new ResponseMessageBuilder()
								.code(200)
								.message("Success")
								.build(),
								new ResponseMessageBuilder()
								.code(417)
								.message("There is some error in validating the input!")
								.build())
						))
				.globalResponseMessage(RequestMethod.POST, new ArrayList<ResponseMessage>(
						Arrays.asList(
								new ResponseMessageBuilder()
								.code(500)
								.message("Internal Server Error")
								.build(),
								new ResponseMessageBuilder()
								.code(401)
								.message("UnAuthorized Usage")
								.build(),
								new ResponseMessageBuilder()
								.code(404)
								.message("Not Found")
								.build(),
								new ResponseMessageBuilder()
								.code(403)
								.message("Forbidden!")
								.build(),
								new ResponseMessageBuilder()
								.code(201)
								.message("Created Successfully")
								.build())
						))
				.globalResponseMessage(RequestMethod.PUT, new ArrayList<ResponseMessage>(
						Arrays.asList(
								new ResponseMessageBuilder()
								.code(500)
								.message("Internal Server Error")
								.build(),
								new ResponseMessageBuilder()
								.code(401)
								.message("UnAuthorized Usage")
								.build(),
								new ResponseMessageBuilder()
								.code(404)
								.message("Not Found")
								.build(),
								new ResponseMessageBuilder()
								.code(403)
								.message("Forbidden!")
								.build(),
								new ResponseMessageBuilder()
								.code(200)
								.message("Success")
								.build(),
								new ResponseMessageBuilder()
								.code(417)
								.message("There is some error in validating the input!")
								.build())
						));
	}
	
	private ApiInfo apiInfo() {
	    return new ApiInfoBuilder()
	    		.title("Msf Finance")
	            .description("Msf Finance is an Finance company which lends money to it's "
	    	    		+ "client for which they need to have an android and mobile "
	    	    		+ "application. This is the api documentation for the Restful "
	    	    		+ "api which the Mobile app and Web application is using.")
	            .version("0.1")
	            .contact(new Contact("Innomind Tech Services", "www.innomind.in", 
	            		"mail2innomind@gmail.com"))
	            .license("Apche 2.0")
	            .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
	            .build();
	}
}
