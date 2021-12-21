package com.trkpo.blogin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trkpo.blogin.converter.UserConverter;
import com.trkpo.blogin.dto.UserDTO;
import com.trkpo.blogin.entity.User;
import com.trkpo.blogin.exception.InvalidPasswordException;
import com.trkpo.blogin.repository.CredentialRepository;
import com.trkpo.blogin.repository.PostRepository;
import com.trkpo.blogin.repository.UserRepository;
import com.trkpo.blogin.service.PostService;
import com.trkpo.blogin.service.UserService;
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
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private UserConverter userConverter;

    @PutMapping("/v1/user")
    public ResponseEntity<?> edit(@RequestBody UserDTO userDTO) throws InvalidPasswordException {
        return ResponseEntity.ok(userService.editUser(userDTO, getUserFromContext()));
    }

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

    @PostMapping("/v1/user/favorites/{id}")
    public ResponseEntity<?> addUserFavorites(@PathVariable Long id) {
        if (userService.addFavorites(id, getUserFromContext())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/v1/user/favorites/{id}")
    public ResponseEntity<?> deleteUserFavorites(@PathVariable Long id) {
        if (userService.deleteFavorites(id, getUserFromContext())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/v1/user/news")
    public ResponseEntity<?> getNews() {
        User user = getUserFromContext();
        try {
            return ResponseEntity.ok().body(postService.getNews(user));
        } catch (JsonProcessingException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/v1/user/posts")
    public ResponseEntity<?> getPosts() {
        try {
            return ResponseEntity.ok().body(postService.getPosts(getUserFromContext()));
        } catch (JsonProcessingException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/v1/users/{id}/posts")
    public ResponseEntity<?> getPostsByUserId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok().body(postService.getPosts(userRepository.getById(id)));
        } catch (JsonProcessingException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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

    @GetMapping("/v1/search/{info}")
    public ResponseEntity<?> searchUser(@PathVariable String info) {
        return ResponseEntity.ok().body(userService.search(info));
    }

    @GetMapping("/v1/user/isFavorite/{id}")
    public ResponseEntity<?> isFavorite(@PathVariable Long id) {
        return ResponseEntity.ok().body(userService.isFavorite(id, getUserFromContext()));
    }

    private User getUserFromContext() {
        String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.getById(Long.valueOf(id));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<?> handleInvalidPasswordException(InvalidPasswordException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
    }
}
