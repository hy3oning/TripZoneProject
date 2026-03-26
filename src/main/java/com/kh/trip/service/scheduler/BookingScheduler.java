package com.kh.trip.service.scheduler;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.domain.enums.BookingStatus;
import com.kh.trip.repository.BookingRepository;

@Component
public class BookingScheduler {
	@Autowired
	private BookingRepository bookingRepository;
	
	@Scheduled(cron = "0 0 13 * * *")
	@Transactional
	public void updateBookingStatus() {
		LocalDateTime today = LocalDateTime.now();
		bookingRepository.updateStatusForCheckout(today,BookingStatus.COMPLETED);
	}
	
}
