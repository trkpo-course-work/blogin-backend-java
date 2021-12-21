package com.trkpo.blogin.dto;

import java.util.List;

public class PostDTO {
    private UserDTO userDTO;
    private Long userId;
    private String text;
    private List<SpanDTO> span;
    private boolean isPrivate;
    private Long dateTime;
    private Long pictureId;

    public com.trkpo.blogin.dto.UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public Long getPictureId() {
        return pictureId;
    }

    public void setPictureId(Long pictureId) {
        this.pictureId = pictureId;
    }

    public List<SpanDTO> getSpan() {
        return span;
    }

    public void setSpan(List<SpanDTO> span) {
        this.span = span;
    }
}
