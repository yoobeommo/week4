package com.example.week2.entity;

import com.example.week2.dto.CommentRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Comment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private String username;

    @ManyToOne
    @JoinColumn(name = "BLOG_ID", nullable = false)
    @JsonIgnore                                           // 게시물 조회시 댓글에 blog 컬럼 내용이 보이지않게 해당 데이터는 Ignore 되도록 처리함
    private Blog blog;


    public Comment(CommentRequestDto requestDto, Blog blog) {
        this.contents = requestDto.getContents();
        this.username = requestDto.getUsername();
        this.blog = blog;                                 // blog 컬럼 데이터
    }

    public void update(CommentRequestDto requestDto) {
        this.contents = requestDto.getContents();         // 사용자가 입력한 댓글 내용
        this.username = requestDto.getUsername();         // 사용자 ID
    }
}
