package com.kh.trip.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.domain.InquiryMessage;
import com.kh.trip.domain.InquiryRoom;
import com.kh.trip.domain.User;
import com.kh.trip.domain.enums.InquiryMessageSenderType;
import com.kh.trip.domain.enums.InquiryRoomInquiryType;
import com.kh.trip.domain.enums.InquiryRoomStatus;
import com.kh.trip.domain.enums.InquiryRoomTargetType;
import com.kh.trip.dto.InquiryRequestDTO;
import com.kh.trip.repository.InquiryMessageRepository;
import com.kh.trip.repository.InquiryRoomRepository;
import com.kh.trip.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryServiceImpl implements InquiryService {

	private final InquiryRoomRepository inquiryRoomRepository;
	private final InquiryMessageRepository inquiryMessageRepository;
	private final UserRepository userRepository;
	
	@Override
	public Long createInquiry(InquiryRequestDTO request, Long userNo) {
		User user = userRepository.findById(userNo)
				.orElseThrow(() -> new NoSuchElementException("?ъ슜?먮? 李얠쓣 ???놁뒿?덈떎."));

		InquiryRoomInquiryType inquiryType;
		try {
			inquiryType = InquiryRoomInquiryType.valueOf(request.getInquiryType());
		} catch (IllegalArgumentException e) {
			inquiryType = InquiryRoomInquiryType.ETC;
		}

		InquiryRoomTargetType targetType = switch (inquiryType) {
		case USER_TO_SELLER -> InquiryRoomTargetType.HOST;
		case SELLER_TO_ADMIN, COMMON_TO_ADMIN -> InquiryRoomTargetType.ADMIN;
		default -> InquiryRoomTargetType.ADMIN;
		};

		InquiryRoom room = InquiryRoom.builder().user(user).inquiryType(inquiryType).targetType(targetType)
				.title(request.getTitle()).build();

		InquiryRoom savedRoom = inquiryRoomRepository.save(room);

		InquiryMessage message = InquiryMessage.builder().inquiryRoom(savedRoom).senderUser(user)
				.senderType(targetType == InquiryRoomTargetType.HOST ? InquiryMessageSenderType.USER
						: InquiryMessageSenderType.USER)
				.messageContent(request.getContent()).build();
		inquiryMessageRepository.save(message);

		return savedRoom.getInquiryRoomNo();
	}

	@Override
	@Transactional(readOnly = true)
	public List<InquiryRoom> findByUserNo(Long userNo) {
		return inquiryRoomRepository.findByUserNo(userNo);
	}

	@Override
	@Transactional(readOnly = true)
	public InquiryRoom findById(Long inquiryRoomNo) {
		return inquiryRoomRepository.findById(inquiryRoomNo)
				.orElseThrow(() -> new NoSuchElementException("臾몄쓽瑜?李얠쓣 ???놁뒿?덈떎."));
	}

	@Override
	public void closeInquiry(Long inquiryRoomNo) {
		InquiryRoom room = findById(inquiryRoomNo);
		room.changeStatus(InquiryRoomStatus.CLOSED);
		inquiryRoomRepository.save(room);
	}

	@Override
	@Transactional(readOnly = true)
	public List<InquiryRoom> findAll() {
		return inquiryRoomRepository.findAllByOrderByInquiryRoomNoDesc();
	}

	@Override
	public void updateStatus(Long inquiryRoomNo, String status) {
		InquiryRoom room = findById(inquiryRoomNo);
		InquiryRoomStatus newStatus = "CLOSED".equalsIgnoreCase(status) || "ANSWERED".equalsIgnoreCase(status)
				? InquiryRoomStatus.CLOSED
				: InquiryRoomStatus.OPEN;
		room.changeStatus(newStatus);
		inquiryRoomRepository.save(room);
	}
}
