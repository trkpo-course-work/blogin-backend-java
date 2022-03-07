package com.trkpo.blogin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trkpo.blogin.converter.PostConverter;
import com.trkpo.blogin.dto.PostDTO;
import com.trkpo.blogin.entity.Post;
import com.trkpo.blogin.repository.PostRepository;
import com.trkpo.blogin.repository.UserRepository;
import com.trkpo.blogin.service.PostService;
import com.trkpo.blogin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PostController {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostService postService;
    @Autowired
    private PostConverter postConverter;
    @Autowired
    private UserService userService;

    @PostMapping("/v1/post")
    public ResponseEntity<?> createPost(@RequestBody PostDTO post) {
        try {
            Post newPost = postConverter.convertPostDTOtoEntity(post, userService.getUserFromContext());
            return ResponseEntity.ok(postConverter.convertPostEntityToDTO(postRepository.save(newPost)));
        } catch (JsonProcessingException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/v1/post/{id}")
    public ResponseEntity<?> editPost(@PathVariable Long id, @RequestBody PostDTO post) {
        Optional<Post> postToEdit = postRepository.findById(id);
        if (postToEdit.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        try {
            postService.editPost(postToEdit.get(), post);
            return ResponseEntity.ok(postConverter.convertPostEntityToDTO(postRepository.save(postToEdit.get())));
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
}
