package com.trkpo.blogin.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "Post")
@Table(name = "Posts")
public class Post {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private com.trkpo.blogin.entity.User user;
    private String text;
    @OneToOne
    private com.trkpo.blogin.entity.Picture picture;
    private Long dateTime;
    private boolean isPrivate;
    private String span;

    public Post() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public com.trkpo.blogin.entity.User getUser() {
        return user;
    }

    public void setUser(com.trkpo.blogin.entity.User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public com.trkpo.blogin.entity.Picture getPicture() {
        return picture;
    }

    public void setPicture(com.trkpo.blogin.entity.Picture picture) {
        this.picture = picture;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getSpan() {
        return span;
    }

    public void setSpan(String span) {
        this.span = span;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", user=" + user +
                ", text='" + text + '\'' +
                ", picture=" + picture +
                ", dateTime=" + dateTime +
                ", isPrivate=" + isPrivate +
                ", span='" + span + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        return isPrivate() == post.isPrivate() && Objects.equals(getId(), post.getId()) && Objects.equals(getUser(), post.getUser()) && Objects.equals(getText(), post.getText()) && Objects.equals(getPicture(), post.getPicture()) && Objects.equals(getDateTime(), post.getDateTime()) && Objects.equals(getSpan(), post.getSpan());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUser(), getText(), getPicture(), getDateTime(), isPrivate(), getSpan());
    }
}
