package com.example.petner.domain.post.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 삭제 성공 응답을 위한 DTO 클래스
 *
 * 서버에서 클라이언트로 게시글 삭제 성공 메시지를 전달할 때 사용하는 데이터 전송 객체입니다.
 * 게시글 삭제 API의 응답으로 사용됩니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDeleteResponseDto {
    private Long postId;
    private Long memberId;
    private String message;
    private boolean success;

    public PostDeleteResponseDto(Long postId, Long memberId, String message) {
        this.postId = postId;
        this.memberId = memberId;
        this.message = message;
        this.success = true;
    }

    public static PostDeleteResponseDto success(Long postId, Long memberId) {
        return new PostDeleteResponseDto(postId, memberId, "게시글이 성공적으로 삭제되었습니다.");
    }
}