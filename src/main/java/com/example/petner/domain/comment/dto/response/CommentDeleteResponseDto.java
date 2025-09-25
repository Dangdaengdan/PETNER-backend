package com.example.petner.domain.comment.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글 삭제 성공 응답을 위한 DTO 클래스
 *
 * 서버에서 클라이언트로 댓글 삭제 성공 메시지를 전달할 때 사용하는 데이터 전송 객체입니다.
 * 댓글 삭제 API의 응답으로 사용됩니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentDeleteResponseDto {

    /**
     * 삭제된 댓글 고유 ID
     */
    private Long commentId;

    /**
     * 삭제를 수행한 멤버 고유 ID
     */
    private Long memberId;

    /**
     * 성공 메시지
     */
    private String message;

    /**
     * 삭제 성공 상태
     */
    private boolean success;

    /**
     * 댓글 삭제 성공 응답 DTO 생성자
     *
     * @param commentId 삭제된 댓글 고유 ID
     * @param memberId 삭제를 수행한 멤버 고유 ID
     * @param message 성공 메시지
     */
    public CommentDeleteResponseDto(Long commentId, Long memberId, String message) {
        this.commentId = commentId;
        this.memberId = memberId;
        this.message = message;
        this.success = true;
    }

    /**
     * 댓글 삭제 성공 응답 생성
     *
     * @param commentId 삭제된 댓글 고유 ID
     * @param memberId 삭제를 수행한 멤버 고유 ID
     * @return CommentDeleteResponseDto
     */
    public static CommentDeleteResponseDto success(Long commentId, Long memberId) {
        return new CommentDeleteResponseDto(commentId, memberId, "댓글이 성공적으로 삭제되었습니다.");
    }
}