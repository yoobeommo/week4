package com.example.week2.entity;

import com.example.week2.dto.BlogRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity(name="Blog")
@NoArgsConstructor
public class Blog extends Timestamped {
    @Id                                                         // primary_key
    @GeneratedValue(strategy = GenerationType.IDENTITY)         // id값은 생성시 자동으로 생성됨
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String contents;

//    @Column(nullable = false)
//    private String password;

    @Column(nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "blog",cascade = CascadeType.REMOVE)
    List<Comment> comment = new ArrayList<>();                          // blog가 삭제될 경우 comment도 삭제

    public Blog(BlogRequestDto requestDto, String username, Long userId) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
        this.username = username;
//        this.password = password;
        this.userId = userId;                               // User Column Id
    }

    public void update(BlogRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
    }
}