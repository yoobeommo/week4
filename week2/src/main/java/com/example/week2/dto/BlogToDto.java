package com.example.week2.dto;

import com.example.week2.entity.Blog;
import com.example.week2.entity.Comment;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
public class BlogToDto {
    private Long id;
    private String title;
    private String username;
    private String contents;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private ArrayList<Comment> commentArrayList;

    public BlogToDto(Blog blog) {
        this.id = blog.getId();
        this.title = blog.getTitle();
        this.contents = blog.getContents();
        this.username = blog.getUsername();
        this.createdAt = blog.getCreatedAt();
        this.modifiedAt = blog.getModifiedAt();
    }

    public BlogToDto(Blog blog, ArrayList<Comment> commentArrayList) {
        this.id = blog.getId();
        this.title = blog.getTitle();
        this.contents = blog.getContents();
        this.username = blog.getUsername();
        this.createdAt = blog.getCreatedAt();
        this.modifiedAt = blog.getModifiedAt();
        this.commentArrayList = (ArrayList<Comment>)commentArrayList.clone();
    }
}
