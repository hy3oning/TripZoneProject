package com.kh.trip.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kh.trip.domain.Coupon;
import com.kh.trip.domain.Event;
import com.kh.trip.domain.EventCoupon;
import com.kh.trip.domain.User;
import com.kh.trip.domain.enums.EventStatus;
import com.kh.trip.dto.EventDTO;
import com.kh.trip.dto.PageRequestDTO;
import com.kh.trip.dto.PageResponseDTO;
import com.kh.trip.repository.CouponRepository;
import com.kh.trip.repository.EventCouponRepository;
import com.kh.trip.repository.EventRepository;
import com.kh.trip.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventServiceImpl implements EventService {
	private final EventRepository eventRepository;
	private final UserRepository userRepository;
	private final EventCouponRepository eventCouponRepository;
	private final CouponRepository couponRepository;

	// list
	@Override
	public PageResponseDTO<EventDTO> findAll(PageRequestDTO pageRequestDTO) {

		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
				Sort.by("eventNo").descending());

		Page<Event> result = eventRepository.findAll(pageable);

		List<EventDTO> dtoList = result.getContent().stream().map(event -> {
			return EventDTO.builder().eventNo(event.getEventNo()).title(event.getTitle()).content(event.getContent())
					.thumbnailUrl(event.getThumbnailUrl()).startDate(event.getStartDate()).endDate(event.getEndDate())
					.viewCount(event.getViewCount()).status(event.getStatus()).build();
		}).collect(Collectors.toList());

		Long totalCount = result.getTotalElements();

		return PageResponseDTO.<EventDTO>withAll().dtoList(dtoList) // 위에서 만든 dtoList를 담아줍니다
				.totalCount(totalCount).pageRequestDTO(pageRequestDTO).build();
	}

	// findById
	@Override
	public EventDTO findById(Long eno) {
		log.info(".....................");
		java.util.Optional<Event> result = eventRepository.findById(eno);
		Event event = result.orElseThrow();
		Long viewCount = event.getViewCount() + 1L;
		event.changeViewCount(viewCount);
		eventRepository.save(event);
		List<Coupon> coupons = eventCouponRepository.findAllById(eno);
		List<String> couponNames = coupons.stream().map(coupon-> coupon.getCouponName()).collect(Collectors.toList());
		return EventDTO.builder().eventNo(event.getEventNo()).title(event.getTitle()).content(event.getContent())
				.thumbnailUrl(event.getThumbnailUrl()).startDate(event.getStartDate()).endDate(event.getEndDate())
				.adminUserNo(event.getAdminUserNo().getUserNo()).couponNames(couponNames).status(event.getStatus())
				.viewCount(event.getViewCount())
				.build();
	}

	// save
	@Override
	public Long save(EventDTO eventDTO) {
		log.info(".........");
		User user = userRepository.findById(eventDTO.getAdminUserNo())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자 번호입니다."));
		Event event = Event.builder().title(eventDTO.getTitle()).content(eventDTO.getContent())
				.thumbnailUrl(eventDTO.getThumbnailUrl()).startDate(eventDTO.getStartDate())
				.endDate(eventDTO.getEndDate()).viewCount(0L).adminUserNo(user).status(EventStatus.DRAFT).build();
		// [추가] 2. 중복 검사 (방어 코드)
	    // 같은 제목의 이벤트가 이미 있는지 확인합니다.
	    boolean isExist = eventRepository.existsByTitle(eventDTO.getTitle());
	    
	    if (isExist) {
	        throw new IllegalStateException("이미 동일한 제목의 이벤트가 존재합니다.");
	    }
		Event savedEvent = eventRepository.save(event);
		List<Long> coupons = eventDTO.getCoupons();
		if (coupons != null && !coupons.isEmpty()) {
			List<EventCoupon> eventCouponList = coupons.stream().map(cno -> {
				Coupon coupon = couponRepository.findById(cno).orElseThrow();
				return EventCoupon.builder().event(savedEvent).coupon(coupon).build();
			}).collect(Collectors.toList());
			eventCouponRepository.saveAll(eventCouponList);
		}
		;
		return savedEvent.getEventNo();

	}

	// update
	@Override
	public void update(EventDTO eventDTO) {
		Optional<Event> result = eventRepository.findById(eventDTO.getEventNo());
		Event event = result.orElseThrow(() -> new RuntimeException("해당 ID 없음: " + eventDTO.getEventNo()));

		event.changeTitle(eventDTO.getTitle());
		event.changeContent(eventDTO.getContent());
		event.changeThumbnailUrl(eventDTO.getThumbnailUrl());
		event.changeStartDate(eventDTO.getStartDate());
		event.changeEndDate(eventDTO.getEndDate());
		log.info("DB 조회 시도 ID: " + eventDTO.getEventNo());
		eventRepository.findAll().forEach(e -> log.info("DB에 있는 ID: " + e.getEventNo()));
		eventRepository.save(event);
	}

	// delete
	@Override
	public void delete(Long eno) {
		Optional<Event> result = eventRepository.findById(eno);
		Event event = result.orElseThrow(() -> new RuntimeException("해당 ID 없음: " + eno));
		event.changeStatus(EventStatus.HIDDEN);
		eventRepository.save(event);
	}
}
