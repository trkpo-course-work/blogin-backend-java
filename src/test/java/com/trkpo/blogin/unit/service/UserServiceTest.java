package com.trkpo.blogin.unit.service;

import com.trkpo.blogin.converter.UserConverter;
import com.trkpo.blogin.dto.UserDTO;
import com.trkpo.blogin.entity.Credentials;
import com.trkpo.blogin.entity.User;
import com.trkpo.blogin.exception.InvalidPasswordException;
import com.trkpo.blogin.repository.CredentialRepository;
import com.trkpo.blogin.repository.PictureRepository;
import com.trkpo.blogin.repository.PostRepository;
import com.trkpo.blogin.repository.UserRepository;
import com.trkpo.blogin.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {UserService.class})
public class UserServiceTest {
    @InjectMocks
    @Autowired
    UserService userService;
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
    private User user = new User();
    private UserDTO userDTO = new UserDTO();
    private UserDTO editedUserDTO = new UserDTO();
    private Credentials credentials = new Credentials();
    private Credentials favCredentials = new Credentials();
    private UserDTO favUserDTO = new UserDTO();
    private User favUser = new User();
    private List<User> favUsers = new ArrayList<>();
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void init() {
        user.setId(1L);
        user.setName("name");
        favUser.setId(2L);
        favUser.setName("eman");
        favUser.setFavourites(new ArrayList<>());
        favUserDTO.setId(favUser.getId());
        favUserDTO.setName(favUser.getName());
        favUsers.add(favUser);
        user.setFavourites(favUsers);

        credentials.setEmail("email");
        credentials.setLogin("login");
        credentials.setUser(user);
        credentials.setPassword("pswd");

        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setPictureId(1L);
        userDTO.setLogin(credentials.getLogin());
        userDTO.setEmail(credentials.getEmail());
        userDTO.setPassword(credentials.getPassword());

        editedUserDTO.setId(user.getId());
        editedUserDTO.setName("new name");
        editedUserDTO.setPictureId(userDTO.getPictureId());
        editedUserDTO.setLogin(credentials.getLogin());
        editedUserDTO.setEmail(credentials.getEmail());
        editedUserDTO.setPassword(credentials.getPassword());

        favCredentials.setEmail("liame");
        favCredentials.setLogin("nigol");
        favCredentials.setUser(favUser);
        favCredentials.setPassword(encoder.encode("pswd1"));

        favUserDTO.setLogin(favCredentials.getLogin());
        favUserDTO.setEmail(favCredentials.getEmail());

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(favUser.getId())).thenReturn(Optional.of(favUser));
        Mockito.when(userRepository.getById(user.getId())).thenReturn(user);
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user, favUser));
        Mockito.when(userConverter.convertUserEntityToDTO(user)).thenReturn(userDTO);
        Mockito.when(userConverter.convertUserEntityToDTO(favUser)).thenReturn(favUserDTO);
        Mockito.when(userRepository.getById(favUser.getId())).thenReturn(favUser);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(credentialRepository.findAll()).thenReturn(List.of(credentials, favCredentials));
        Mockito.when(credentialRepository.findByUserId(user.getId())).thenReturn(Optional.ofNullable(credentials));
        Mockito.when(credentialRepository.findByUserId(favUser.getId())).thenReturn(Optional.ofNullable(favCredentials));
        Mockito.when(credentialRepository.save(Mockito.any(Credentials.class))).thenReturn(null);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            return (User) args[0];
        });
    }

    @Test
    void testAddFavorites() {
        assertTrue(userService.addFavorites(2L, user));
    }

    @Test
    void testSearch() {
        List<UserDTO> correct = List.of(userDTO);
        List<UserDTO> createPostAnswer = userService.search(user.getName());
        assertEquals(correct, createPostAnswer);

        correct = List.of(favUserDTO);
        createPostAnswer = userService.search(favUser.getName());
        assertEquals(correct, createPostAnswer);

        correct = List.of(userDTO, favUserDTO);
        createPostAnswer = userService.search("a");
        assertEquals(correct, createPostAnswer);

        correct = Collections.emptyList();
        createPostAnswer = userService.search("nonexistent");
        assertEquals(correct, createPostAnswer);
    }

    @Test
    void testEditUser() throws InvalidPasswordException {
        UserDTO correct_answer = editedUserDTO;
        userDTO.setName(editedUserDTO.getName());
        UserDTO answer = userService.editUser(editedUserDTO, user);
        assertEquals(correct_answer, answer);

        editedUserDTO.setEmail("new email");
        correct_answer = editedUserDTO;
        userDTO.setEmail(editedUserDTO.getEmail());
        answer = userService.editUser(editedUserDTO, user);
        assertEquals(correct_answer, answer);

        editedUserDTO.setEmail("new email");
        correct_answer = editedUserDTO;
        userDTO.setEmail(editedUserDTO.getEmail());
        answer = userService.editUser(editedUserDTO, user);
        assertEquals(correct_answer, answer);

        editedUserDTO.setLogin("new login");
        correct_answer = editedUserDTO;
        userDTO.setLogin(editedUserDTO.getLogin());
        answer = userService.editUser(editedUserDTO, user);
        assertEquals(correct_answer, answer);

        editedUserDTO.setPictureId(2L);
        correct_answer = editedUserDTO;
        userDTO.setPictureId(editedUserDTO.getPictureId());
        answer = userService.editUser(editedUserDTO, user);
        assertEquals(correct_answer, answer);

        editedUserDTO.setNewPassword("new pswd"); //дто с измененным паролем
        editedUserDTO.setPassword(credentials.getPassword()); //верный старый пароль "pswd"
        correct_answer = editedUserDTO;
        userDTO.setPassword(editedUserDTO.getNewPassword()); //изменяем для выполнения проверки (userDTO должен быть равен editedUserDTO)
        credentials.setPassword(encoder.encode("pswd")); //изменяем пароль для выполнения проверки
        answer = userService.editUser(editedUserDTO, user); //процедура изменения пользователя
        assertEquals(correct_answer.getNewPassword(), answer.getPassword()); //проверка того, что пароль сменился

        editedUserDTO.setNewPassword("new pswd"); //дто с измененным паролем
        editedUserDTO.setPassword("wrong pswd"); //НЕВЕРНЫЙ старый пароль
        userDTO.setPassword(editedUserDTO.getNewPassword()); //изменяем для выполнения проверки (userDTO должен быть равен editedUserDTO)
        credentials.setPassword(encoder.encode("pswd")); //изменяем пароль для выполнения проверки
        assertThrows(InvalidPasswordException.class, () -> userService.editUser(editedUserDTO, user)); //проверка что вызывается исключение - старый пароль неверен
    }

    @Test
    void testIsFavourite() {
        assertTrue(userService.isFavorite(favUser.getId(), user));
        assertFalse(userService.isFavorite(user.getId(), favUser));
    }

    @Test
    void testDeleteFavourites() {
        assertTrue(userService.deleteFavorites(favUser.getId(), user));

        user.setFavourites(new ArrayList<>());
        assertFalse(userService.deleteFavorites(favUser.getId(), user));
    }
}
