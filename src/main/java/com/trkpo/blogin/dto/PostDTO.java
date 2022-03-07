package com.trkpo.blogin.dto;

import java.util.List;
import java.util.Objects;

public class PostDTO {
    private Long id;
    private UserDTO userDTO;
    private Long userId;
    private String text;
    private List<SpanDTO> span;
    private boolean isPrivate;
    private Long dateTime;
    private Long pictureId;

    public UserDTO getUserDTO() {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "PostDTO{" +
                "id=" + id +
                ", userDTO=" + userDTO +
                ", userId=" + userId +
                ", text='" + text + '\'' +
                ", span=" + span +
                ", isPrivate=" + isPrivate +
                ", dateTime=" + dateTime +
                ", pictureId=" + pictureId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostDTO)) return false;
        PostDTO postDTO = (PostDTO) o;
        return isPrivate() == postDTO.isPrivate() && Objects.equals(getId(), postDTO.getId()) && Objects.equals(getUserDTO(), postDTO.getUserDTO()) && Objects.equals(getUserId(), postDTO.getUserId()) && Objects.equals(getText(), postDTO.getText()) && Objects.equals(getSpan(), postDTO.getSpan()) && Objects.equals(getDateTime(), postDTO.getDateTime()) && Objects.equals(getPictureId(), postDTO.getPictureId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUserDTO(), getUserId(), getText(), getSpan(), isPrivate(), getDateTime(), getPictureId());
    }
}
