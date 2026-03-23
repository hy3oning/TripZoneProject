package com.kh.trip.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.kh.trip.domain.Booking;
import com.kh.trip.domain.Payment;
import com.kh.trip.domain.enums.PaymentPayMethod;
import com.kh.trip.domain.enums.PaymentStatus;
import com.kh.trip.dto.PaymentDTO;
import com.kh.trip.repository.BookingRepository;
import com.kh.trip.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final BookingRepository bookingRepository;

	@Override
	public Long save(PaymentDTO paymentDTO) {
		List<PaymentStatus> blockedStatuses = List.of(PaymentStatus.READY, PaymentStatus.PAID);
		boolean exists = paymentRepository.existsByBooking_BookingNoAndPaymentStatusIn(paymentDTO.getBookingNo(),
				blockedStatuses);

		if (exists) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 진행 중이거나 완료된 결제가 있습니다.");
		}
		Payment payment = dtoToEntity(paymentDTO);
		Payment result = paymentRepository.save(payment);
		return result.getPaymentNo();
	}

	@Override
	public List<PaymentDTO> getPaymentsByBooking(Long bookingNo) {
		List<Payment> paymentList = paymentRepository.findByBooking_BookingNoOrderByPaymentNoDesc(bookingNo);

		return paymentList.stream().map(this::entityToDTO).collect(Collectors.toList());
	}

	@Override
	public PaymentDTO getPaymentById(Long paymentNo) {
		Payment payment = paymentRepository.findById(paymentNo).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 결제입니다. paymentNo=" + paymentNo));
		return entityToDTO(payment);
	}

	@Override
	public void cancel(Long paymentNo) {
		Payment payment = paymentRepository.findById(paymentNo).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 결제입니다. paymentNo=" + paymentNo));

		if (payment.getPaymentStatus() == PaymentStatus.CANCELED) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 취소된 결제입니다. paymentNo=" + paymentNo);
		}

		if (payment.getPaymentStatus() == PaymentStatus.CANCELED) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 취소된 결제입니다. paymentNo=" + paymentNo);
		}

		if (payment.getPaymentStatus() != PaymentStatus.READY && payment.getPaymentStatus() != PaymentStatus.PAID) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"결제 대기 또는 결제 완료 상태만 취소할 수 있습니다. paymentNo=" + paymentNo);
		}
		payment.changePaymentStatus(PaymentStatus.CANCELED);
		payment.changeCanceledAt(LocalDateTime.now());
		payment.changeRefundAmount(payment.getPaymentAmount());

		paymentRepository.save(payment);

	}

	private Payment dtoToEntity(PaymentDTO paymentDTO) {
		Booking booking = bookingRepository.findById(paymentDTO.getBookingNo())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"존재하지 않는 예약입니다. bookingNo=" + paymentDTO.getBookingNo()));
		String orderName = booking.getRoom().getLodgingNo() + "예약";

		return Payment.builder().booking(booking).paymentId(paymentDTO.getPaymentId()).storeId(paymentDTO.getStoreId())
				.channelKey(paymentDTO.getChannelKey()).orderName(orderName)
				.paymentAmount(paymentDTO.getPaymentAmount())
				.currency(paymentDTO.getCurrency() != null ? paymentDTO.getCurrency() : "KRW")
				.payMethod(parsePayMethod(paymentDTO.getPayMethod())).pgProvider(paymentDTO.getPgProvider())
				.paymentStatus(PaymentStatus.READY).approvedAt(null).canceledAt(null).refundAmount(0L).failReason(null)
				.rawResponse(null).build();

	}

	private PaymentDTO entityToDTO(Payment payment) {

		return PaymentDTO.builder().paymentNo(payment.getPaymentNo()).bookingNo(payment.getBooking().getBookingNo())
				.paymentId(payment.getPaymentId()).storeId(payment.getStoreId()).channelKey(payment.getChannelKey())
				.orderName(payment.getOrderName()).paymentAmount(payment.getPaymentAmount())
				.currency(payment.getCurrency()).payMethod(payment.getPayMethod().name())
				.pgProvider(payment.getPgProvider()).paymentStatus(payment.getPaymentStatus().name())
				.approvedAt(payment.getApprovedAt()).canceledAt(payment.getCanceledAt())
				.refundAmount(payment.getRefundAmount()).failReason(payment.getFailReason())
				.rawResponse(payment.getRawResponse()).build();
	}

	// ENUM 변환용 메서드
	private PaymentPayMethod parsePayMethod(String payMethod) {
		try {
			return PaymentPayMethod.valueOf(payMethod);
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 결제 수단입니다. payMethod=" + payMethod);
		}
	}

	private PaymentStatus parsePaymentStatus(String paymentStatus) {
		if (paymentStatus == null) {
			return PaymentStatus.READY;
		}

		try {
			return PaymentStatus.valueOf(paymentStatus);
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 결제 상태값입니다. paymentStatus=" + paymentStatus);
		}
	}
}
