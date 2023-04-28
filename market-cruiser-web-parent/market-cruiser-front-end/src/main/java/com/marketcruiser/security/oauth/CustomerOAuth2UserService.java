package com.marketcruiser.security.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * This class extends the DefaultOAuth2UserService to customize the OAuth2User object
 * returned by the loadUser method to create a CustomerOAuth2User object. This is done to
 * add a custom attribute (fullName) to the OAuth2User object returned by the OAuth2 provider
 * and use it in the application.
 *
 * @see DefaultOAuth2UserService
 * @see OAuth2UserRequest
 * @see OAuth2AuthenticationException
 * @see OAuth2User
 * @see CustomerOAuth2User
 */
@Service
public class CustomerOAuth2UserService extends DefaultOAuth2UserService {

    /**
     * This method overrides the loadUser method of the DefaultOAuth2UserService class
     * to customize the OAuth2User object returned by the method. It creates a new instance
     * of CustomerOAuth2User and returns it instead of the default OAuth2User object.
     *
     * @param userRequest the user request containing the access token to be used to load the user
     * @return a customized OAuth2User object (CustomerOAuth2User)
     * @throws OAuth2AuthenticationException if an error occurs while loading the user
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        return new CustomerOAuth2User(user);
    }
}
