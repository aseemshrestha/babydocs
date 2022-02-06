package com.babydocs.service;

import com.babydocs.model.Role;
import com.babydocs.model.User;
import com.babydocs.repository.RoleRepository;
import com.babydocs.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AppService
{
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public AppService(RoleRepository roleRepository, UserRepository userRepository)
    {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Transactional( rollbackFor = Exception.class )
    public void saveRole(Role role)
    {
        this.roleRepository.save(role);
    }

    @Transactional( rollbackFor = Exception.class )
    public void saveUser(User user)
    {
        this.userRepository.save(user);
    }

    @Transactional( rollbackFor = Exception.class )
    public List<User> saveUsers(List<User> users)
    {
        return this.userRepository.saveAll(users);
    }

    public Optional<User> getUser(String username)
    {
        return this.userRepository.findByUsername(username);
    }
}
