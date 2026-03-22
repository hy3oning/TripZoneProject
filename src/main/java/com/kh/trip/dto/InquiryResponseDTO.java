package com.kh.trip.dto;

import java.util.List;

import com.kh.trip.domain.InquiryMessage;
import com.kh.trip.domain.InquiryRoom;
import com.kh.trip.domain.enums.InquiryRoomStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryResponseDTO {
	private Long inquiryId;
	private String senderName;
	private String inquiryType;
	private String title;
	private String content;
	private String inquiryStatus;
	private String createdAt;

	public static InquiryResponseDTO fromEntity(InquiryRoom room, List<InquiryMessage> messages) {
		String content = messages == null || messages.isEmpty() ? null : messages.get(0).getMessageContent();
		String status = room.getStatus() == InquiryRoomStatus.CLOSED ? "CLOSED" : "PENDING";
		String createdAt = room.getRegDate() != null ? room.getRegDate().toLocalDate().toString() : null;

		return InquiryResponseDTO.builder().inquiryId(room.getInquiryRoomNo())
				.senderName(room.getUser() != null ? room.getUser().getUserName() : "")
				.inquiryType(room.getInquiryType() != null ? room.getInquiryType().name() : null).title(room.getTitle())
				.content(content).inquiryStatus(status).createdAt(createdAt).build();
	}

}
