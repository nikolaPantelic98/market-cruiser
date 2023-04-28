package com.marketcruiser.security;

import com.marketcruiser.security.oauth.CustomerOAuth2UserService;
import com.marketcruiser.security.oauth.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomerOAuth2UserService oAuth2UserService;
    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    @Autowired
    private DatabaseLoginSuccessHandler databaseLoginSuccessHandler;


    /**
     * This method returns a PasswordEncoder instance which uses the BCrypt hashing function.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * This method configures the security for HTTPS requests, including
     * URL path restrictions and success handlers for form login and OAuth2 login.
     * It also sets a key and validity period for remember me functionality.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/account_details", "/update_account_details", "/cart", "/orders/**",
                        "/address_book/**", "/checkout", "/place_order", "/process_paypal_order").authenticated()
                .anyRequest().permitAll()
                .and().formLogin()
                    .loginPage("/login")
                    .usernameParameter("email")
                    .successHandler(databaseLoginSuccessHandler)
                    .permitAll()
                .and()
                .oauth2Login()
                    .loginPage("/login")
                    .userInfoEndpoint()
                    .userService(oAuth2UserService)
                    .and()
                    .successHandler(oAuth2LoginSuccessHandler)
                .and()
                .logout().permitAll()
                .and()
                .rememberMe()
                    .key("123456789_anCdEfGhIjKlMnoPqRsTuVwXyZ")
                    .tokenValiditySeconds(7 * 24 * 60 * 60)
                .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
    }

    /**
     * This method configures web security and ignores resources such as images,
     * JavaScript, and webjars.
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
    }

    /**
     * This method returns an instance of CustomerUserDetailsService, which is responsible
     * for loading user details.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomerUserDetailsService();
    }

    /**
     * This method returns an instance of DaoAuthenticationProvider, which is responsible
     * for authenticating users using the userDetailsService and passwordEncoder.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}
