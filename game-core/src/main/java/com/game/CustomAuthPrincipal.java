package com.game;

import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor
public class CustomAuthPrincipal {

    public Set<GrantedAuthority> getAuthorities(){
        return Collections.singleton("User").stream().
                map(t -> new SimpleGrantedAuthority("ROLE_" + t)).collect(Collectors.toSet());
    }

}
