package com.example.petner.domain.dogApply.repository;

import com.example.petner.domain.dogApply.common.ApplyStatus;
import com.example.petner.domain.dogApply.entity.DogApply;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 유기견 분양 신청 Repository
 * N+1 문제 방지를 위한 페치 조인 활용
 */
@Repository
public interface DogApplyRepository extends JpaRepository<DogApply, Long> {

    /**
     * 특정 유기견과 신청자로 신청 내역 조회
     * @param dogId 유기견 ID
     * @param applicantId 신청자 ID
     * @return 신청 내역
     */
    @Query("SELECT da FROM DogApply da " +
           "WHERE da.dog.dogId = :dogId " +
           "AND da.applicant.memberId = :applicantId")
    Optional<DogApply> findByDogIdAndApplicantId(@Param("dogId") Long dogId,
                                                 @Param("applicantId") Long applicantId);

    /**
     * 특정 신청자의 모든 신청 내역 조회 (페이징, N+1 방지)
     * @param applicantId 신청자 ID
     * @param pageable 페이징 정보
     * @return 신청 내역 목록
     */
    @Query("SELECT da FROM DogApply da " +
           "JOIN FETCH da.dog d " +
           "JOIN FETCH d.breed " +
           "JOIN FETCH d.member " +
           "LEFT JOIN FETCH d.shelter s " +
           "LEFT JOIN FETCH s.location " +
           "JOIN FETCH da.applicant " +
           "WHERE da.applicant.memberId = :applicantId " +
           "ORDER BY da.createdAt DESC")
    List<DogApply> findByApplicantIdWithDetails(@Param("applicantId") Long applicantId,
                                                Pageable pageable);

    /**
     * 특정 유기견 등록자에게 온 모든 신청 내역 조회 (페이징, N+1 방지)
     * @param dogOwnerId 유기견 등록자 ID
     * @param pageable 페이징 정보
     * @return 신청 내역 목록
     */
    @Query("SELECT da FROM DogApply da " +
           "JOIN FETCH da.dog d " +
           "JOIN FETCH d.breed " +
           "JOIN FETCH d.member " +
           "LEFT JOIN FETCH d.shelter s " +
           "LEFT JOIN FETCH s.location " +
           "JOIN FETCH da.applicant " +
           "WHERE d.member.memberId = :dogOwnerId " +
           "AND d.deleted = false " +
           "ORDER BY da.createdAt DESC")
    List<DogApply> findByDogOwnerIdWithDetails(@Param("dogOwnerId") Long dogOwnerId,
                                               Pageable pageable);

    /**
     * 특정 신청자의 특정 상태 신청 내역 조회 (페이징, N+1 방지)
     * @param applicantId 신청자 ID
     * @param status 신청 상태
     * @param pageable 페이징 정보
     * @return 신청 내역 목록
     */
    @Query("SELECT da FROM DogApply da " +
           "JOIN FETCH da.dog d " +
           "JOIN FETCH d.breed " +
           "JOIN FETCH d.member " +
           "LEFT JOIN FETCH d.shelter s " +
           "LEFT JOIN FETCH s.location " +
           "JOIN FETCH da.applicant " +
           "WHERE da.applicant.memberId = :applicantId " +
           "AND da.status = :status " +
           "AND d.deleted = false " +
           "ORDER BY da.createdAt DESC")
    List<DogApply> findByApplicantIdAndStatusWithDetails(@Param("applicantId") Long applicantId,
                                                         @Param("status") ApplyStatus status,
                                                         Pageable pageable);

    /**
     * 특정 유기견 등록자의 특정 상태 신청 내역 조회 (페이징, N+1 방지)
     * @param dogOwnerId 유기견 등록자 ID
     * @param status 신청 상태
     * @param pageable 페이징 정보
     * @return 신청 내역 목록
     */
    @Query("SELECT da FROM DogApply da " +
           "JOIN FETCH da.dog d " +
           "JOIN FETCH d.breed " +
           "JOIN FETCH d.member " +
           "LEFT JOIN FETCH d.shelter s " +
           "LEFT JOIN FETCH s.location " +
           "JOIN FETCH da.applicant " +
           "WHERE d.member.memberId = :dogOwnerId " +
           "AND da.status = :status " +
           "AND d.deleted = false " +
           "ORDER BY da.createdAt DESC")
    List<DogApply> findByDogOwnerIdAndStatusWithDetails(@Param("dogOwnerId") Long dogOwnerId,
                                                        @Param("status") ApplyStatus status,
                                                        Pageable pageable);

    /**
     * 신청 ID로 상세 정보 조회 (N+1 방지)
     * @param dogApplyId 신청 ID
     * @return 신청 상세 정보
     */
    @Query("SELECT da FROM DogApply da " +
           "JOIN FETCH da.dog d " +
           "JOIN FETCH d.breed " +
           "JOIN FETCH d.member " +
           "LEFT JOIN FETCH d.shelter s " +
           "LEFT JOIN FETCH s.location " +
           "JOIN FETCH da.applicant " +
           "WHERE da.dogApplyId = :dogApplyId")
    Optional<DogApply> findByIdWithDetails(@Param("dogApplyId") Long dogApplyId);

    /**
     * 특정 유기견에 대한 신청 개수 조회
     * @param dogId 유기견 ID
     * @return 신청 개수
     */
    @Query("SELECT COUNT(da) FROM DogApply da " +
           "WHERE da.dog.dogId = :dogId")
    Long countByDogId(@Param("dogId") Long dogId);

    /**
     * 특정 유기견에 대한 대기 중인 신청 개수 조회
     * @param dogId 유기견 ID
     * @return 대기 중인 신청 개수
     */
    @Query("SELECT COUNT(da) FROM DogApply da " +
           "WHERE da.dog.dogId = :dogId " +
           "AND da.status = 'PENDING'")
    Long countPendingByDogId(@Param("dogId") Long dogId);

    /**
     * 특정 신청자가 특정 유기견에 신청했는지 확인
     * @param dogId 유기견 ID
     * @param applicantId 신청자 ID
     * @return 신청 존재 여부
     */
    @Query("SELECT COUNT(da) > 0 FROM DogApply da " +
           "WHERE da.dog.dogId = :dogId " +
           "AND da.applicant.memberId = :applicantId")
    boolean existsByDogIdAndApplicantId(@Param("dogId") Long dogId,
                                       @Param("applicantId") Long applicantId);
}