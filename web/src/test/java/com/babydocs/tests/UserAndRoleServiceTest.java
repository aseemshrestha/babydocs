package com.babydocs.tests;

import com.babydocs.constants.RoleBuilder;
import com.babydocs.model.Role;
import com.babydocs.model.User;
import com.babydocs.repository.RoleRepository;
import com.babydocs.repository.UserRepository;
import com.babydocs.service.UserAndRoleService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAndRoleServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserAndRoleService userAndRoleService;

    @Test
    public void test_when_create_user_should_return_user() {
        User userToSave = new User();
        userToSave.setUsername("email@email.com");
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(userToSave);
        User savedUser = userAndRoleService.saveUser(userToSave);
        Assertions.assertThat(savedUser.getUsername()).isSameAs(userToSave.getUsername());
        verify(userRepository, times(1)).save(userToSave);

    }

    @Test
    public void test_when_create_users_should_return_users() {
        User userToSave1 = new User();
        userToSave1.setUsername("email1@email.com");
        User userToSave2 = new User();
        userToSave2.setUsername("email2@email.com");
        List<User> users = new ArrayList<>();
        users.add(userToSave1);
        users.add(userToSave2);
        when(userRepository.saveAll(users)).thenReturn(users);
        List<User> savedUsers = userAndRoleService.saveUsers(users);
        Assertions.assertThat(savedUsers.size()).isEqualTo(2);
        verify(userRepository, times(1)).saveAll(users);

    }

    @Test
    public void test_save_role() {
        Role role = RoleBuilder.getSiteUserR();
        userAndRoleService.saveRole(role);
        verify(roleRepository, times(1)).save(role);
    }
}
