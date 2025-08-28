package com.app.lurnityBackend.security;

import com.app.lurnityBackend.model.User;
import com.app.lurnityBackend.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    /**
     * Load user by email (used as username for Spring Security)
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Convert your User entity to Spring Security UserDetails
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),          // username (email)
                user.getPassword(),       // hashed password
                new ArrayList<>()         // authorities/roles (empty for now)
        );
    }
}
