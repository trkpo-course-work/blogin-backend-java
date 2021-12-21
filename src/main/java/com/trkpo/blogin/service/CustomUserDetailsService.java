package com.trkpo.blogin.service;

import com.trkpo.blogin.entity.Credentials;
import com.trkpo.blogin.entity.CustomUserDetails;
import com.trkpo.blogin.repository.CredentialRepository;
import com.trkpo.blogin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CredentialRepository credentialRepository;
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Optional<Credentials> credentials = credentialRepository.findById(Long.valueOf(userId));
        if (credentials.isPresent()) {
            CustomUserDetails userDetails = new CustomUserDetails();
            userDetails.setId(credentials.get().getUser().getId().toString());
            userDetails.setPassword(credentials.get().getPassword());
            return userDetails;
        } else {
            return null;
        }
    }
}
