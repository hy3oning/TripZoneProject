package com.kh.trip.service;

import java.util.List;

import com.kh.trip.dto.PaymentDTO;

public interface PaymentService {

	Long save(PaymentDTO paymentDTO);

	List<PaymentDTO> getPaymentsByBooking(Long bookingNo);

	PaymentDTO getPaymentById(Long paymentNo);

	void complete(Long paymentNo);

	void cancel(Long paymentNo);

}
