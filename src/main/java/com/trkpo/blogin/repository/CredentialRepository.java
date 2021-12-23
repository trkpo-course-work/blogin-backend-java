package com.trkpo.blogin.repository;

import com.trkpo.blogin.entity.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<Credentials, Long> {
    Credentials getByLogin(String login);
    Optional<Credentials> findByLogin(String login);
    Optional<Credentials> findByUserId(Long id);

    void deleteByUserId(Long id);
}
