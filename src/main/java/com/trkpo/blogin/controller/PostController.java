package com.trkpo.blogin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trkpo.blogin.converter.PostConverter;
import com.trkpo.blogin.dto.PostDTO;
import com.trkpo.blogin.entity.Post;
import com.trkpo.blogin.entity.User;
import com.trkpo.blogin.repository.PostRepository;
import com.trkpo.blogin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PostController {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostConverter postConverter;

    @PostMapping("/v1/post")
    public ResponseEntity<?> createPost(@RequestBody PostDTO post) {
        try {
            Post newPost = postConverter.convertPostDTOtoEntity(post, getUserFromContext());
            return ResponseEntity.ok(postConverter.convertPostEntityToDTO(postRepository.save(newPost)));
        } catch (JsonProcessingException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/v1/post/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        if (postRepository.findById(id).isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        postRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private User getUserFromContext() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.getById(Long.valueOf(userId));
    }
}
