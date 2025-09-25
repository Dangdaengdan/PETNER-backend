package com.example.petner.domain.dog.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 유기견 삭제 성공 응답을 위한 DTO 클래스
 *
 * 서버에서 클라이언트로 유기견 삭제 성공 메시지를 전달할 때 사용하는 데이터 전송 객체입니다.
 * 유기견 삭제 API의 응답으로 사용됩니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DogDeleteResponseDto {

    /**
     * 삭제된 유기견 고유 ID
     */
    private Long dogId;

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
     * 유기견 삭제 성공 응답 DTO 생성자
     *
     * @param dogId 삭제된 유기견 고유 ID
     * @param memberId 삭제를 수행한 멤버 고유 ID
     * @param message 성공 메시지
     */
    public DogDeleteResponseDto(Long dogId, Long memberId, String message) {
        this.dogId = dogId;
        this.memberId = memberId;
        this.message = message;
        this.success = true;
    }
}