package com.trkpo.blogin.service;

import com.trkpo.blogin.converter.UserConverter;
import com.trkpo.blogin.dto.UserDTO;
import com.trkpo.blogin.entity.Credentials;
import com.trkpo.blogin.entity.Picture;
import com.trkpo.blogin.entity.User;
import com.trkpo.blogin.exception.InvalidPasswordException;
import com.trkpo.blogin.repository.CredentialRepository;
import com.trkpo.blogin.repository.PictureRepository;
import com.trkpo.blogin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CredentialRepository credentialRepository;
    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private UserConverter userConverter;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public boolean addFavorites(Long id, User user) {
        Optional<User> favouriteUser = userRepository.findById(id);
        if (favouriteUser.isEmpty()) return false;
        user.getFavourites().add(favouriteUser.get());
        userRepository.save(user);
        return true;
    }

    public List<UserDTO> search(String info) {
        List<UserDTO> result = new ArrayList<>();
        List<User> allUsers = userRepository.findAll();
        List<Credentials> allCredentials = credentialRepository.findAll();

        result.addAll(allUsers.stream()
                .filter(user -> ((user.getName().equals(info)) || (user.getName().contains(info)) || (info.contains(user.getName()))))
                .map(userEntity -> userConverter.convertUserEntityToDTO(userEntity))
                .collect(Collectors.toList()));

        result.addAll(allCredentials.stream()
                .filter(credentials -> (credentials.getLogin().equals(info) || (credentials.getLogin().contains(info)) || info.contains(credentials.getLogin())))
                .map(credentials -> userRepository.getById(credentials.getUser().getId()))
                .map(userEntity -> userConverter.convertUserEntityToDTO(userEntity))
                .collect(Collectors.toList()));

        return result.stream().filter(distinctByKey(UserDTO::getLogin)).collect(Collectors.toList());
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public UserDTO editUser(UserDTO userDTO, User userToEdit) throws InvalidPasswordException {
        Optional<Credentials> principalCredentials = credentialRepository.findByUserId(userToEdit.getId());
        if (userDTO.getName() != null) userToEdit.setName(userDTO.getName());
        if (principalCredentials.isPresent()) {
            if (userDTO.getLogin() != null) {
                principalCredentials.get().setLogin(userDTO.getLogin());
            }
            if (userDTO.getNewPassword() != null) {
                if (encoder.matches(userDTO.getPassword(), principalCredentials.get().getPassword())) {
                    principalCredentials.get().setPassword(encoder.encode(userDTO.getNewPassword()));
                } else {
                    throw new InvalidPasswordException("Password doesnt match hashed value");
                }
            }
            if (userDTO.getEmail() != null) {
                principalCredentials.get().setEmail(userDTO.getEmail());
            }
            credentialRepository.save(principalCredentials.get());
        }
        if (userDTO.getPictureId() != null) {
            Optional<Picture> picture = pictureRepository.findById(userDTO.getPictureId());
            picture.ifPresent(userToEdit::setPicture);
        }
        userRepository.save(userToEdit);
        return userConverter.convertUserEntityToDTO(userToEdit);
    }

    public boolean isFavorite(Long id, User currentUser) {
        User user = userRepository.getById(id);
        return currentUser.getFavourites().contains(user);
    }

    public boolean deleteFavorites(Long id, User user) {
        Optional<User> favouriteUser = userRepository.findById(id);
        if (favouriteUser.isEmpty()) return false;
        if (!user.getFavourites().contains(favouriteUser.get())) {
            return false;
        }
        user.getFavourites().remove(favouriteUser.get());
        userRepository.save(user);
        return true;
    }
}
