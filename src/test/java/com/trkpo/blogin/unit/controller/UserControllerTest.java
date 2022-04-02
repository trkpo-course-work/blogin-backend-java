package com.trkpo.blogin.unit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trkpo.blogin.controller.UserController;
import com.trkpo.blogin.converter.PostConverter;
import com.trkpo.blogin.converter.UserConverter;
import com.trkpo.blogin.dto.PostDTO;
import com.trkpo.blogin.dto.UserDTO;
import com.trkpo.blogin.entity.Credentials;
import com.trkpo.blogin.entity.User;
import com.trkpo.blogin.exception.InvalidPasswordException;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {UserController.class})
public class UserControllerTest {
    @InjectMocks
    @Autowired
    UserController userController;
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

    private UserDTO userDTO = new UserDTO();
    private UserDTO editedUserDTO = new UserDTO();
    private UserDTO favUserDTO = new UserDTO();
    private User user = new User();
    private User favUser = new User();
    private PostDTO postDTO = new PostDTO();
    private Credentials credentials = new Credentials();
    private List<User> favouriteUsers = Collections.singletonList(favUser);
    private List<UserDTO> favouriteUsersDTO = Collections.singletonList(favUserDTO);

    @BeforeEach
    void init() throws InvalidPasswordException, JsonProcessingException {
        MockitoAnnotations.openMocks(this);
        user.setId(1L);
        user.setName("name");
        favUser.setId(2L);
        favUser.setName("fav name");
        favUserDTO.setId(favUser.getId());
        favUserDTO.setName(favUser.getName());
        user.setFavourites(favouriteUsers);

        credentials.setEmail("email");
        credentials.setLogin("login");
        credentials.setUser(user);
        credentials.setPassword("pswd");

        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setPictureId(null);
        userDTO.setLogin(credentials.getLogin());
        userDTO.setEmail(credentials.getEmail());
        userDTO.setPassword(credentials.getPassword());

        editedUserDTO.setId(user.getId());
        editedUserDTO.setName("new name");
        editedUserDTO.setPictureId(userDTO.getPictureId());
        editedUserDTO.setLogin(credentials.getLogin());
        editedUserDTO.setEmail(credentials.getEmail());
        editedUserDTO.setPassword(credentials.getPassword());

        postDTO.setId(1L);
        postDTO.setUserDTO(favUserDTO);
        postDTO.setUserId(favUserDTO.getId());
        postDTO.setText("text");
        postDTO.setPrivate(true);
        postDTO.setDateTime(123L);

        Mockito.when(userService.getUserFromContext()).thenReturn(user);
        Mockito.when(credentialRepository.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(credentials));
        Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user, favUser));
        Mockito.when(userRepository.getById(Mockito.any(Long.class))).thenReturn(user);
        Mockito.when(userConverter.convertUserEntityToDTO(user)).thenReturn(userDTO);
        Mockito.when(userConverter.convertUserEntityToDTO(favUser)).thenReturn(favUserDTO);
        Mockito.when(userService.editUser(editedUserDTO, user)).thenAnswer(invocation -> {
            user.setName(editedUserDTO.getName());
            userDTO.setName(editedUserDTO.getName());
            return userDTO;
        });
        Mockito.when(userService.addFavorites(favUser.getId(), user)).thenReturn(true);
        Mockito.when(userService.search(userDTO.getLogin())).thenReturn(Collections.singletonList(userDTO));
        Mockito.when(userService.isFavorite(favUser.getId(), user)).thenReturn(true);
        Mockito.when(postService.getPosts(user)).thenReturn(Collections.singletonList(postDTO));
        Mockito.when(postService.getNews(user)).thenReturn(Collections.singletonList(postDTO));
    }

    @Test
    void testEdit() throws InvalidPasswordException {
        ResponseEntity<?> correct = ResponseEntity.ok(editedUserDTO);
        ResponseEntity<?> answer = userController.edit(editedUserDTO);
        assertEquals(correct, answer);
    }

    @Test
    void testCurrentUser() {
        ResponseEntity<?> correct = ResponseEntity.ok(userDTO);
        ResponseEntity<?> answer = userController.getAuthorizedUserInfo();
        assertEquals(correct, answer);
    }

    @Test
    void getUserInfo() {
        ResponseEntity<?> correct = ResponseEntity.ok(userDTO);
        ResponseEntity<?> answer = userController.getUserInfo(user.getId());
        assertEquals(correct, answer);

        Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());
        correct = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        answer = userController.getUserInfo(user.getId());
        assertEquals(correct, answer);
    }

    @Test
    void testGetUserFavourites() {
        ResponseEntity<?> correct = ResponseEntity.ok(favouriteUsersDTO);
        ResponseEntity<?> answer = userController.getUserFavourites();
        assertEquals(correct, answer);
    }

    @Test
    void testAddUserFavourites() {
        ResponseEntity<?> correct = ResponseEntity.ok().build();
        ResponseEntity<?> answer = userController.addUserFavorites(favUser.getId());
        assertEquals(correct, answer);

        Mockito.when(userService.addFavorites(favUser.getId(), user)).thenReturn(false);
        correct = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        answer = userController.addUserFavorites(favUser.getId());
        assertEquals(correct, answer);
    }

    @Test
    void testGetAllUsers() {
        ResponseEntity<?> correct = ResponseEntity.ok(List.of(userDTO, favUserDTO));
        ResponseEntity<?> answer = userController.getAllUsers();
        assertEquals(correct, answer);
    }

    @Test
    void testSearchUser() {
        ResponseEntity<?> correct = ResponseEntity.ok(List.of(userDTO));
        ResponseEntity<?> answer = userController.searchUser(userDTO.getLogin());
        assertEquals(correct, answer);
    }

    @Test
    void testIsFavourite() {
        ResponseEntity<?> correct = ResponseEntity.ok(true);
        ResponseEntity<?> answer = userController.isFavorite(favUser.getId());
        assertEquals(correct, answer);
    }

    @Test
    void testGetNews() throws JsonProcessingException {
        ResponseEntity<?> correct = ResponseEntity.ok(Collections.singletonList(postDTO));
        ResponseEntity<?> answer = userController.getNews();
        assertEquals(correct, answer);

        Mockito.when(postService.getNews(user)).thenThrow(JsonProcessingException.class);
        correct = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        answer = userController.getNews();
        assertEquals(correct, answer);
    }

    @Test
    void testGetPosts() throws JsonProcessingException {
        ResponseEntity<?> correct = ResponseEntity.ok(Collections.singletonList(postDTO));
        ResponseEntity<?> answer = userController.getPosts();
        assertEquals(correct.getBody(), answer.getBody());

        Mockito.when(postService.getPosts(user)).thenThrow(JsonProcessingException.class);
        correct = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        answer = userController.getPosts();
        assertEquals(correct, answer);
    }

    @Test
    void testGetPostsByUserId() throws JsonProcessingException {
        ResponseEntity<?> correct = ResponseEntity.ok(Collections.singletonList(postDTO));
        ResponseEntity<?> answer = userController.getPostsByUserId(user.getId());
        assertEquals(correct.getBody(), answer.getBody());

        Mockito.when(postService.getPosts(user)).thenThrow(JsonProcessingException.class);
        correct = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        answer = userController.getPostsByUserId(user.getId());
        assertEquals(correct, answer);
    }

    @Test
    void testDeleteUser() {
        Mockito.doNothing().when(credentialRepository).deleteByUserId(user.getId());
        Mockito.doNothing().when(postRepository).deleteAllByUserId(user.getId());
        Mockito.doNothing().when(userRepository).deleteById(user.getId());

        ResponseEntity<?> correct = ResponseEntity.ok().build();
        ResponseEntity<?> answer = userController.deleteUser();
        assertEquals(correct, answer);
    }

    @Test
    void testDeleteUserFavourites() {
        Mockito.when(userService.deleteFavorites(favUser.getId(), user)).thenReturn(true);
        ResponseEntity<?> correct = ResponseEntity.ok().build();
        ResponseEntity<?> answer = userController.deleteUserFavorites(favUser.getId());
        assertEquals(correct, answer);

        Mockito.when(userService.deleteFavorites(favUser.getId(), user)).thenReturn(false);
        correct = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        answer = userController.deleteUserFavorites(favUser.getId());
        assertEquals(correct, answer);
    }
}
