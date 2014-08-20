package com.github.vspiewak.jbeatnik.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

    public static String getCurrentUsername() {

        String username = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            if(authentication.getPrincipal() instanceof UserDetails) {
               username = ((UserDetails) authentication.getPrincipal()).getUsername();
            }
        }

        return username;

    }

}
