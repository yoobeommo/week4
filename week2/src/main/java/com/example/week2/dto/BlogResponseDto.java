package com.example.week2.dto;

import com.example.week2.entity.Blog;
import com.example.week2.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;

@Getter
@NoArgsConstructor
@Configuration
public class BlogResponseDto extends ResponseDto {  // 응답용 Dto 생성
    BlogToDto blogContents;                         // BlogToDto 연결

    // 생성자(mgs+Statuscode)
    public BlogResponseDto(String msg, int statusCode) {
        super(msg, statusCode);
    }

    // 생성자(mgs+Statuscode+Entity)
    public BlogResponseDto(String msg, int statusCode, Blog blog) {
        super(msg, statusCode);
        this.blogContents = new BlogToDto(blog);
    }

    // 생성자(mgs+Statuscode+comment Entity)
    public BlogResponseDto(String msg, int statusCode, Blog blog, ArrayList<Comment> commentArrayList) {
        super(msg, statusCode);
        this.blogContents = new BlogToDto(blog, commentArrayList);
    }

}