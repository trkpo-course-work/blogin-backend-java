package com.trkpo.blogin.interg;

import com.trkpo.blogin.dto.UserDTO;
import com.trkpo.blogin.entity.Credentials;
import com.trkpo.blogin.entity.User;
import com.trkpo.blogin.repository.CredentialRepository;
import com.trkpo.blogin.repository.PictureRepository;
import com.trkpo.blogin.repository.PostRepository;
import com.trkpo.blogin.repository.UserRepository;
import com.trkpo.blogin.interg.util.JWTUtil;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class AddDeleteFavoriteInvalidTest {
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

    private UserDTO userDTO;
    private User user;
    private JWTUtil jwtUtil = new JWTUtil();

    @BeforeEach
    private void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("NAME");
        user.setFavourites(new ArrayList<>());

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

        Mockito.when(credentialRepository.findByUserId(1L)).thenReturn(java.util.Optional.of(credentials));
        Mockito.when(credentialRepository.findByUserId(2L)).thenReturn(java.util.Optional.empty());
        Mockito.when(credentialRepository.findById(1L)).thenReturn(java.util.Optional.of(credentials));
        Mockito.when(credentialRepository.findById(2L)).thenReturn(java.util.Optional.empty());
        Mockito.doNothing().when(postRepository).deleteById(Mockito.any(Long.class));
        Mockito.when(userRepository.getById(1L)).thenReturn(user);
        Mockito.when(userRepository.getById(2L)).thenReturn(null);
        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(user));
        Mockito.when(userRepository.findById(2L)).thenReturn(java.util.Optional.empty());
    }

    @Test
    void test() throws Exception {
        mockMvc.perform(post("/api/v1/user/favorites/{id}", 2)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.createToken("1")))
                .andExpect(status().isNotFound());

        verify(userRepository, times(1)).getById(Mockito.any(Long.class));
        verify(userRepository, times(1)).findById(Mockito.any(Long.class));

        mockMvc.perform(delete("/api/v1/user/favorites/{id}", 2)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.createToken("1")))
                .andExpect(status().isNotFound());

        verify(userRepository, times(2)).findById(Mockito.any(Long.class));
    }
}
