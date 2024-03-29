package com.marketcruiser.security.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * Represents a customer authenticated via OAuth2.
 * Provides additional functionality over the base OAuth2User implementation, such as retrieving the user's full name.
 */
public class CustomerOAuth2User implements OAuth2User {

    private OAuth2User oAuth2User;
    private String fullName;


    public CustomerOAuth2User(OAuth2User oAuth2User) {
        this.oAuth2User = oAuth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oAuth2User.getAttribute("name");
    }

    public String getEmail() {
        return oAuth2User.getAttribute("email");
    }

    public String getFullName() {
        return fullName != null ? fullName : oAuth2User.getAttribute("name");
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
