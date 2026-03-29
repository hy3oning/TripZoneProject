package com.kh.trip.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.domain.HostProfile;
import com.kh.trip.domain.InquiryRoom;
import com.kh.trip.domain.Lodging;
import com.kh.trip.domain.User;
import com.kh.trip.domain.enums.InquiryRoomStatus;
import com.kh.trip.dto.InquiryRoomDTO;
import com.kh.trip.repository.HostProfileRepository;
import com.kh.trip.repository.InquiryRoomRepository;
import com.kh.trip.repository.LodgingRepository;
import com.kh.trip.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class InquiryRoomServiceImpl implements InquiryRoomService {

	private final InquiryRoomRepository repository;
	private final UserRepository userRepository;
	private final HostProfileRepository hostRepository;
	private final LodgingRepository lodgingRepository;

	@Override
	public Long save(InquiryRoomDTO roomDTO) {
		return repository.findByDetail(roomDTO.getUserNo(), roomDTO.getHostNo(), roomDTO.getLodgingNo(),
				InquiryRoomStatus.CLOSED).map(room -> room.getInquiryRoomNo()).orElseGet(() -> {
					User user = userRepository.findById(roomDTO.getUserNo())
							.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
					HostProfile host = hostRepository.findById(roomDTO.getHostNo())
							.orElseThrow(() -> new IllegalArgumentException("호스트를 찾을 수 없습니다."));
					Lodging lodging = lodgingRepository.findById(roomDTO.getLodgingNo())
							.orElseThrow(() -> new IllegalArgumentException("숙소를 찾을 수 없습니다."));

					InquiryRoom newRoom = InquiryRoom.builder()
							.user(user)
							.host(host)
							.lodging(lodging)
							.status(InquiryRoomStatus.OPEN)
							.build();

					return repository.save(newRoom).getInquiryRoomNo();
				});
	}

	@Override
	public List<InquiryRoomDTO> findByUserNo(Long userNo) {
		List<InquiryRoom> rooms = new ArrayList<>();

		if (hostRepository.existsByUser_UserNo(userNo)) {
			HostProfile host = hostRepository.findByUser_UserNo(userNo)
					.orElseThrow(() -> new IllegalArgumentException("호스트 정보를 찾을 수 없습니다."));
			rooms.addAll(repository.findByHostNo(host.getHostNo(), InquiryRoomStatus.CLOSED));
		}

		User user = userRepository.findById(userNo)
				.orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
		rooms.addAll(repository.findByUserNo(user.getUserNo(), InquiryRoomStatus.CLOSED));

		return rooms.stream()
				.distinct()
				.map(room -> entityToDTO(room))
				.collect(Collectors.toList());
	}

	@Override
	public void delete(Long roomNo) {
		Optional<InquiryRoom> result = repository.findById(roomNo);
		InquiryRoom room = result.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
		room.changeStatus(InquiryRoomStatus.CLOSED);
		repository.save(room);
	}

	private InquiryRoomDTO entityToDTO(InquiryRoom room) {
		return InquiryRoomDTO.builder()
				.inquiryRoomNo(room.getInquiryRoomNo())
				.userNo(room.getUser().getUserNo())
				.hostNo(room.getHost().getHostNo())
				.lodgingNo(room.getLodging().getLodgingNo())
				.status(room.getStatus())
				.build();
	}

}
