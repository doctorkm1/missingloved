package com.alkamel.missingloved.security;

import com.alkamel.missingloved.model.User;
import com.alkamel.missingloved.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userCode) throws UsernameNotFoundException {
        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with code: " + userCode));

        // Convert authorityCode (e.g. 6 or 7) to a GrantedAuthority
        String authority = String.valueOf(user.getAuthorityCode());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserCode())
                .password(user.getPassword()) // must be encoded
                .authorities(new SimpleGrantedAuthority(authority))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isActivated()) // prevent login if not activated
                .build();
    }
}
