package com.cb.unit.repository;

import com.cb.model.AppUser;
import com.cb.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save and retrieve a user by id")
    void saveAndFindById() {
        AppUser user = new AppUser();
        user.setUsername("john_doe");
        AppUser saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(userRepository.findById(saved.getId())).contains(saved);
    }

    @Test
    @DisplayName("Should find user by username")
    void findByUsername_found() {
        AppUser user = new AppUser();
        user.setUsername("jane_doe");
        userRepository.save(user);

        Optional<AppUser> found = userRepository.findByUsername("jane_doe");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("jane_doe");
    }

    @Test
    @DisplayName("Should return empty when username does not exist")
    void findByUsername_notFound() {
        Optional<AppUser> found = userRepository.findByUsername("nonexistent");
        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("Should find all users")
    void findAllUsers() {
        AppUser u1 = new AppUser();
        u1.setUsername("user1");
        AppUser u2 = new AppUser();
        u2.setUsername("user2");
        userRepository.save(u1);
        userRepository.save(u2);

        assertThat(userRepository.findAll())
                .extracting(AppUser::getUsername)
                .containsExactlyInAnyOrder("user1", "user2");
    }
}