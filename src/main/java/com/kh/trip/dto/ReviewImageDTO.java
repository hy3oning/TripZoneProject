package com.kh.trip.dto; 

import com.kh.trip.domain.ReviewImage; 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
@NoArgsConstructor 
@AllArgsConstructor
@Builder 
public class ReviewImageDTO {

    private Long reviewImageNo; // 리뷰 이미지 번호
    private String imageUrl; // 이미지 경로
    private Integer sortOrder; // 정렬 순서

    // 엔티티를 DTO로 변환하는 정적 메서드
    public static ReviewImageDTO fromEntity(ReviewImage reviewImage) {
        return ReviewImageDTO.builder()
                .reviewImageNo(reviewImage.getReviewImageNo()) // 리뷰 이미지 번호 세팅
                .imageUrl(reviewImage.getImageUrl()) // 이미지 URL 세팅
                .sortOrder(reviewImage.getSortOrder()) // 정렬 순서 세팅
                .build(); // DTO 생성
    }
}
