package com.kh.trip.service;

import com.kh.trip.dto.BookingDTO;
import com.kh.trip.dto.PageRequestDTO;
import com.kh.trip.dto.PageResponseDTO;

public interface BookingService {

	Long save(BookingDTO bookingDTO);

	PageResponseDTO<BookingDTO> findByUserId(Long userNo,PageRequestDTO pageRequestDTO);

	PageResponseDTO<BookingDTO> findByRoomId(Long hostNo,PageRequestDTO pageRequestDTO);

	void delete(Long bookingNo);

}
