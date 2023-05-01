package com.example.week2.repository;

import com.example.week2.entity.Blog;
import com.example.week2.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByBlog(Blog blog);
    List<Comment> findAllByBlogOrderByCreatedAtDesc(Long id);
}