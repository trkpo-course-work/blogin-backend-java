package com.trkpo.blogin.unit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trkpo.blogin.controller.PostController;
import com.trkpo.blogin.converter.PostConverter;
import com.trkpo.blogin.converter.UserConverter;
import com.trkpo.blogin.dto.PostDTO;
import com.trkpo.blogin.dto.UserDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {PostController.class})
class PostControllerTest {
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
    @MockBean
    PostConverter postConverter;
    @MockBean
    UserConverter userConverter;

    private PostDTO postDTO = new PostDTO();
    private PostDTO editedPostDTO = new PostDTO();
    private Post post = new Post();
    private UserDTO userDTO = new UserDTO();
    private User user = new User();

    @BeforeEach
    public void init() throws JsonProcessingException {
        user.setId(1L);
        user.setName("name");

        post.setId(1L);
        post.setUser(user);
        post.setDateTime(123456L);
        post.setPrivate(true);
        post.setText("text");

        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setPictureId(null);
        userDTO.setLogin("login");
        userDTO.setEmail("email");
        userDTO.setPassword("pass");

        postDTO.setId(post.getId());
        postDTO.setUserDTO(userDTO);
        postDTO.setUserId(userDTO.getId());
        postDTO.setText(post.getText());
        postDTO.setPrivate(true);
        postDTO.setDateTime(post.getDateTime());

        editedPostDTO.setId(1L);
        editedPostDTO.setUserDTO(postDTO.getUserDTO());
        editedPostDTO.setUserId(postDTO.getUserDTO().getId());
        editedPostDTO.setText("newTest");
        editedPostDTO.setPrivate(postDTO.isPrivate());
        editedPostDTO.setDateTime(postDTO.getDateTime());
        editedPostDTO.setPictureId(postDTO.getPictureId());


        MockitoAnnotations.openMocks(this);
        Mockito.when(postRepository.save(Mockito.any(Post.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Post post = (Post) args[0];
            post.setId(1L);
            return post;
        });
        Mockito.when(postService.editPost(Mockito.any(Post.class), Mockito.any(PostDTO.class))).thenAnswer(invocation -> {
            post.setText(editedPostDTO.getText());
            postDTO.setText(editedPostDTO.getText());
            return post;
        });
        Mockito.when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        Mockito.when(postRepository.findById(3L)).thenReturn(Optional.empty());
        Mockito.when(postConverter.convertPostDTOtoEntity(Mockito.any(PostDTO.class), Mockito.any(User.class))).thenReturn(post);
        Mockito.when(postConverter.convertPostEntityToDTO(Mockito.any(Post.class))).thenReturn(postDTO);
        Mockito.when(userService.getUserFromContext()).thenReturn(user);
        Mockito.doNothing().when(postRepository).deleteById(post.getId());

    }

    @Test
    void testCreatePost() throws JsonProcessingException {
        ResponseEntity<?> correct = ResponseEntity.ok(postDTO);
        ResponseEntity<?> answer = postController.createPost(postDTO);
        assertEquals(correct, answer);

        Mockito.when(postConverter.convertPostEntityToDTO(Mockito.any(Post.class))).thenThrow(JsonProcessingException.class);
        correct = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        answer = postController.createPost(postDTO);
        assertEquals(correct, answer);
    }
    @Test
    void testEditPost() throws JsonProcessingException {
        ResponseEntity<?> correct = ResponseEntity.ok(editedPostDTO);
        ResponseEntity<?> answer = postController.editPost(postDTO.getId(), editedPostDTO);
        assertEquals(correct.getBody(), answer.getBody());

        Mockito.when(postService.editPost(Mockito.any(Post.class), Mockito.any(PostDTO.class))).thenThrow(JsonProcessingException.class);
        correct = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        answer = postController.editPost(postDTO.getId(), editedPostDTO);
        assertEquals(correct.getBody(), answer.getBody());
    }
    @Test
    void testDeletePost() {
        ResponseEntity<?> correct = ResponseEntity.ok().build();
        ResponseEntity<?> answer = postController.deletePost(postDTO.getId());
        assertEquals(correct, answer);

        correct = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        answer = postController.deletePost(3L);
        assertEquals(correct, answer);
    }
}