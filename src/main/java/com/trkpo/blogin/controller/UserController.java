package com.trkpo.blogin.controller;

import com.trkpo.blogin.converter.UserConverter;
import com.trkpo.blogin.entity.User;
import com.trkpo.blogin.repository.CredentialRepository;
import com.trkpo.blogin.repository.PostRepository;
import com.trkpo.blogin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CredentialRepository credentialRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserConverter userConverter;

    @GetMapping("/v1/user")
    public ResponseEntity<?> getAuthorizedUserInfo() {
        return ResponseEntity.ok(userConverter.convertUserEntityToDTO(getUserFromContext()));
    }

    @GetMapping("/v1/user/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(userConverter.convertUserEntityToDTO(user.get()));
    }

    @GetMapping("/v1/user/favorites")
    public ResponseEntity<?> getUserFavourites() {
        User user = userRepository.getById(getUserFromContext().getId());
        List<User> favourite = user.getFavourites();
        return ResponseEntity.ok().body(favourite.stream().map(it -> userConverter.convertUserEntityToDTO(it)).collect(Collectors.toList()));
    }

    @Transactional
    @DeleteMapping("/v1/user")
    public ResponseEntity<?> deleteUser() {
        credentialRepository.deleteByUserId(getUserFromContext().getId());
        postRepository.deleteAllByUserId(getUserFromContext().getId());
        userRepository.deleteById(getUserFromContext().getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/v1/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok().body(userRepository.findAll().stream().map(it -> userConverter.convertUserEntityToDTO(it)).collect(Collectors.toList()));
    }

    private User getUserFromContext() {
        String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.getById(Long.valueOf(id));
    }
}
