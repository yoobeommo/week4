package com.example.week2.service;

import com.example.week2.dto.*;
import com.example.week2.entity.*;
import com.example.week2.jwt.JwtUtil;
import com.example.week2.repository.BlogRepository;
import com.example.week2.repository.CommentRepository;
import com.example.week2.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;


    public BlogResponseDto createBlog(BlogRequestDto requestDto,  @AuthenticationPrincipal User user) {

        try{
            Blog blog = blogRepository.saveAndFlush(new Blog(requestDto, user.getUsername(), user.getId()));

            return new BlogResponseDto("success", HttpStatus.OK.value(), blog);
        } catch (Exception e) {
            throw new IllegalArgumentException("토큰이 존재하지 않습니다.");
        }
    }

    public BlogListResponseDto getBlogs() {

        BlogListResponseDto blogListResponseDto = new BlogListResponseDto("success", HttpStatus.OK.value());   // 조회한 엔티티 결과와 status, msg 같이 반환

        List<Blog> blogs = blogRepository.findAllByOrderByCreatedAtAsc();                                           // DB에서 전체 데이터를 조회해서하는데 생성시간 기준 역순으로 조회

        for (Blog blog : blogs) {                                                                                   // blog size 만큼 반복
            List<Comment> comments = commentRepository.findAllByBlog(blog);                                         // comment List에 DB에서 조회한 내용 저장

            if (comments.isEmpty()) {                                                                               // 만약 comments가 비어있다면 == comment의 blog_id와 일치하는 블로그 게시판 내용이 없으면
                blogListResponseDto.add(new BlogToDto(blog));                                                      // 블로그 게시판 내용만 blogList에 추가
            } else {                                                                                                 // 매핑되는 댓글이 있다면
                blogListResponseDto.add(new BlogToDto(blog, (ArrayList<Comment>) comments));                         // 게시판 내용 + 매핑되는 Comment 내용 추가
            }

        }
        return blogListResponseDto;                                                                              // List 조회 결과 반환
    }

    public BlogResponseDto getBlogsOne(Long id) {
        Blog blog = checkBlogs(blogRepository, id);                                                 //  checkBlog Function을 통해서 일치하는 ID 가 있는지 확인, 없으면 오류 반환
        List<Comment> comments = commentRepository.findAllByBlog(blog);                             // comment List에 DB에서 조회한 내용 저장
        if (comments.isEmpty()) {                                                                    // 만약 comments가 비어있다면 == comment의 blog_id와 일치하는 블로그 게시판 내용이 없으면
            return new BlogResponseDto("success", HttpStatus.OK.value(), blog);                // 선택된 블로그 게시판 내용만 반환
        } else {
            return new BlogResponseDto("fail", HttpStatus.BAD_REQUEST.value(), blog, (ArrayList<Comment>) comments);   // 게시판 내용 + 매핑되는 Comment 내용 반환
        }
    }
    public BlogResponseDto updateBlog(Long id, BlogRequestDto requestDto,  @AuthenticationPrincipal User user) {

        UserRoleEnum userRoleEnum = user.getRole();                     // 사용자의 User Type(User, Admin) 값 받아오기
        Blog blog;                                                      // Blog Entity 연결

        if (userRoleEnum == UserRoleEnum.USER) {                                // 사용자의 권한이 일반 사용자(User)일 경우
            blog = blogRepository.findById(id).orElseThrow(                         // 입력받은 게시판 Id를 DB에서 조회했을 때 일치하는 값이 없으면
                    () -> new NullPointerException("해당 게시글은 존재하지 않습니다.")      // nullPointException Throw
            );

            if (blog.getUserId().equals(user.getId())) {                    // blog에 저장된 User ID와 업데이트를 시도하는 User의 ID가 같을 경우
                blog.update(requestDto);                                // 업데이트 처리
            } else {
                throw new IllegalArgumentException("게시물 수정 권한이 없습니다.");      // blog에 저장된 User ID와 업데이트를 시도하는 User의 ID가 다를 경우 Excption Throw
            }
        } else {                                                                  // User 권한이 Admin일 경우 모든 내용 수정 가능
            blog = blogRepository.findById(id).orElseThrow(
                    () -> new NullPointerException("해당 게시글은 존재하지 않습니다.")
            );
            blog.update(requestDto);                                                                    // 업데이트 처리
        }
        Blog saveBlog = blogRepository.save(blog);                                                      // 업데이트한 내용 저장 (Transection 어노테이션을 제거하여 변경된 상태에서 커밋해주는 로직이 필요함)
        return new BlogResponseDto("success", HttpStatus.OK.value(), saveBlog);
    }
    // Delete DB Function
    public ResponseDto deleteBlog( Long id, @AuthenticationPrincipal User user) {
        UserRoleEnum userRoleEnum = user.getRole();
        Blog blog;

        if (userRoleEnum == UserRoleEnum.USER) {
            blog = blogRepository.findById(id).orElseThrow(
                    () -> new NullPointerException("해당 게시글은 존재하지 않습니다.")
            );

            if (blog.getUserId().equals(user.getId())) {
                blogRepository.deleteById(id);
            } else {
                throw new IllegalArgumentException("게시물 삭제 권한이 없습니다.");
            }
        } else {
            blog = blogRepository.findById(id).orElseThrow(
                    () -> new NullPointerException("해당 게시글은 존재하지 않습니다.")
            );
            blogRepository.deleteById(id);
        }

        return new ResponseDto("success", HttpStatus.OK.value());
    }


    private Blog checkBlogs(BlogRepository blogRepository, Long id) {
        return blogRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("일치하는 ID가 없습니다.")
                // 원래 NullPointerException을 사용했지만 IllegalArgumentException 이 상황에 더 알맞아서 변경.
        );
    }
}