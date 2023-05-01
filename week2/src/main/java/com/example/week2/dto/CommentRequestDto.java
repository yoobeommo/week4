package com.example.week2.dto;

import lombok.Getter;

@Getter
// Comment용 RequestDto
public class CommentRequestDto {
    private String id;
    private String username;
    private String contents;
}
