package com.trkpo.blogin.converter;

import com.trkpo.blogin.dto.UserDTO;
import com.trkpo.blogin.entity.Credentials;
import com.trkpo.blogin.entity.Picture;
import com.trkpo.blogin.entity.User;
import com.trkpo.blogin.repository.CredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {UserConverter.class})
public class UserConverterTest {
    @Autowired
    @InjectMocks
    private UserConverter userConverter;
    @MockBean
    private CredentialRepository credentialRepository;
    private User user = new User();
    private Credentials credentials = new Credentials();
    private UserDTO userDTO = new UserDTO();

    @BeforeEach
    void init() {
        Picture pic = new Picture();
        pic.setPath("/path");
        pic.setId(1L);

        user.setId(1L);
        user.setName("name");
        user.setPicture(pic);

        credentials.setEmail("email");
        credentials.setLogin("login");
        credentials.setUser(user);

        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setPictureId(pic.getId());
        userDTO.setLogin(credentials.getLogin());
        userDTO.setEmail(credentials.getEmail());

        Mockito.when(credentialRepository.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.ofNullable(credentials));
    }

    @Test
    void testConvertEntityToDTO() {
        UserDTO correct = userDTO;
        UserDTO result = userConverter.convertUserEntityToDTO(user);
        assertEquals(correct, result);
    }
}
