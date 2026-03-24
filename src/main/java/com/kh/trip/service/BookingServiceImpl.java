package com.kh.trip.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.domain.Booking;
import com.kh.trip.domain.Lodging;
import com.kh.trip.domain.Room;
import com.kh.trip.domain.User;
import com.kh.trip.domain.UserCoupon;
import com.kh.trip.domain.enums.BookingStatus;
import com.kh.trip.domain.enums.CouponStatus;
import com.kh.trip.domain.enums.DiscountType;
import com.kh.trip.domain.enums.RoomStatus;
import com.kh.trip.dto.BookingDTO;
import com.kh.trip.dto.PageRequestDTO;
import com.kh.trip.dto.PageResponseDTO;
import com.kh.trip.repository.BookingRepository;
import com.kh.trip.repository.RoomRepository;
import com.kh.trip.repository.UserCouponRepository;
import com.kh.trip.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class BookingServiceImpl implements BookingService {
	private final BookingRepository repository;
	private final UserCouponRepository userCouponRepository;
	private final UserRepository userRepository;
	private final RoomRepository roomRepository;

	@Override
	public Long save(BookingDTO bookingDTO) {

		User user = userRepository.findById(bookingDTO.getUserNo())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 정보입니다."));

		Room room = roomRepository.findById(bookingDTO.getRoomNo())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객실 정보입니다."));

		if (room.getStatus().equals(RoomStatus.UNAVAILABLE))
			throw new IllegalArgumentException("예약이 불가한 방입니다.");

		// 숙박 일수 계산 (체크아웃 날짜 - 체크인 날짜)
		Long daysBetween = ChronoUnit.DAYS.between(bookingDTO.getCheckInDate().toLocalDate(), bookingDTO.getCheckOutDate().toLocalDate());
		Long roomPrice = room.getPricePerNight() * daysBetween;
		Long totalPrice = roomPrice;
		Long discountAmount = 0L;

		UserCoupon userCoupon = null;
		if (bookingDTO.getUserCouponNo() != null) {
			userCoupon = userCouponRepository.findById(bookingDTO.getUserCouponNo())
					.orElseThrow(() -> new IllegalArgumentException("회원이 갖고 있지 않는 쿠폰번호입니다."));
			if (!userCoupon.getStatus().equals(CouponStatus.ACTIVE))
				throw new IllegalArgumentException("사용불가한 쿠폰입니다.");

			// enum에서 만든 로직 사용
			DiscountType type = userCoupon.getCoupon().getDiscountType();
			Long discountValue = userCoupon.getCoupon().getDiscountValue();

			totalPrice = type.calculate(roomPrice, discountValue);

			// 0원 미만으로 떨어지는 것을 방지하는 안전장치
			if (totalPrice < 0)
				totalPrice = 0L;

			discountAmount = roomPrice - totalPrice;
		}

		Booking booking = Booking.builder().user(user).userCoupon(userCoupon).room(room)
				.checkInDate(bookingDTO.getCheckInDate()).checkOutDate(bookingDTO.getCheckOutDate())
				.guestCount(bookingDTO.getGuestCount()).pricePerNight(Long.valueOf(room.getPricePerNight()))
				.discountAmount(discountAmount).totalPrice(totalPrice).regDate(bookingDTO.getRegDate())
				.status(BookingStatus.PENDING).build();
		Long bookingNo = repository.save(booking).getBookingNo();

		// 변경해야할 부분
		room.changeStatus(RoomStatus.UNAVAILABLE);
		roomRepository.save(room);
		if (userCoupon != null) {
			userCoupon.changeUsedAt(LocalDateTime.now());
			userCoupon.changeStatus(CouponStatus.USED);
		}
		return bookingNo;
	}

	@Override
	public PageResponseDTO<BookingDTO> findByUserId(Long userNo, PageRequestDTO pageRequestDTO) {
		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
				Sort.by("bookingNo").descending());
		User user = userRepository.findById(userNo)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 정보입니다."));
		Page<Booking> result = repository.findByUserId(user.getUserNo(), pageable);
		

		List<BookingDTO> dtoList = entityToDTO(user, result);
		return PageResponseDTO.<BookingDTO>withAll().dtoList(dtoList).pageRequestDTO(pageRequestDTO)
				.totalCount(result.getTotalElements()).build();
	}

	@Override
	public PageResponseDTO<BookingDTO> findByRoomId(Long hostNo, PageRequestDTO pageRequestDTO) {
		// userNo랑 Lodging의 host가 같은 조건으로 숙소를 찾고, 그 숙소에 포함된 roomNo에 따라 불러올 리스트를 정한다.
		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
				Sort.by("bookingNo").descending());
		User user = userRepository.findById(hostNo)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 정보입니다."));
		Page<Booking> result = repository.findByRoomId(user.getUserNo(), pageable);

		List<BookingDTO> dtoList = entityToDTO(user, result);
		return PageResponseDTO.<BookingDTO>withAll().dtoList(dtoList).pageRequestDTO(pageRequestDTO)
				.totalCount(result.getTotalElements()).build();
	}

	@Override
	public void delete(Long bookingNo) {
		Optional<Booking> result = repository.findById(bookingNo);
		Booking booking = result.orElseThrow();
		// 쿠폰복구 처리도 포함된 함수
		booking.cancel();
		repository.save(booking);
		Optional<Room> resultRoom = roomRepository.findById(booking.getRoom().getRoomNo());
		Room room = resultRoom.orElseThrow();
		// 변경해야할 부분
		room.changeStatus(RoomStatus.AVAILABLE);
		roomRepository.save(room);

		// 환불처리로직도 추가해야함.
	}

	public List<BookingDTO> entityToDTO(User user, Page<Booking> result) {
		return result.getContent().stream().map(booking -> {
			Long targetLodgingNo = booking.getRoom().getLodgingNo();
			return BookingDTO.builder().bookingNo(booking.getBookingNo()).userNo(user.getUserNo())
						.roomNo(booking.getRoom().getRoomNo()).lodgingName(findLodgingName(user, targetLodgingNo))
						.userCouponNo(booking.getUserCoupon()!=null? booking.getUserCoupon().getUserCouponNo():null).roomName(booking.getRoom().getRoomName())
						.checkInDate(booking.getCheckInDate()).checkOutDate(booking.getCheckOutDate())
						.guestCount(booking.getGuestCount()).pricePerNight(booking.getPricePerNight())
						.discountAmount(booking.getDiscountAmount()).totalPrice(booking.getTotalPrice())
						.status(booking.getStatus()).requestMessage(booking.getRequestMessage())
						.regDate(booking.getRegDate())
						.build();})
				.collect(Collectors.toList());
	}
	
	public String findLodgingName(User user, Long targetLodgingNo) {
		String lodgingName = null;
		List<Lodging> lodgingList = repository.findLodigByUserId(user.getUserNo());
		for(Lodging lodging : lodgingList) {
			if(lodging.getLodgingNo().equals(targetLodgingNo)) {
				lodgingName = lodging.getLodgingName();
			}
		}
		return lodgingName;
	}
}
