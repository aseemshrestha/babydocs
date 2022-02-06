package com.babydocs.security;

import com.babydocs.model.User;
import com.babydocs.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserPrincipalDetailService implements UserDetailsService
{

    private final UserRepository userRepository;

    public UserPrincipalDetailService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException
    {
        Optional<User> parent = this.userRepository.findByUsername(s);
        CustomUserDetails customUserDetails = new CustomUserDetails(parent.get());
        return customUserDetails;
    }
}
