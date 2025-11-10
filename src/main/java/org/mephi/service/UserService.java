package org.mephi.service;

import lombok.RequiredArgsConstructor;
import org.mephi.domain.entity.User;
import org.mephi.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getOrCreateUser(String username) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = new User(username);
                    return userRepository.save(newUser);
                });
    }
}
