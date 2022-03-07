package com.trkpo.blogin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trkpo.blogin.converter.PostConverter;
import com.trkpo.blogin.converter.UserConverter;
import com.trkpo.blogin.dto.PostDTO;
import com.trkpo.blogin.entity.Picture;
import com.trkpo.blogin.entity.Post;
import com.trkpo.blogin.entity.User;
import com.trkpo.blogin.repository.CredentialRepository;
import com.trkpo.blogin.repository.PictureRepository;
import com.trkpo.blogin.repository.PostRepository;
import com.trkpo.blogin.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {PostService.class})
public class PostServiceTest {
    @InjectMocks
    @Autowired
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
    UserConverter userConverter;
    @MockBean
    PostConverter postConverter;
    private PostDTO postDTO = new PostDTO();
    private PostDTO editedPostDTO = new PostDTO();
    private Post post = new Post();
    private Picture pic = new Picture();
    private User user = new User();
    private User favUser = new User();

    @BeforeEach
    void init() throws JsonProcessingException {
        user.setId(1L);
        user.setName("name");
        user.setFavourites(List.of(favUser));

        favUser.setId(2L);
        favUser.setName("eman");

        post.setId(1L);
        post.setDateTime(123456L);
        post.setPrivate(true);
        post.setText("text");
        post.setUser(user);

        pic.setId(1L);
        pic.setPath("/path");

        postDTO.setId(post.getId());
        postDTO.setText(post.getText());
        postDTO.setPrivate(true);
        postDTO.setDateTime(post.getDateTime());

        editedPostDTO.setId(1L);
        editedPostDTO.setUserDTO(postDTO.getUserDTO());
        editedPostDTO.setText(postDTO.getText());
        editedPostDTO.setPrivate(postDTO.isPrivate());
        editedPostDTO.setDateTime(postDTO.getDateTime());
        editedPostDTO.setPictureId(pic.getId());

        Mockito.when(postConverter.convertPostDTOtoEntity(Mockito.any(PostDTO.class), Mockito.any(User.class))).thenReturn(post);
        Mockito.when(postConverter.convertPostEntityToDTO(Mockito.any(Post.class))).thenReturn(postDTO);
        Mockito.when(pictureRepository.getById(Mockito.any(Long.class))).thenReturn(pic);
        Mockito.when(postRepository.getAllByUserId(Mockito.any(Long.class))).thenReturn(List.of(post));
    }

    @Test
    void testEditPost() throws JsonProcessingException {
        Post createPostAnswer = postService.editPost(post, editedPostDTO);
        post.setPicture(pic);
        Post correct = post;
        assertEquals(correct, createPostAnswer);

        editedPostDTO.setText("new text");
        post.setText("new text");
        correct = post;
        assertEquals(correct, createPostAnswer);
    }

    @Test
    void testGetPosts() throws JsonProcessingException {
        List<PostDTO> correct = List.of(postDTO);
        List<PostDTO> answer = postService.getPosts(user);
        assertEquals(correct, answer);
    }

    @Test
    void testGetNews() throws JsonProcessingException {
        post.setUser(favUser);
        post.setPrivate(false);
        postDTO.setPrivate(false);
        List<PostDTO> correct = List.of(postDTO);
        List<PostDTO> answer = postService.getNews(user);
        assertEquals(correct, answer);

        post.setPrivate(true);
        postDTO.setPrivate(true);
        correct = Collections.emptyList();
        answer = postService.getNews(user);
        assertEquals(correct, answer);
    }
}
