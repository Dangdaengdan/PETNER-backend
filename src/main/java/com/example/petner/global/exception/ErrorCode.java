package com.example.petner.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* 인증/세션 관련 에러 */
    SESSION_EXPIRED("401-AU01", "세션이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED_ACCESS("401-AU02", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    KAKAO_AUTH_FAILED("401-AU03", "카카오 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    KAKAO_API_ERROR("500-AU04", "카카오 API 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    SESSION_DATA_CORRUPTED("500-AU05", "세션 데이터가 손상되었습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    SESSION_INVALID_DATA("400-AU06", "세션 데이터 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    /* 서버 에러 */
    GLOBAL_ERROR("500-GL01", "서버 오류", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR("500-DB01","데이터 베이스 오류", HttpStatus.INTERNAL_SERVER_ERROR),

    /* 멤버 관련 */
    MEMBER_NOT_FOUND("404-MB01", "사용자 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    MEMBER_ALREADY_EXISTS("409-MB02", "이미 존재하는 사용자입니다", HttpStatus.CONFLICT),
    MEMBER_EMAIL_DUPLICATE("409-MB03", "이미 사용 중인 이메일입니다", HttpStatus.CONFLICT),
    MEMBER_NICKNAME_DUPLICATE("409-MB04", "이미 사용 중인 닉네임입니다", HttpStatus.CONFLICT),
    MEMBER_PROFILE_INCOMPLETE("400-MB05", "프로필 정보가 완성되지 않았습니다", HttpStatus.BAD_REQUEST),
    MEMBER_CREATION_FAILED("500-MB06", "사용자 생성 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),

    /* 강아지 관련 */
    DOG_NOT_FOUND("404-DG01", "강아지 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    DOG_ALREADY_ADOPTED("409-DG02", "이미 입양된 강아지입니다", HttpStatus.CONFLICT),
    DOG_NOT_AVAILABLE("400-DG03", "입양 불가능한 강아지입니다", HttpStatus.BAD_REQUEST),
    DOG_ACCESS_DENIED("403-DG04", "강아지 정보에 대한 접근 권한이 없습니다", HttpStatus.FORBIDDEN),
    DOG_BREED_NOT_FOUND("404-DG05", "견종 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    DOG_SHELTER_NOT_FOUND("404-DG06", "보호소 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    DOG_ALREADY_DELETED("410-DG07", "이미 삭제된 강아지입니다", HttpStatus.GONE),

    /* 유기견 분양 신청 관련 */
    DOG_APPLY_NOT_FOUND("404-DA01", "분양 신청을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    DOG_APPLY_ALREADY_EXISTS("409-DA02", "이미 신청한 유기견입니다", HttpStatus.CONFLICT),
    DOG_APPLY_SELF_APPLICATION("400-DA03", "자신의 유기견에는 신청할 수 없습니다", HttpStatus.BAD_REQUEST),
    DOG_APPLY_NOT_AVAILABLE("400-DA04", "분양 신청이 불가능한 유기견입니다", HttpStatus.BAD_REQUEST),
    DOG_APPLY_ACCESS_DENIED("403-DA05", "분양 신청에 대한 접근 권한이 없습니다", HttpStatus.FORBIDDEN),
    DOG_APPLY_ALREADY_PROCESSED("409-DA06", "이미 처리된 분양 신청입니다", HttpStatus.CONFLICT),
    DOG_APPLY_INVALID_STATUS("400-DA07", "유효하지 않은 처리 상태입니다", HttpStatus.BAD_REQUEST),

    /* 게시글 관련 */
    POST_NOT_FOUND("404-PT01", "게시글을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    POST_ACCESS_DENIED("403-PT02", "게시글에 대한 접근 권한이 없습니다", HttpStatus.FORBIDDEN),

    /* 댓글 관련 */
    COMMENT_NOT_FOUND("404-CM01", "댓글을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    COMMENT_ACCESS_DENIED("403-CM02", "댓글에 대한 접근 권한이 없습니다", HttpStatus.FORBIDDEN),
    COMMENT_POST_MISMATCH("400-CM03", "댓글과 게시글이 일치하지 않습니다", HttpStatus.BAD_REQUEST),
    COMMENT_MAX_DEPTH_EXCEEDED("400-CM04", "댓글은 2단계까지만 작성 가능합니다", HttpStatus.BAD_REQUEST),
    COMMENT_INVALID_REQUEST("400-CM05", "잘못된 댓글 요청입니다", HttpStatus.BAD_REQUEST),

    /* 보호소 관련 */
    SHELTER_NOT_FOUND("404-SH01", "보호소 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    SHELTER_ACCESS_DENIED("403-SH02", "보호소에 대한 접근 권한이 없습니다", HttpStatus.FORBIDDEN),

    /* 즐겨찾기 관련 */
    FAVORITE_NOT_FOUND("404-FV01", "즐겨찾기를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    FAVORITE_ALREADY_EXISTS("409-FV02", "이미 즐겨찾기에 추가된 항목입니다", HttpStatus.CONFLICT),
    FAVORITE_NOT_IN_MY_LIST("400-FV03", "내 즐겨찾기 목록에 없는 강아지입니다", HttpStatus.BAD_REQUEST),

    /* 위치 관련 */
    LOCATION_NOT_FOUND("404-LC01", "위치 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    LOCATION_INVALID("400-LC02", "유효하지 않은 위치 정보입니다", HttpStatus.BAD_REQUEST),

    /* 채팅 관련 */
    CHAT_ROOM_NOT_FOUND("404-CH01", "채팅방을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CHAT_MEMBER_NOT_FOUND("404-CH02", "사용자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CHAT_UNAUTHORIZED_ACCESS("403-CH03", "채팅방에 대한 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    CHAT_ROOM_ACCESS_DENIED("403-CH04", "채팅방에 대한 접근이 거부되었습니다.", HttpStatus.FORBIDDEN),
    CHAT_DOG_OWNER_MISMATCH("400-CH05", "강아지 소유자가 채팅방 참여자와 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    CHAT_INVALID_MESSAGE_TYPE("400-CH06", "유효하지 않은 메시지 타입입니다.", HttpStatus.BAD_REQUEST),
    CHAT_INVALID_PAYLOAD("400-CH07", "메시지 페이로드가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    CHAT_ALREADY_ADOPTED("409-CH08", "이미 입양된 강아지입니다", HttpStatus.CONFLICT),
    CHAT_INVALID_REQUEST("400-CH09", "잘못된 채팅 요청입니다.", HttpStatus.BAD_REQUEST),
    CHAT_DOG_NOT_FOUND("404-CH10", "강아지 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CHAT_INVALID_SAME_MEMBER("400-CH11", "동일한 사용자끼리는 채팅을 할 수 없습니다.", HttpStatus.BAD_REQUEST),

    /* 업로드 관련 */
    UPLOAD_INVALID_FILE_TYPE("400-UP01", "지원하지 않는 파일 형식입니다", HttpStatus.BAD_REQUEST),
    UPLOAD_FILE_SIZE_EXCEEDED("400-UP02", "파일 크기가 허용 범위를 초과했습니다", HttpStatus.BAD_REQUEST),
    UPLOAD_PRESIGNED_URL_GENERATION_FAILED("500-UP03", "업로드 URL 생성에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    UPLOAD_OBJECT_NOT_FOUND("404-UP04", "요청한 파일을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    UPLOAD_DOWNLOAD_URL_GENERATION_FAILED("500-UP05", "다운로드 URL 생성에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    UPLOAD_DELETE_FAILED("500-UP06", "파일 삭제에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    UPLOAD_INVALID_URL_FORMAT("400-UP07", "잘못된 URL 형식입니다", HttpStatus.BAD_REQUEST);

    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;

}
