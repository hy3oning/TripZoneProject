package com.kh.trip.dto; 

import java.util.List;

import lombok.AllArgsConstructor; 
import lombok.Builder; 
import lombok.Getter; 
import lombok.NoArgsConstructor; 

@Getter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class ReviewCreateDTO {

    private Long bookingNo; // 어떤 예약에 대한 리뷰인지
    private Long lodgingNo; // 어떤 숙소에 대한 리뷰인지
    private Integer rating; // 평점
    private String content; // 리뷰 내용
    private List<String> imageUrls; //리뷰이미지
}
