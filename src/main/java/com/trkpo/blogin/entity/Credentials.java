package com.trkpo.blogin.entity;

import javax.persistence.*;

@Entity(name = "Credentials")
public class Credentials {
    @Id
    @GeneratedValue
    private Long id;
    private String login;
    @Column(name = "password_hash")
    private String password;
    @OneToOne
    @JoinColumn(name = "user_id")
    private com.trkpo.blogin.entity.User user;
    private String email;

    public Credentials(String login, String password, String email) {
        this.login = login;
        this.password = password;
        this.email = email;
    }

    public Credentials() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public com.trkpo.blogin.entity.User getUser() {
        return user;
    }

    public void setUser(com.trkpo.blogin.entity.User user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
