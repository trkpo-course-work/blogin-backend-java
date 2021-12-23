package com.trkpo.blogin.repository;

import com.trkpo.blogin.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> getAllByUserId(Long id);
    void deleteAllByUserId(Long id);
}
