package com.trkpo.blogin.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trkpo.blogin.dto.PostDTO;
import com.trkpo.blogin.dto.SpanDTO;
import com.trkpo.blogin.dto.UserDTO;
import com.trkpo.blogin.entity.Picture;
import com.trkpo.blogin.entity.Post;
import com.trkpo.blogin.entity.User;
import com.trkpo.blogin.repository.PictureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {PostConverter.class})
public class PostConverterTest {
    @Autowired
    @InjectMocks
    private PostConverter postConverter;
    @MockBean
    private PictureRepository pictureRepository;
    @MockBean
    private UserConverter userConverter;

    private Picture pic = new Picture();
    private Post entityPost = new Post();
    private PostDTO postDTO = new PostDTO();
    private User user = new User();
    private UserDTO userDTO = new UserDTO();
    @BeforeEach
    void init() {
        Picture pic = new Picture();
        pic.setPath("/path");
        pic.setId(1L);

        user.setId(1L);
        user.setName("name");

        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setPictureId(1L);
        userDTO.setLogin("login");
        userDTO.setEmail("email");
        userDTO.setPassword("pswd");

        entityPost.setPrivate(false);
        entityPost.setText("text");
        entityPost.setPicture(pic);
        entityPost.setDateTime(123L);
        entityPost.setUser(user);
        entityPost.setSpan("[{\"type\":\"BOLD\", \"start\":1, \"end\":2}]");

        postDTO.setPrivate(entityPost.isPrivate());
        postDTO.setText(entityPost.getText());
        postDTO.setPictureId(pic.getId());
        postDTO.setDateTime(entityPost.getDateTime());
        postDTO.setUserDTO(userDTO);
        postDTO.setUserId(userDTO.getId());
        postDTO.setSpan(List.of(new SpanDTO("BOLD", 1, 2)));

        Mockito.when(pictureRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(pic));
    }

    @Test
    void testConvertDToToEntity() throws JsonProcessingException {
        Post correct = entityPost;
        Post result = postConverter.convertPostDTOtoEntity(postDTO, user);
        assertEquals(correct.getDateTime(), result.getDateTime());
        assertEquals(correct.getText(), result.getText());
        assertEquals(correct.getPicture(), result.getPicture());
        assertEquals(correct.getUser(), result.getUser());
    }

    @Test
    void testConvertEntityToDTO() throws JsonProcessingException {
        PostDTO correct = postDTO;
        PostDTO result = postConverter.convertPostEntityToDTO(entityPost);
        assertEquals(correct.getDateTime(), result.getDateTime());
        assertEquals(correct.getText(), result.getText());
        assertEquals(correct.getPictureId(), result.getPictureId());
        assertEquals(correct.getUserId(), result.getUserId());
    }
}
