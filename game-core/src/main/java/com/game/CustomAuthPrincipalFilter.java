package com.game;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

public class CustomAuthPrincipalFilter extends RequestHeaderAuthenticationFilter {

    public CustomAuthPrincipalFilter(){

        this.setPrincipalRequestHeader("Authorization");

        this.setAuthenticationManager(authentication -> new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                CustomAuthPrincipal principal = (CustomAuthPrincipal) authentication.getPrincipal();
                if (principal != null && principal.getAuthorities() != null) {
                    return principal.getAuthorities();
                }
                return null;
            }

            @Override
            public Object getCredentials() {
                return authentication.getCredentials();
            }

            @Override
            public Object getDetails() {
                return authentication.getDetails();
            }

            @Override
            public Object getPrincipal() {
                return authentication.getPrincipal();
            }

            @Override
            public boolean isAuthenticated() {//authenticated once jwt token decoded successfully.
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
                authentication.setAuthenticated(isAuthenticated);
            }

            @Override
            public String getName() {
                return authentication.getName();
            }

            @Override
            public String toString() {
                return authentication.toString();
            }

        });

        this.setCheckForPrincipalChanges(true);

    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        //TODO: this is where the real auth would take place, generic for now
        String authHeader = request.getHeader("Authorization");
        return new CustomAuthPrincipal();
    }
}


