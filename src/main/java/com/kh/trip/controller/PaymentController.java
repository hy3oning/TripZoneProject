package com.kh.trip.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.trip.dto.PaymentDTO;
import com.kh.trip.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping
	public Map<String, Long> save(@Valid @RequestBody PaymentDTO paymentDTO) {
		Long paymentNo = paymentService.save(paymentDTO);
		return Map.of("result", paymentNo);
	}

	@GetMapping("/booking/{bookingNo}")
	public List<PaymentDTO> getPaymentsByBooking(@PathVariable Long bookingNo) {
		return paymentService.getPaymentsByBooking(bookingNo);
	}

	@GetMapping("/{paymentNo}")
	public PaymentDTO getPaymentById(@PathVariable Long paymentNo) {
		return paymentService.getPaymentById(paymentNo);
	}

	@PutMapping("/{paymentNo}/cancel")
	public Map<String, String> cancel(@PathVariable Long paymentNo) {
		paymentService.cancel(paymentNo);
		return Map.of("result", "SUCCESS");
	}
}
