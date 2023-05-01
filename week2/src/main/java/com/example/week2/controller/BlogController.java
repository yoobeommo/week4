package com.example.week2.controller;

import com.example.week2.Security.UserDetailsImpl;
import com.example.week2.dto.BlogListResponseDto;
import com.example.week2.dto.BlogRequestDto;
import com.example.week2.dto.BlogResponseDto;
import com.example.week2.dto.ResponseDto;
import com.example.week2.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/blog")
public class BlogController {

    private final BlogService blogService;

    @Autowired
    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PostMapping("/create")
    public BlogResponseDto createBlog(@RequestBody BlogRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            return blogService.createBlog(requestDto, userDetails.getUser());                                 // 예외가 발생할 부분을 try으로 묶어주고
        } catch (Exception e) {                                                                 // 예외가 발생하면 catch(Exception e)가 실행된다
            return new BlogResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value());         // 예외 발생시 에러내용, Httpstatus(400)을 리턴값으로 전달한다.
        }
    }

    @GetMapping("/list")
    public BlogListResponseDto getBlogs() {
        return blogService.getBlogs();
    }

    @GetMapping("/{id}")
    public BlogResponseDto getBlogsOne(@PathVariable Long id) {
        return blogService.getBlogsOne(id);
    }

    @PutMapping("/update/{id}")
    public BlogResponseDto updateBlog(@PathVariable Long id, @RequestBody BlogRequestDto requestDto,  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
        return blogService.updateBlog(id, requestDto, userDetails.getUser());
        } catch (Exception e) {
            return new  BlogResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value());        // 예외 발생시 에러내용, Httpstatus(400)을 리턴값으로 전달한다.
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseDto deleteBlog(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            return blogService.deleteBlog(id, userDetails.getUser());
        } catch (Exception e) {
            return new  ResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value());        // 예외 발생시 에러내용, Httpstatus(400)을 리턴값으로 전달한다.
        }
    }


}