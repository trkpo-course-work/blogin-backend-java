package com.trkpo.blogin.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trkpo.blogin.dto.PostDTO;
import com.trkpo.blogin.dto.SpanDTO;
import com.trkpo.blogin.entity.Picture;
import com.trkpo.blogin.entity.Post;
import com.trkpo.blogin.entity.User;
import com.trkpo.blogin.repository.PictureRepository;
import com.trkpo.blogin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PostConverter {
    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserConverter userConverter;
    private ObjectMapper objectMapper = new ObjectMapper();

    public Post convertPostDTOtoEntity(PostDTO post, User user) throws JsonProcessingException {
        Post newPost = new Post();
        if (post.getPictureId() != null) {
            Optional<Picture> pic = pictureRepository.findById(post.getPictureId());
            pic.ifPresent(newPost::setPicture);
        }
        newPost.setUser(user);
        newPost.setDateTime(post.getDateTime());
        newPost.setPrivate(post.isPrivate());
        newPost.setText(post.getText());
        newPost.setSpan(objectMapper.writeValueAsString(post.getSpan()));
        return newPost;
    }

    public PostDTO convertPostEntityToDTO(Post post) throws JsonProcessingException {
        PostDTO postDTO = new PostDTO();
        User user = post.getUser();
        postDTO.setId(post.getId());
        postDTO.setUserDTO(userConverter.convertUserEntityToDTO(user));
        postDTO.setDateTime(post.getDateTime());
        postDTO.setPrivate(post.isPrivate());
        JsonNode jsonSpan = objectMapper.readTree(post.getSpan());
        List<SpanDTO> spanDTOList = new ArrayList<>();
        for (int i = 0; i < jsonSpan.size(); i++) {
            spanDTOList.add(objectMapper.treeToValue(jsonSpan.get(i), SpanDTO.class));
        }
        postDTO.setSpan(spanDTOList);
        postDTO.setUserId(post.getUser().getId());
        postDTO.setText(post.getText());
        if (post.getPicture() != null) {
            postDTO.setPictureId(post.getPicture().getId());
        }
        return postDTO;
    }
}
