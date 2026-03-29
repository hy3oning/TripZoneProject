package com.kh.trip.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kh.trip.domain.InquiryMessage;
import com.kh.trip.domain.InquiryRoom;
import com.kh.trip.domain.User;
import com.kh.trip.domain.enums.InquiryRoomStatus;
import com.kh.trip.domain.enums.SenderType;
import com.kh.trip.dto.InquiryMessageDTO;
import com.kh.trip.repository.InquiryMessageRepository;
import com.kh.trip.repository.InquiryRoomRepository;
import com.kh.trip.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryMessageServiceImpl implements InquiryMessageService {

	private final InquiryMessageRepository repository;
	private final InquiryRoomRepository roomRepository;
	private final UserRepository userRepository;

	@Override
	public List<InquiryMessageDTO> findByRoomNo(Long roomNo) {
		List<InquiryMessage> result = repository.findByRoomNo(roomNo);
		return result.stream().map(message -> entityToDTO(message)).collect(Collectors.toList());
	}

	@Override
	public InquiryMessageDTO save(InquiryMessageDTO messageDTO) {
		InquiryRoom room = roomRepository.findById(messageDTO.getInquiryRoomNo())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

		if (room.getStatus() == InquiryRoomStatus.CLOSED) {
			throw new IllegalArgumentException("닫힌 채팅방에는 메시지를 보낼 수 없습니다.");
		}

		User sender = userRepository.findById(messageDTO.getSenderNo())
				.orElseThrow(() -> new IllegalArgumentException("발신자 정보가 올바르지 않습니다."));

		SenderType senderType;
		if (room.getUser().getUserNo().equals(sender.getUserNo())) {
			senderType = SenderType.USER;
		} else if (room.getHost().getUser().getUserNo().equals(sender.getUserNo())) {
			senderType = SenderType.HOST;
		} else {
			throw new IllegalArgumentException("이 채팅방의 참여자가 아닙니다.");
		}

		InquiryMessage message = InquiryMessage.builder()
				.inquiryRoom(room)
				.user(sender)
				.senderType(senderType)
				.content(messageDTO.getContent())
				.build();

		InquiryMessage savedMessage = repository.save(message);

		if (senderType == SenderType.USER) {
			room.changeStatus(InquiryRoomStatus.WAITING);
		} else {
			room.changeStatus(InquiryRoomStatus.ANSWERED);
		}
		roomRepository.save(room);

		return entityToDTO(savedMessage);
	}

	private InquiryMessageDTO entityToDTO(InquiryMessage message) {
		return InquiryMessageDTO.builder()
				.messageNo(message.getMessageNo())
				.inquiryRoomNo(message.getInquiryRoom().getInquiryRoomNo())
				.senderNo(message.getUser().getUserNo())
				.content(message.getContent())
				.build();
	}

}
