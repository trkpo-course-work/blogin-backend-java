package com.trkpo.blogin.interg;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class EditDeleteUserTest {
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
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private JWTUtil jwtUtil = new JWTUtil();

    private UserDTO userDTO;
    private UserDTO userToEditDTO;
    private User user;
    private Credentials credentials;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    private void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName("NAME");

        userDTO = new UserDTO();
        userDTO.setName(user.getName());
        userDTO.setId(user.getId());
        userDTO.setLogin("username");
        userDTO.setEmail("polina");

        userToEditDTO = new UserDTO();
        userToEditDTO.setName(user.getName());
        userToEditDTO.setId(user.getId());
        userToEditDTO.setLogin("username");
        userToEditDTO.setEmail("polina");

        credentials = new Credentials();
        credentials.setPassword(encoder.encode("password"));
        credentials.setLogin(userDTO.getLogin());
        credentials.setEmail(userDTO.getEmail());
        credentials.setUser(user);

        Mockito.when(credentialRepository.findByUserId(Mockito.any(Long.class))).thenReturn(java.util.Optional.of(credentials));
        Mockito.when(credentialRepository.findById(Mockito.any(Long.class))).thenReturn(java.util.Optional.of(credentials));
        Mockito.when(credentialRepository.save(Mockito.any(Credentials.class))).thenReturn(credentials);
        Mockito.when(userRepository.getById(Mockito.any(Long.class))).thenReturn(user);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
    }

    @Test
    void test() throws Exception {
        userToEditDTO.setName("NEW NAME");
        mockMvc.perform(put("/api/v1/user")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.createToken("1"))
                .content(objectMapper.writeValueAsString(userToEditDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userToEditDTO)));

        userToEditDTO.setEmail("new email");
        mockMvc.perform(put("/api/v1/user")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.createToken("1"))
                .content(objectMapper.writeValueAsString(userToEditDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userToEditDTO)));

        userToEditDTO.setNewPassword("new password");
        userToEditDTO.setPassword("wrong password");
        mockMvc.perform(put("/api/v1/user")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.createToken("1"))
                .content(objectMapper.writeValueAsString(userToEditDTO)))
                .andExpect(status().isUnprocessableEntity());

        userToEditDTO.setNewPassword("new password");
        userToEditDTO.setPassword("password");
        UserDTO correct = new UserDTO();
        correct.setEmail(userToEditDTO.getEmail());
        correct.setName(userToEditDTO.getName());
        correct.setLogin(userToEditDTO.getLogin());
        correct.setId(userToEditDTO.getId());
        mockMvc.perform(put("/api/v1/user")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.createToken("1"))
                .content(objectMapper.writeValueAsString(userToEditDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(correct)));

        mockMvc.perform(delete("/api/v1/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.createToken("1")))
                .andExpect(status().isOk());
    }
}
