package com.weplayWeb.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;

import com.weplayWeb.spring.config.CorsConfig;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@EnableWebSecurity
@Configuration
public class SecurityConfig  {
	
	@Autowired
    private CorsConfig corsConfig;

//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//            .withUser("user")
//            .password(passwordEncoder().encode("password"))
//            .roles("USER")
//            .and()
//            .withUser("admin")
//            .password(passwordEncoder().encode("admin"))
//            .roles("ADMIN");
//    }
//
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//            .authorizeRequests()
//                .requestMatchers("/**").hasRole("USER")
//                .anyRequest().authenticated()
//                .and()
//            .formLogin()
//                .permitAll()
//                .and()
//            .logout()
//                .permitAll();
//    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
	
//	 @Bean
//	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//	        http
//	        
//	          
//	        .cors(customizer -> customizer.configurationSource(corsConfig.corsConfigurationSource())) // Enable CORS with custom configuration
//            .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity
//            .authorizeHttpRequests(auth -> auth
//                    .requestMatchers("/payment").permitAll() // Allow unauthenticated access to /payment
//                    .anyRequest().permitAll());
//	        
//	        return http.build();
	
	
//	    }
	 
	 
	 
	    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfig corsConfig) throws Exception {
         http
         .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfig.corsConfigurationSource()))

         .csrf(csrfCustomizer -> csrfCustomizer.disable())

          .authorizeHttpRequests(authorize -> authorize
        		  							.requestMatchers("/payment").permitAll() // Allow unauthenticated access to /payment endpoint
        		  							.anyRequest()
        		  							.authenticated()); // Require authentication for other requests

	     return http.build();
	 }
}
