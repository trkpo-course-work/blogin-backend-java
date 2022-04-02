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
import com.trkpo.blogin.interg.util.JWTUtil;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class AddFavoriteGetNewsTest {
    @MockBean
    private PostRepository postRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CredentialRepository credentialRepository;
    @MockBean
    private PictureRepository pictureRepository;
    @Autowired
    private MockMvc mockMvc;
    private JWTUtil jwtUtil = new JWTUtil();

    private UserDTO userDTO;
    private UserDTO favouriteUserDTO;
    private User user;
    private User favouriteUser;
    private PostDTO postDTO;
    private Post post;
    private PostDTO postEditDTO;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    private void setUp() throws JsonProcessingException {
        MockitoAnnotations.openMocks(this);

        favouriteUser = new User();
        favouriteUser.setId(2L);
        favouriteUser.setName("FAV NAME");

        user = new User();
        user.setId(1L);
        user.setName("NAME");
        user.setFavourites(new ArrayList<>());

        favouriteUserDTO = new UserDTO();
        favouriteUserDTO.setName(favouriteUser.getName());
        favouriteUserDTO.setId(favouriteUser.getId());
        favouriteUserDTO.setLogin("username1");
        favouriteUserDTO.setEmail("email1");

        userDTO = new UserDTO();
        userDTO.setName(user.getName());
        userDTO.setId(user.getId());
        userDTO.setLogin("username");
        userDTO.setEmail("email");

        Credentials credentials = new Credentials();
        credentials.setPassword("password");
        credentials.setLogin(userDTO.getLogin());
        credentials.setEmail(userDTO.getEmail());
        credentials.setUser(user);

        Credentials favCredentials = new Credentials();
        favCredentials.setPassword("password1");
        favCredentials.setLogin(favouriteUserDTO.getLogin());
        favCredentials.setEmail(favouriteUserDTO.getEmail());
        favCredentials.setUser(favouriteUser);

        postDTO = new PostDTO();
        postDTO.setId(1L);
        postDTO.setText("POST TEXT");
        postDTO.setPrivate(false);
        postDTO.setUserDTO(favouriteUserDTO);
        postDTO.setUserId(favouriteUser.getId());
        postDTO.setSpan(Collections.emptyList());
        postDTO.setDateTime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

        postEditDTO = new PostDTO();
        postEditDTO.setText(postDTO.getText());
        postEditDTO.setPrivate(postDTO.isPrivate());
        postEditDTO.setUserDTO(postDTO.getUserDTO());
        postEditDTO.setUserId(postDTO.getUserId());
        postEditDTO.setSpan(Collections.emptyList());
        postEditDTO.setDateTime(postDTO.getDateTime());

        post = new Post();
        post.setId(1L);
        post.setPrivate(postDTO.isPrivate());
        post.setUser(favouriteUser);
        post.setSpan(objectMapper.writeValueAsString(post.getSpan()));
        post.setText(postDTO.getText());
        post.setDateTime(postDTO.getDateTime());

        Mockito.when(credentialRepository.findByUserId(1L)).thenReturn(java.util.Optional.of(credentials));
        Mockito.when(credentialRepository.findByUserId(2L)).thenReturn(java.util.Optional.of(favCredentials));
        Mockito.when(credentialRepository.findById(1L)).thenReturn(java.util.Optional.of(credentials));
        Mockito.when(credentialRepository.findById(2L)).thenReturn(java.util.Optional.of(favCredentials));
        Mockito.doNothing().when(postRepository).deleteById(Mockito.any(Long.class));
        Mockito.when(userRepository.getById(1L)).thenReturn(user);
        Mockito.when(userRepository.getById(2L)).thenReturn(favouriteUser);
        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(user));
        Mockito.when(userRepository.findById(2L)).thenReturn(java.util.Optional.ofNullable(favouriteUser));
        Mockito.when(postRepository.getAllByUserId(1L)).thenReturn(Collections.emptyList());
        Mockito.when(postRepository.getAllByUserId(2L)).thenReturn(Collections.singletonList(post));
        Mockito.when(postRepository.save(Mockito.any(Post.class))).thenReturn(post);
    }

    @Test
    void test() throws Exception {
        mockMvc.perform(post("/api/v1/user/favorites/{id}", 2)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.createToken("1")))
                .andExpect(status().isOk());

        verify(userRepository, times(1)).getById(Mockito.any(Long.class));
        verify(userRepository, times(1)).findById(Mockito.any(Long.class));

        mockMvc.perform(get("/api/v1/user/favorites")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.createToken("1")))
                .andExpect(status().isOk())
                .andExpect(content().json("[" + objectMapper.writeValueAsString(favouriteUserDTO) + "]"));

        verify(userRepository, times(3)).getById(Mockito.any(Long.class));
        verify(credentialRepository, times(1)).findByUserId(Mockito.any(Long.class));
        assertEquals(user.getFavourites(), Collections.singletonList(favouriteUser));

        mockMvc.perform(post("/api/v1/post")
                .content(objectMapper.writeValueAsString(postDTO))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.createToken("2")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userDTO.id", Matchers.is(favouriteUserDTO.getId().intValue())))
                .andExpect(jsonPath("$.userDTO.name", Matchers.is(favouriteUserDTO.getName())))
                .andExpect(jsonPath("$.userDTO.login", Matchers.is(favouriteUserDTO.getLogin())))
                .andExpect(jsonPath("$.userDTO.email", Matchers.is(favouriteUserDTO.getEmail())))
                .andExpect(jsonPath("$.userId", is(favouriteUser.getId().intValue())))
                .andExpect(jsonPath("$.text", is(postDTO.getText())))
                .andExpect(jsonPath("$.span", is(Collections.emptyList())))
                .andExpect(jsonPath("$.private", is(postDTO.isPrivate())))
                .andExpect(jsonPath("$.dateTime", is(postDTO.getDateTime().intValue())));

        mockMvc.perform(get("/api/v1/user/news")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.createToken("1")))
                .andExpect(status().isOk())
                .andExpect(content().json("[" + objectMapper.writeValueAsString(postDTO) + "]"));
    }
}
