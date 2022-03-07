package com.trkpo.blogin.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity(name = "Pictures")
public class Picture {
    @Id
    @GeneratedValue
    private Long id;
    private String path;

    public Picture() {
    }

    public Picture(Long id, String path) {
        this.id = id;
        this.path = path;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Picture)) return false;
        Picture picture = (Picture) o;
        return Objects.equals(getId(), picture.getId()) && Objects.equals(getPath(), picture.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPath());
    }
}
