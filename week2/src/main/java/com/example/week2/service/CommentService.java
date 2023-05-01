package com.example.week2.service;

import com.example.week2.dto.CommentRequestDto;
import com.example.week2.dto.CommentResponseDto;
import com.example.week2.dto.ResponseDto;
import com.example.week2.entity.Blog;
import com.example.week2.entity.Comment;
import com.example.week2.entity.User;
import com.example.week2.entity.UserRoleEnum;
import com.example.week2.jwt.JwtUtil;
import com.example.week2.repository.BlogRepository;
import com.example.week2.repository.CommentRepository;
import com.example.week2.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // Blog Comment Create Function
    @Transactional
    public CommentResponseDto createComment(Long id, CommentRequestDto requestDto, HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);                      // Request(Token) -> BlogService
        Claims claims;

        if (token != null) {                                               // 토큰이 있는 경우에만 댓글 작성 가능
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token이 유효하지않습니다.");
            }

            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            Blog blog = blogRepository.findById(id).orElseThrow(
                    () -> new NullPointerException("해당 게시글은 존재하지 않습니다.")
            );

            Comment comment = commentRepository.saveAndFlush(new Comment(requestDto, blog));    // Db에 입력받은 Comment내용과, Join되어있는 blog 데이터 저장

            return new CommentResponseDto("success", HttpStatus.OK.value(), comment);       // Db 저장 성공 시 Success Code 리턴
        } else {
            throw new IllegalArgumentException("토큰이 존재하지 않습니다.");
        }
    }

    public CommentResponseDto update(Long id, CommentRequestDto requestDto, HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);           // Request(Token) -> BlogService
        Claims claims;

        if (token != null) {                                      // 토큰이 있는 경우에만 댓글 작성 가능
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);

                User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                        () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
                );

                UserRoleEnum userRoleEnum = user.getRole();
                Comment comment;

                if (userRoleEnum == UserRoleEnum.USER) {
                    comment = commentRepository.findById(id).orElseThrow(
                            () -> new NullPointerException("해당 댓글은 존재하지 않습니다.")
                    );

                    String loginUserName = user.getUsername();
                    if (!comment.getUsername().equals(loginUserName)) {
                        throw new IllegalArgumentException("회원님의 댓글이 아니므로 업데이트 할 수 없습니다.");
                    }
                } else {
                    comment = commentRepository.findById(id).orElseThrow(
                            () -> new NullPointerException("해당 댓글은 존재하지 않습니다.")
                    );
                    comment.update(requestDto);                                             // 입력받은 내용 Update 처리
                }
                Comment saveComment = commentRepository.save(comment);                      // 업데이트한 내용 저장 (Transection 어노테이션을 제거하여 변경된 상태에서 커밋해주는 로직이 필요함)
                return new CommentResponseDto("sucess", HttpStatus.OK.value(), saveComment);
            }
        } else {
            throw new IllegalArgumentException("토큰이 존재하지 않습니다.");
        }
        return null;
    }

    public ResponseDto deleteComment(Long id, HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 토큰이 있는 경우에만 관심상품 추가 가능
        if (token != null) {
            if (jwtUtil.validateToken(token)) {                                         // Request(Token) -> BlogService
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }
            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            UserRoleEnum userRoleEnum = user.getRole();
            Comment comment;

            if (userRoleEnum == UserRoleEnum.USER) {
                comment = commentRepository.findById(id).orElseThrow(
                        () -> new NullPointerException("해당 댓글은 존재하지 않습니다.")
                );

                String loginUserName = user.getUsername();
                if (!comment.getUsername().equals(loginUserName)) {
                    throw new IllegalArgumentException("회원님의 댓글이 아니므로 삭제할 수 없습니다.");
                }
            } else {
                comment = commentRepository.findById(id).orElseThrow(
                        () -> new NullPointerException("해당 댓글은 존재하지 않습니다.")
                );
                commentRepository.deleteById(id);                                          // 입력받은 게시판 id 삭제 처리
            }
            return new ResponseDto("sucess", HttpStatus.OK.value());
        } else {
            throw new IllegalArgumentException("토큰이 존재하지 않습니다.");
        }
    }

}