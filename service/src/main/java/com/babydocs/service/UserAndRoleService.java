package com.babydocs.service;

import com.babydocs.model.Role;
import com.babydocs.model.User;
import com.babydocs.repository.RoleRepository;
import com.babydocs.repository.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserAndRoleService
{
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public UserAndRoleService(RoleRepository roleRepository, UserRepository userRepository)
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
    public User saveUser(User user)
    {
        return this.userRepository.save(user);
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

    @Transactional( rollbackFor = Exception.class )
    public int updatePassword(String username, String password)
    {
        return userRepository.updatePassword(username, password);
    }
}
