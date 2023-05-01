package com.example.week2.controller;

import com.example.week2.dto.CommentRequestDto;
import com.example.week2.dto.CommentResponseDto;
import com.example.week2.dto.ResponseDto;
import com.example.week2.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;

    // insert Comment
    @PostMapping("/{id}")
    public CommentResponseDto createComment(@PathVariable Long id, @RequestBody CommentRequestDto requestDto, HttpServletRequest request) {
        try {
            return commentService.createComment(id, requestDto, request);
        } catch (Exception e) {
            return new CommentResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value());             // 예외 발생시 에러 내용, Httpstatus(400)을 리턴값으로 전달한다.
        }
    }

    // Comment Update
    @PutMapping("/{id}")
    public CommentResponseDto updateComment(@PathVariable Long id, @RequestBody CommentRequestDto reqeustDto, HttpServletRequest request) {
        try {
            return commentService.update(id, reqeustDto, request);
        } catch (Exception e) {
            return new CommentResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value());           // 예외 발생시 에러 내용, Httpstatus(400)을 리턴값으로 전달한다.
        }
    }

    // Comment Delete
    @DeleteMapping("/{id}")
    public ResponseDto deleteComment(@PathVariable Long id, HttpServletRequest request) {
        try {
            return commentService.deleteComment(id, request);
        } catch (Exception e) {
            return new CommentResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value());            // 예외 발생시 에러 내용, Httpstatus(400)을 리턴값으로 전달한다.
        }
    }
}
