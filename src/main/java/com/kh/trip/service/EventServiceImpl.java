package com.kh.trip.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kh.trip.domain.Event;
import com.kh.trip.dto.EventDTO;
import com.kh.trip.dto.PageRequestDTO;
import com.kh.trip.dto.PageResponseDTO;
import com.kh.trip.repository.EventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventServiceImpl implements EventService {
	private final EventRepository eventRepository;
	private final ModelMapper modelMapper;

	// list
	@Override
	public PageResponseDTO<EventDTO> list(PageRequestDTO pageRequestDTO) {

		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
				Sort.by("eventNo").descending());

		Page<Event> result = eventRepository.findAll(pageable);

		List<EventDTO> dtoList = result.getContent().stream().map(event -> {
			return EventDTO.builder().eventNo(event.getEventNo()).title(event.getTitle()).content(event.getContent())
					.thumbnailUrl(event.getThumbnailUrl()).startDate(event.getStartDate()).build();
		}).collect(Collectors.toList());

		long totalCount = result.getTotalElements();

		return PageResponseDTO.<EventDTO>withAll().dtoList(dtoList) // 위에서 만든 dtoList를 담아줍니다
				.totalCount(totalCount).pageRequestDTO(pageRequestDTO).build();
	}

	// save
	@Override
	public Long save(EventDTO eventDTO) {
		log.info(".........");
		Event event = Event.builder().title(eventDTO.getTitle()).content(eventDTO.getContent())
				.thumbnailUrl(eventDTO.getThumbnailUrl()).startDate(eventDTO.getStartDate())
				.endDate(eventDTO.getEndDate()).viewCount((long) eventDTO.getViewCount())
				// 만약 adminUserNo가 필요하다면 여기서 추가 설정
				.build();
		Event savedEvent = eventRepository.save(event);
		return savedEvent.getEventNo();

	}

	// update
	@Override
	public void update(EventDTO eventDTO) {
		Optional<Event> result = eventRepository.findById(eventDTO.getEventNo());
		Event event = eventRepository.findById(eventDTO.getEventNo())
				.orElseThrow(() -> new RuntimeException("해당 ID 없음: " + eventDTO.getEventNo()));

		event.changeTitle(eventDTO.getTitle());
		event.changeContent(eventDTO.getContent());
		event.changeThumbnailUrl(eventDTO.getThumbnailUrl());
		log.info("DB 조회 시도 ID: " + eventDTO.getEventNo());
		eventRepository.findAll().forEach(e -> log.info("DB에 있는 ID: " + e.getEventNo()));
		eventRepository.save(event);
	}

	// delete
	@Override
	public void delete(Long eno) {
		eventRepository.deleteById(eno);
	}

}
