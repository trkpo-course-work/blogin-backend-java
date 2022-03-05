//package com.trkpo.blogin.controller;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.trkpo.blogin.BloginApplication;
//import com.trkpo.blogin.converter.PostConverter;
//import com.trkpo.blogin.converter.UserConverter;
//import com.trkpo.blogin.dto.PostDTO;
//import com.trkpo.blogin.dto.UserDTO;
//import com.trkpo.blogin.entity.Post;
//import com.trkpo.blogin.entity.User;
//import com.trkpo.blogin.repository.CredentialRepository;
//import com.trkpo.blogin.repository.PictureRepository;
//import com.trkpo.blogin.repository.PostRepository;
//import com.trkpo.blogin.repository.UserRepository;
//import com.trkpo.blogin.service.PostService;
//import com.trkpo.blogin.service.UserService;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.ArrayList;
//
////@RunWith(MockitoJUnitRunner.class)
////@ExtendWith(MockitoExtension.class)
////@SpringBootTest(classes = {PostController.class, PostConverter.class, UserConverter.class})
//////@ContextConfiguration(classes = TestConfig.class)
////public class PostControllerTest {
////    @InjectMocks
////    PostController postController;
////    @MockBean
////    PostRepository postRepository;
////    @MockBean
////    UserRepository userRepository;
////    @MockBean
////    PictureRepository pictureRepository;
////    @MockBean
////    CredentialRepository credentialRepository;
////    @MockBean
////    PostService postService;
////    @MockBean
////    UserService userService;
//@SpringBootTest(classes = PostController.class)
//class PostControllerTest {
//    @InjectMocks
//    @Autowired
//    PostController postController;
//    @MockBean
//    PostService postService;
//    @MockBean
//    PostRepository postRepository;
//    @MockBean
//    UserRepository userRepository;
//    @MockBean
//    PictureRepository pictureRepository;
//    @MockBean
//    CredentialRepository credentialRepository;
//    @MockBean
//    PostConverter postConverter;
//
//    private PostDTO postDTO = new PostDTO();
//    private Post post = new Post();
//    private UserDTO userDTO = new UserDTO();
//    private User user = new User();
//
//    @org.junit.jupiter.api.BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        postController = new PostController();
//
//        user.setId(1L);
//        user.setName("name");
//
//        userDTO.setId(1L);
//        userDTO.setName("name");
//        userDTO.setPictureId(2L);
//        userDTO.setLogin("login");
//        userDTO.setEmail("email");
//        userDTO.setPassword("pass");
//
//        postDTO.setId(1L);
//        postDTO.setUserDTO(userDTO);
//        postDTO.setUserId(userDTO.getId());
//        postDTO.setText("text");
//        postDTO.setPrivate(true);
//        postDTO.setDateTime(123456L);
//        postDTO.setPictureId(2L);
//
//        post.setUser(user);
//        post.setDateTime(123456L);
//        post.setPrivate(true);
//        post.setText("text");
//    }
//
//    @org.junit.jupiter.api.Test
//    void createPost() {
//        ResponseEntity<?> correct_answer = ResponseEntity.ok(postDTO);
//        Mockito.when(postRepository.save(post)).thenReturn(post);
//        ResponseEntity<?> createPostAnswer = postController.createPost(postDTO);
//        assertEquals(createPostAnswer, correct_answer);
//    }
//
//    @org.junit.jupiter.api.Test
//    void editPost() {
//    }
//
//    @org.junit.jupiter.api.Test
//    void deletePost() {
//    }
//}

package com.trkpo.blogin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trkpo.blogin.controller.PostController;
import com.trkpo.blogin.converter.PostConverter;
import com.trkpo.blogin.converter.UserConverter;
import com.trkpo.blogin.dto.PostDTO;
import com.trkpo.blogin.dto.UserDTO;
import com.trkpo.blogin.entity.Credentials;
import com.trkpo.blogin.entity.Post;
import com.trkpo.blogin.entity.User;
import com.trkpo.blogin.repository.CredentialRepository;
import com.trkpo.blogin.repository.PictureRepository;
import com.trkpo.blogin.repository.PostRepository;
import com.trkpo.blogin.repository.UserRepository;
import com.trkpo.blogin.service.PostService;
import com.trkpo.blogin.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {PostController.class, PostConverter.class, UserConverter.class})
class BloginApplicationTests {
    @InjectMocks
    @Autowired
    PostController postController;
    @MockBean
    UserService userService;
    @MockBean
    PostService postService;
    @MockBean
    PostRepository postRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    PictureRepository pictureRepository;
    @MockBean
    CredentialRepository credentialRepository;
    @Autowired
    PostConverter postConverter;
    @Autowired
    PostConverter userConverter;

    private PostDTO postDTO = new PostDTO();
    private Post post = new Post();
    private UserDTO userDTO = new UserDTO();
    private User user = new User();

    @BeforeEach
    public void init() throws JsonProcessingException {
        MockitoAnnotations.openMocks(this);
        Mockito.when(postRepository.save(Mockito.any(Post.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            return args[0];
        });
        Mockito.when(userService.getUserFromContext()).thenReturn(new User("Name", "email", null));
        user.setId(1L);
        user.setName("name");

        userDTO.setId(1L);
        userDTO.setName("name");
        userDTO.setPictureId(2L);
        userDTO.setLogin("login");
        userDTO.setEmail("email");
        userDTO.setPassword("pass");

        postDTO.setId(1L);
        postDTO.setUserDTO(userDTO);
        postDTO.setUserId(userDTO.getId());
        postDTO.setText("text");
        postDTO.setPrivate(true);
        postDTO.setDateTime(123456L);
        postDTO.setPictureId(2L);

        post.setUser(user);
        post.setDateTime(123456L);
        post.setPrivate(true);
        post.setText("text");

        Mockito.when(credentialRepository.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(new Credentials("login", "pass", "email")));
    }

    @Test
    void createPost() {
        ResponseEntity<?> correct_answer = ResponseEntity.ok(postDTO);
        ResponseEntity<?> createPostAnswer = postController.createPost(postDTO);
        assertEquals(correct_answer, createPostAnswer);
    }
}