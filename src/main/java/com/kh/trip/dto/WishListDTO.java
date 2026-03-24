package com.kh.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WishListDTO {
	private Long wishListNo;
	private Long userNo;
	private Long lodgingNo;
	private LodgingDTO lodgingDTO;
}
