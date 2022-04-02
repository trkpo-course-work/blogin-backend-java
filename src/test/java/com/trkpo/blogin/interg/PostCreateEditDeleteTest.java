package com.trkpo.blogin.interg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trkpo.blogin.dto.PostDTO;
import com.trkpo.blogin.dto.UserDTO;
import com.trkpo.blogin.entity.Credentials;
import com.trkpo.blogin.entity.Post;
import com.trkpo.blogin.entity.User;
import com.trkpo.blogin.repository.CredentialRepository;
import com.trkpo.blogin.repository.PictureRepository;
import com.trkpo.blogin.repository.PostRepository;
import com.trkpo.blogin.repository.UserRepository;
import com.trkpo.blogin.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class PostCreateEditDeleteTest {
    @MockBean
    private PostRepository postRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CredentialRepository credentialRepository;
    @MockBean
    private PictureRepository pictureRepository;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    private PostDTO postDTO;
    private PostDTO postEditDTO;
    private UserDTO userDTO;
    private User user;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    private void setUp() throws JsonProcessingException {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName("NAME");

        Post post = new Post();

        userDTO = new UserDTO();
        userDTO.setName(user.getName());
        userDTO.setId(user.getId());
        userDTO.setLogin("username");
        userDTO.setEmail("polina");
        userDTO.setPassword("password");

        Credentials credentials = new Credentials();
        credentials.setPassword(userDTO.getPassword());
        credentials.setLogin(userDTO.getLogin());
        credentials.setEmail(userDTO.getEmail());
        credentials.setUser(user);

        postDTO = new PostDTO();
        postDTO.setText("POST TEXT");
        postDTO.setPrivate(true);
        postDTO.setUserDTO(userDTO);
        postDTO.setUserId(user.getId());
        postDTO.setSpan(Collections.emptyList());
        postDTO.setDateTime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

        postEditDTO = new PostDTO();
        postEditDTO.setText(postDTO.getText());
        postEditDTO.setPrivate(postDTO.isPrivate());
        postEditDTO.setUserDTO(postDTO.getUserDTO());
        postEditDTO.setUserId(postDTO.getUserId());
        postEditDTO.setSpan(Collections.emptyList());
        postEditDTO.setDateTime(postDTO.getDateTime());

        post.setId(1L);
        post.setPrivate(postDTO.isPrivate());
        post.setUser(user);
        post.setSpan(objectMapper.writeValueAsString(post.getSpan()));
        post.setText(postDTO.getText());
        post.setDateTime(postDTO.getDateTime());

        Mockito.when(userService.getUserFromContext()).thenReturn(user);
        Mockito.when(postRepository.save(Mockito.any(Post.class))).thenReturn(post);
        Mockito.when(credentialRepository.findByUserId(Mockito.any(Long.class))).thenReturn(java.util.Optional.of(credentials));
        Mockito.when(postRepository.findById(Mockito.any(Long.class))).thenReturn(java.util.Optional.of(post));
        Mockito.doNothing().when(postRepository).deleteById(Mockito.any(Long.class));
    }

    @WithMockUser(username="username")
    @Test
    void test1() throws Exception {
        mockMvc.perform(post("/api/v1/post")
                .content(objectMapper.writeValueAsString(postDTO))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userDTO.id", Matchers.is(userDTO.getId().intValue())))
                .andExpect(jsonPath("$.userDTO.name", Matchers.is(userDTO.getName())))
                .andExpect(jsonPath("$.userDTO.login", Matchers.is(userDTO.getLogin())))
                .andExpect(jsonPath("$.userDTO.email", Matchers.is(userDTO.getEmail())))
                .andExpect(jsonPath("$.userId", is(user.getId().intValue())))
                .andExpect(jsonPath("$.text", is(postDTO.getText())))
                .andExpect(jsonPath("$.span", is(Collections.emptyList())))
                .andExpect(jsonPath("$.private", is(postDTO.isPrivate())))
                .andExpect(jsonPath("$.dateTime", is(postDTO.getDateTime().intValue())));

        postEditDTO.setText("NEW TEXT");
        mockMvc.perform(put("/api/v1/post/{id}", 1L)
                .content(objectMapper.writeValueAsString(postEditDTO))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userDTO.id", Matchers.is(userDTO.getId().intValue())))
                .andExpect(jsonPath("$.userDTO.name", Matchers.is(userDTO.getName())))
                .andExpect(jsonPath("$.userDTO.login", Matchers.is(userDTO.getLogin())))
                .andExpect(jsonPath("$.userDTO.email", Matchers.is(userDTO.getEmail())))
                .andExpect(jsonPath("$.userId", is(user.getId().intValue())))
                .andExpect(jsonPath("$.text", is(postEditDTO.getText())))
                .andExpect(jsonPath("$.span", is(Collections.emptyList())))
                .andExpect(jsonPath("$.private", is(postEditDTO.isPrivate())))
                .andExpect(jsonPath("$.dateTime", is(postEditDTO.getDateTime().intValue())));

        mockMvc.perform(delete("/api/v1/post/{id}", 1L))
                .andExpect(status().isOk());

        verify(postRepository, times(2)).save(Mockito.any(Post.class));
        verify(postRepository, times(1)).deleteById(Mockito.any(Long.class));
    }
}
