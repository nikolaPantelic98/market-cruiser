package com.marketcruiser.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // used to load data by username (in this case email address)
    @Bean
    public UserDetailsService userDetailsService() {
        return new MarketCruiserUserDetailsService();
    }

    // used to encode and verify passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // used to authenticate users based on their credentials
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    // configures the authentication manager which is responsible for authenticating users based on their credentials
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    // configures the security for HTTPS requests
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/users/**")
                .hasAuthority("Admin")
                .antMatchers("/categories/**").hasAnyAuthority("Admin", "Editor")
                .antMatchers("/brands/**").hasAnyAuthority("Admin", "Editor")

                .antMatchers("/products/new", "/products/delete/**")
                    .hasAnyAuthority("Admin", "Editor")
                .antMatchers("/products/edit/**", "/products/save", "/products/check-unique")
                    .hasAnyAuthority("Admin", "Editor", "Salesperson")
                .antMatchers("/products", "/products/", "/products/detail/**", "/products/page/**")
                    .hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")

                .antMatchers("/products/**").hasAnyAuthority("Admin", "Editor")

                .antMatchers("/orders", "/orders/", "/orders/page/**", "/orders/detail/**").hasAnyAuthority("Admin", "Salesperson", "Shipper")
                .antMatchers("/customers/**", "/orders/**", "/get_shipping_cost").hasAnyAuthority("Admin", "Salesperson")
                .antMatchers("/shipping_rates/**").hasAnyAuthority("Admin", "Salesperson")
                .antMatchers("/report/**").hasAnyAuthority("Admin", "Salesperson")
                .antMatchers("/articles/**").hasAnyAuthority("Admin", "Editor")
                .antMatchers("/menus/**").hasAnyAuthority("Admin", "Editor")
                .antMatchers("/settings/**", "/countries/**", "/states/**").hasAuthority("Admin")
                .antMatchers("/states/list_by_country/**").hasAnyAuthority("Admin", "Salesperson")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .usernameParameter("emailAddress")
                    .permitAll()
                .and().logout().permitAll()
                .and()
                    .rememberMe()
                        .key("AbcdefgHijKLmN_12345")
                        .tokenValiditySeconds(7 * 24 * 60 * 60);

        http.headers().frameOptions().sameOrigin();
    }

    // configures web security
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
    }
}
