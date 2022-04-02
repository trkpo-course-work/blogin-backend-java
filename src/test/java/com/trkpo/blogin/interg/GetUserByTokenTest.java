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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class GetUserByTokenTest {
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
    private User user;
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

        Credentials credentials = new Credentials();
        credentials.setPassword("password");
        credentials.setLogin(userDTO.getLogin());
        credentials.setEmail(userDTO.getEmail());
        credentials.setUser(user);

        Mockito.when(credentialRepository.findByUserId(Mockito.any(Long.class))).thenReturn(java.util.Optional.of(credentials));
        Mockito.doNothing().when(postRepository).deleteById(Mockito.any(Long.class));
        Mockito.when(credentialRepository.findById(Mockito.any(Long.class))).thenReturn(java.util.Optional.of(credentials));
        Mockito.when(userRepository.getById(Mockito.any(Long.class))).thenReturn(user);
    }

    @Test
    void testToken() throws Exception {
        mockMvc.perform(get("/api/v1/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.createToken("1")))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDTO)));

        verify(userRepository, times(1)).getById(Mockito.any(Long.class));
        verify(credentialRepository, times(2)).findById(Mockito.any(Long.class));

        mockMvc.perform(get("/api/v1/user")
                .header(HttpHeaders.AUTHORIZATION, "NOT VALID TOKEN"))
                .andExpect(status().isForbidden());
    }
}
