package com.kh.trip.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.trip.domain.HostProfile;
import com.kh.trip.domain.Lodging;
import com.kh.trip.domain.LodgingImage;
import com.kh.trip.domain.Room;
import com.kh.trip.domain.RoomImage;
import com.kh.trip.domain.enums.LodgingStatus;
import com.kh.trip.domain.enums.RoomStatus;
import com.kh.trip.dto.LodgingDTO;
import com.kh.trip.dto.PageRequestDTO;
import com.kh.trip.dto.PageResponseDTO;
import com.kh.trip.dto.RoomDTO;
import com.kh.trip.repository.HostProfileRepository;
import com.kh.trip.repository.LodgingRepository;
import com.kh.trip.repository.ReviewRepository;
import com.kh.trip.repository.RoomImageRepository;
import com.kh.trip.repository.RoomRepository;
import com.kh.trip.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;

/**
 * LodgingService 인터페이스의 실제 구현체
 * 
 * 이 클래스는 숙소 등록, 조회, 수정, 삭제와 같은
 * 숙소 관련 비즈니스 로직을 담당한다.
 * 
 * @Service
 * -> 스프링이 서비스 계층 Bean으로 등록한다.
 * 
 * @RequiredArgsConstructor
 * -> final 필드를 매개변수로 받는 생성자를 Lombok이 자동 생성한다.
 * -> 그래서 생성자 주입 방식으로 Repository와 Util을 주입받을 수 있다.
 * 
 * @Transactional
 * -> 이 클래스의 메서드들은 하나의 트랜잭션 단위로 실행된다.
 * -> 등록, 수정, 삭제 중간에 예외가 발생하면 전체 작업이 롤백된다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LodgingServiceImpl implements LodgingService {

	// 숙소 기본 CRUD 및 조회 처리용 Repository
	private final LodgingRepository lodgingRepository;

	// 숙소에 연결된 객실 조회/수정용 Repository
	private final RoomRepository roomRepository;

	// 객실 이미지 조회용 Repository
	// 숙소 상세조회 시 객실 이미지까지 같이 내려주기 위해 사용
	private final RoomImageRepository roomImageRepository;

	// hostNo를 기준으로 실제 HostProfile 엔티티를 찾을 때 사용
	private final HostProfileRepository hostProfileRepository;

	// 숙소별 리뷰 개수, 평균 평점 집계를 위해 사용
	private final ReviewRepository reviewRepository;

	// 실제 파일 저장/삭제를 처리하는 유틸
	private final CustomFileUtil fileUtil;

	// 레거시 Oracle 시퀀스 값을 직접 읽기 위한 DataSource
	private final DataSource dataSource;

	// 숙소 등록
	@Override
	public LodgingDTO createLodging(LodgingDTO lodgingDTO) {

		// 1. 프론트에서 MultipartFile로 전달한 이미지 파일이 있으면
		// 실제 서버에 저장하고 저장된 파일명 목록을 반환받는다.
		List<String> uploadFileNames = saveUploadedFiles(lodgingDTO.getFiles());

		// 2. 방금 저장된 파일명 목록을 DTO에 다시 세팅한다.
		// 이후 Entity 변환 시 LodgingImage 생성에 사용된다.
		lodgingDTO.setUploadFileNames(uploadFileNames);

		// 3. DTO를 Lodging 엔티티로 변환한다.
		Lodging lodging = toLodgingEntity(lodgingDTO);

		// 4. 기본 유효성 검사
		if (lodging == null) {
			throw new IllegalArgumentException("숙소 정보가 없습니다.");
		}

		// 숙소명은 필수값
		if (lodging.getLodgingName() == null || lodging.getLodgingName().isBlank()) {
			throw new IllegalArgumentException("숙소명은 필수입니다.");
		}

		// 숙소 유형도 필수값
		if (lodging.getLodgingType() == null) {
			throw new IllegalArgumentException("숙소 유형은 필수입니다.");
		}

		// 주소도 필수값
		if (lodging.getAddress() == null || lodging.getAddress().isBlank()) {
			throw new IllegalArgumentException("주소는 필수입니다.");
		}

		// 상태값이 비어 있으면 기본값 ACTIVE로 설정
		if (lodging.getStatus() == null) {
			lodging.changeStatus(LodgingStatus.ACTIVE);
		}

		// 5. 숙소 등록 시 객실 정보가 함께 넘어오면 Room 엔티티 리스트로 변환
		// 넘어오지 않았다면 빈 리스트 사용
		List<Room> roomList = lodgingDTO.getRooms() == null
				? List.of()
				: lodgingDTO.getRooms().stream()
						.map(this::toRoomEntity)
						.collect(Collectors.toList());

		// 6. 현재 프로젝트는 레거시 스키마 때문에
		// LodgingImage 저장 시 imageNo / legacyImageNo 값을 직접 세팅해야 한다.
		assignLodgingImageNumbers(lodging);

		// 7. 숙소를 먼저 저장해서 lodgingNo(PK)를 생성한다.
		Lodging savedLodging = lodgingRepository.save(lodging);

		// 8. 객실 리스트가 있다면
		// 저장된 숙소와 연관관계를 연결한 뒤 객실도 저장한다.
		if (!roomList.isEmpty()) {
			roomList.forEach(room -> room.changeLodging(savedLodging));
			roomRepository.saveAll(roomList);
		}

		// 9. 최종 저장 결과를 DTO로 바꿔서 반환
		return toLodgingDTO(savedLodging);
	}

	// 숙소 단건 조회
	@Override
	@Transactional(readOnly = true)
	public LodgingDTO getLodging(Long lodgingNo) {

		// 1. 숙소 번호로 조회
		Lodging lodging = lodgingRepository.findById(lodgingNo)
				.orElseThrow(() -> new NoSuchElementException("해당 숙소를 찾을 수 없습니다. lodgingNo=" + lodgingNo));

		// 2. 일반 사용자 조회에서는 ACTIVE 상태 숙소만 허용
		// 비활성화된 숙소는 조회되지 않도록 막는다.
		if (lodging.getStatus() != LodgingStatus.ACTIVE) {
			throw new NoSuchElementException("비활성화된 숙소입니다. lodgingNo=" + lodgingNo);
		}

		// 3. 엔티티를 DTO로 변환
		LodgingDTO lodgingDTO = toLodgingDTO(lodging);

		// 4. 리뷰 개수와 평균 평점 정보를 DTO에 추가 반영
		applyReviewSummaries(List.of(lodgingDTO));

		return lodgingDTO;
	}

	// 숙소 전체 목록 조회
	@Override
	@Transactional(readOnly = true)
	public List<LodgingDTO> getAllLodgings() {

		// 1. ACTIVE 숙소 목록과 대표 이미지 1장을 같이 조회
		// selectList()는 Object[] 형태로 반환된다.
		List<Object[]> result = lodgingRepository.selectList();

		// 2. 조회 결과를 DTO 리스트로 변환
		List<LodgingDTO> dtoList = result.stream().map(arr -> {

			// arr[0] = Lodging 엔티티
			Lodging lodging = (Lodging) arr[0];

			// arr[1] = 대표 LodgingImage 엔티티
			LodgingImage image = (LodgingImage) arr[1];

			// Lodging -> LodgingDTO 변환
			LodgingDTO lodgingDTO = toLodgingDTO(lodging);

			// 목록에서는 예약 가능한 객실만 보여주기 위해 AVAILABLE 객실만 세팅
			lodgingDTO.setRooms(loadAvailableRoomDTOs(lodging.getLodgingNo()));

			// 대표 이미지가 존재하면 DTO에 파일명 1개만 넣어준다.
			String imageStr = image != null ? image.getFileName() : null;
			if (imageStr != null) {
				lodgingDTO.setUploadFileNames(List.of(imageStr));
			}

			return lodgingDTO;
		}).collect(Collectors.toList());

		// 3. 모든 숙소 DTO에 리뷰 개수/평균 평점 반영
		applyReviewSummaries(dtoList);

		return dtoList;
	}

	@Override
	@Transactional(readOnly = true)
	public List<LodgingDTO> getLodgingsByHostNo(Long hostNo) {

		// 특정 호스트가 등록한 숙소 목록을 조회
		List<LodgingDTO> dtoList = lodgingRepository.findByHost_HostNo(hostNo).stream()
				.map(this::toLodgingDTO)

				// 판매자 관리 화면에서는 전체 객실을 보여주는 것이 자연스럽기 때문에
				// AVAILABLE만이 아니라 전체 객실을 연결한다.
				.peek(dto -> dto.setRooms(loadRoomDTOs(dto.getLodgingNo())))
				.toList();

		// 리뷰 집계 반영
		applyReviewSummaries(dtoList);

		return dtoList;
	}

	// 숙소 목록 페이징 조회
	@Override
	@Transactional(readOnly = true)
	public PageResponseDTO<LodgingDTO> getAllLodgings(PageRequestDTO pageRequestDTO) {

		// 사용자는 보통 page를 1부터 생각하지만
		// Spring PageRequest는 0부터 시작하므로 1을 빼준다.
		Pageable pageable = PageRequest.of(
				pageRequestDTO.getPage() - 1,
				pageRequestDTO.getSize(),
				Sort.by("lodgingNo").descending()
		);

		// ACTIVE 상태의 숙소만 페이징 조회
		Page<Lodging> result = lodgingRepository.findByStatus(LodgingStatus.ACTIVE, pageable);

		// 엔티티를 DTO로 변환
		List<LodgingDTO> dtoList = result.getContent().stream()
				.map(this::toLodgingDTO)
				.toList();

		// 리뷰 요약 반영
		applyReviewSummaries(dtoList);

		// 공통 페이징 응답 DTO 구성
		return PageResponseDTO.<LodgingDTO>withAll()
				.dtoList(dtoList)
				.pageRequestDTO(pageRequestDTO)
				.totalCount(result.getTotalElements())
				.build();
	}

	// 지역으로 숙소 목록 조회
	@Override
	@Transactional(readOnly = true)
	public List<LodgingDTO> getLodgingsByRegion(String region) {

		// 지역값이 비어 있으면 예외 처리
		if (region == null || region.isBlank()) {
			throw new IllegalArgumentException("지역 값이 비어 있습니다.");
		}

		// 해당 지역의 ACTIVE 숙소만 조회
		List<LodgingDTO> dtoList = lodgingRepository.findByRegionAndStatus(region, LodgingStatus.ACTIVE).stream()
				.map(this::toLodgingDTO)
				.toList();

		// 리뷰 요약 반영
		applyReviewSummaries(dtoList);

		return dtoList;
	}

	// 숙소명 검색
	@Override
	@Transactional(readOnly = true)
	public List<LodgingDTO> searchLodgingsByName(String keyword) {

		// 검색어가 비어 있으면 예외 처리
		if (keyword == null || keyword.isBlank()) {
			throw new IllegalArgumentException("검색어가 비어 있습니다.");
		}

		// 숙소명에 keyword가 포함된 ACTIVE 숙소만 조회
		List<LodgingDTO> dtoList = lodgingRepository
				.findByLodgingNameContainingAndStatus(keyword, LodgingStatus.ACTIVE).stream()
				.map(this::toLodgingDTO)
				.toList();

		// 리뷰 요약 반영
		applyReviewSummaries(dtoList);

		return dtoList;
	}

	// 숙소 수정
	@Override
	public LodgingDTO updateLodging(Long lodgingNo, LodgingDTO lodgingDTO) {

		// 1. 수정 대상 숙소 조회
		Lodging findLodging = lodgingRepository.findById(lodgingNo)
				.orElseThrow(() -> new NoSuchElementException("수정할 숙소가 존재하지 않습니다. lodgingNo=" + lodgingNo));

		// 2. 수정 전에 기존 이미지 파일명을 미리 보관해둔다.
		// 나중에 더 이상 사용하지 않는 파일은 실제 서버에서 삭제하기 위해 필요하다.
		List<String> oldFileNames = extractFileNames(findLodging.getImageList());

		// 3. 숙소 기본 필드 수정
		applyLodgingUpdate(findLodging, lodgingDTO);

		// 4. 프론트에서 기존 이미지 목록이 전달되었는지 체크
		boolean hasImagePayload = lodgingDTO.getUploadFileNames() != null;

		// 5. 새 MultipartFile 업로드가 있으면 저장
		List<String> currentUploadFileNames = saveUploadedFiles(lodgingDTO.getFiles());
		boolean hasNewUploadedFiles = currentUploadFileNames != null && !currentUploadFileNames.isEmpty();

		// 6. 기본값은 기존 파일 유지
		List<String> uploadFileNames = oldFileNames;

		// 7. 이미지 수정 요청이 있는 경우만 이미지 목록을 다시 구성
		if (hasImagePayload || hasNewUploadedFiles) {

			// 기존에 유지할 파일명 목록을 기준으로 새 리스트 생성
			uploadFileNames = lodgingDTO.getUploadFileNames() == null
					? new ArrayList<>()
					: new ArrayList<>(lodgingDTO.getUploadFileNames());

			// 새로 업로드된 파일도 이어붙인다.
			if (hasNewUploadedFiles) {
				uploadFileNames.addAll(currentUploadFileNames);
			}

			// 기존 엔티티의 이미지 연관관계를 비운다.
			findLodging.clearList();

			// 새 파일명 목록을 다시 엔티티에 세팅
			if (!uploadFileNames.isEmpty()) {
				uploadFileNames.forEach(uploadName -> {
					findLodging.addImageString(uploadName);
				});
			}

			// 새 이미지들에 번호 재할당
			assignLodgingImageNumbers(findLodging);
		}

		// 8. 수정된 숙소 저장
		Lodging updatedLodging = lodgingRepository.save(findLodging);
		List<String> finalUploadFileNames = uploadFileNames;

		// 9. 기존 파일 중 더 이상 DB에서 사용하지 않는 파일은 실제 서버에서 삭제
		if (oldFileNames != null && !oldFileNames.isEmpty()) {
			List<String> removeFiles = oldFileNames.stream()
					.filter(fileName -> !finalUploadFileNames.contains(fileName))
					.toList();

			fileUtil.deleteFiles(removeFiles);
		}

		return toLodgingDTO(updatedLodging);
	}

	// LodgingDTO의 수정값을 기존 Lodging 엔티티에 반영하는 메서드
	// Setter 대신 엔티티 내부 change 메서드를 사용해서 캡슐화를 유지한다.
	private void applyLodgingUpdate(Lodging findLodging, LodgingDTO lodgingDTO) {

		// 숙소명 수정
		if (lodgingDTO.getLodgingName() != null && !lodgingDTO.getLodgingName().isBlank()) {
			findLodging.changeLodgingName(lodgingDTO.getLodgingName());
		}

		// 숙소 설명 수정
		if (lodgingDTO.getDescription() != null) {
			findLodging.changeDescription(lodgingDTO.getDescription());
		}

		// 체크인 시간 수정
		if (lodgingDTO.getCheckInTime() != null) {
			findLodging.changeCheckInTime(lodgingDTO.getCheckInTime());
		}

		// 체크아웃 시간 수정
		if (lodgingDTO.getCheckOutTime() != null) {
			findLodging.changeCheckOutTime(lodgingDTO.getCheckOutTime());
		}

		// 상태값 수정
		if (lodgingDTO.getStatus() != null) {
			findLodging.changeStatus(lodgingDTO.getStatus());
		}
	}

	// 숙소 삭제
	@Override
	public void deleteLodging(Long lodgingNo) {

		// 1. 삭제할 숙소 조회
		Lodging findLodging = lodgingRepository.findById(lodgingNo)
				.orElseThrow(() -> new NoSuchElementException("삭제할 숙소가 존재하지 않습니다. lodgingNo=" + lodgingNo));

		// 2. 실제 파일 삭제를 위해 기존 이미지 파일명 목록 확보
		List<String> oldFileNames = extractFileNames(findLodging.getImageList());

		// 3. 물리 삭제 대신 상태를 INACTIVE로 변경
		// 즉, 소프트 삭제 방식이다.
		findLodging.changeStatus(LodgingStatus.INACTIVE);

		// 4. 이미지 연관관계 제거
		// orphanRemoval = true 이므로 DB 이미지 데이터도 함께 정리된다.
		findLodging.clearList();

		// 5. 숙소 상태/이미지 변경 반영 저장
		lodgingRepository.save(findLodging);

		// 6. 연결된 객실도 모두 예약 불가 상태로 변경
		// 숙소가 비활성화되었는데 객실만 AVAILABLE이면 데이터가 어색하기 때문이다.
		List<Room> roomList = roomRepository.findByLodging_LodgingNo(lodgingNo);
		if (roomList != null && !roomList.isEmpty()) {
			roomList.forEach(room -> room.changeStatus(RoomStatus.UNAVAILABLE));
			roomRepository.saveAll(roomList);
		}

		// 7. 실제 서버 파일 삭제
		fileUtil.deleteFiles(oldFileNames);
	}

	// 숙소 상세조회
	@Override
	@Transactional(readOnly = true)
	public LodgingDTO getLodgingDetail(Long lodgingNo) {

		// 1. 숙소 기본 정보와 이미지 목록을 함께 조회
		Optional<Lodging> result = lodgingRepository.selectOne(lodgingNo);

		// 2. 숙소가 없으면 예외
		Lodging lodging = result
				.orElseThrow(() -> new NoSuchElementException("해당 숙소를 찾을 수 없습니다. lodgingNo=" + lodgingNo));

		// 3. 비활성화된 숙소는 상세조회 불가
		if (lodging.getStatus() != LodgingStatus.ACTIVE) {
			throw new NoSuchElementException("비활성화된 숙소입니다. lodgingNo=" + lodgingNo);
		}

		// 4. 엔티티를 DTO로 변환
		LodgingDTO lodgingDTO = toLodgingDTO(lodging);

		// 5. 상세보기에서는 예약 가능한 객실 목록도 함께 포함
		lodgingDTO.setRooms(loadAvailableRoomDTOs(lodgingNo));

		// 6. 리뷰 개수, 평균 평점 반영
		applyReviewSummaries(List.of(lodgingDTO));

		return lodgingDTO;
	}

	// 여러 숙소 DTO에 리뷰 개수와 평균 평점을 반영하는 공통 메서드
	private void applyReviewSummaries(List<LodgingDTO> dtoList) {

		// DTO 리스트가 없으면 종료
		if (dtoList == null || dtoList.isEmpty()) {
			return;
		}

		// DTO 목록에서 lodgingNo만 추출
		List<Long> lodgingNos = dtoList.stream()
				.map(LodgingDTO::getLodgingNo)
				.filter(java.util.Objects::nonNull)
				.toList();

		if (lodgingNos.isEmpty()) {
			return;
		}

		// 리뷰 저장소에서 숙소별 리뷰 개수와 평균 평점을 집계 조회
		Map<Long, ReviewRepository.LodgingReviewSummary> summaryMap =
				reviewRepository.summarizeVisibleByLodgingNos(lodgingNos)
				.stream()
				.collect(Collectors.toMap(
						ReviewRepository.LodgingReviewSummary::getLodgingNo,
						summary -> summary,
						(left, right) -> left,
						HashMap::new
				));

		// 각 DTO에 맞는 리뷰 통계 주입
		dtoList.forEach(lodgingDTO -> {
			ReviewRepository.LodgingReviewSummary summary = summaryMap.get(lodgingDTO.getLodgingNo());

			// 리뷰가 하나도 없는 경우 기본값 세팅
			if (summary == null) {
				lodgingDTO.setReviewCount(0L);
				lodgingDTO.setReviewAverage(0.0);
				return;
			}

			// null 방지하면서 값 세팅
			lodgingDTO.setReviewCount(summary.getReviewCount() != null ? summary.getReviewCount() : 0L);
			lodgingDTO.setReviewAverage(summary.getReviewAverage() != null ? summary.getReviewAverage() : 0.0);
		});
	}

	// 숙소에 속한 AVAILABLE 객실 목록만 DTO로 변환해서 반환
	private List<RoomDTO> loadAvailableRoomDTOs(Long lodgingNo) {
		List<Room> rooms = roomRepository.findByLodging_LodgingNoAndStatusOrderByRoomNoAsc(
				lodgingNo,
				RoomStatus.AVAILABLE
		);
		return mapRoomDTOs(rooms);
	}

	// 숙소에 속한 전체 객실 목록을 DTO로 변환해서 반환
	private List<RoomDTO> loadRoomDTOs(Long lodgingNo) {
		List<Room> rooms = roomRepository.findByLodging_LodgingNo(lodgingNo);
		return mapRoomDTOs(rooms);
	}

	// Room 엔티티 리스트를 RoomDTO 리스트로 변환하는 공통 메서드
	private List<RoomDTO> mapRoomDTOs(List<Room> rooms) {
		return rooms.stream().map(room -> {

			// 객실 이미지들을 정렬 순서대로 조회
			List<String> imageUrls = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo()).stream()
					.map(RoomImage::getImageUrl)
					.toList();

			return toRoomDTO(room, imageUrls);
		}).collect(Collectors.toList());
	}

	// LodgingDTO -> Lodging Entity 변환
	private Lodging toLodgingEntity(LodgingDTO lodgingDTO) {

		// hostNo를 기준으로 실제 HostProfile 엔티티 조회
		HostProfile host = hostProfileRepository.findById(lodgingDTO.getHostNo())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 호스트입니다. hostNo=" + lodgingDTO.getHostNo()));

		// Lodging 엔티티 생성
		Lodging lodging = Lodging.builder()
				.lodgingNo(lodgingDTO.getLodgingNo())
				.host(host)
				.lodgingName(lodgingDTO.getLodgingName())
				.lodgingType(lodgingDTO.getLodgingType())
				.region(lodgingDTO.getRegion())
				.address(lodgingDTO.getAddress())
				.detailAddress(lodgingDTO.getDetailAddress())
				.zipCode(lodgingDTO.getZipCode())
				.latitude(lodgingDTO.getLatitude())
				.longitude(lodgingDTO.getLongitude())
				.description(lodgingDTO.getDescription())
				.checkInTime(lodgingDTO.getCheckInTime())
				.checkOutTime(lodgingDTO.getCheckOutTime())
				.status(lodgingDTO.getStatus())
				.build();

		// DTO에 업로드 파일명 목록이 있으면 LodgingImage 엔티티로 변환해 연결
		List<String> uploadFileNames = lodgingDTO.getUploadFileNames();
		if (uploadFileNames == null) {
			return lodging;
		}

		uploadFileNames.forEach(uploadName -> {
			lodging.addImageString(uploadName);
		});

		return lodging;
	}

	// Lodging Entity -> LodgingDTO 변환
	private LodgingDTO toLodgingDTO(Lodging lodging) {
		LodgingDTO lodgingDTO = LodgingDTO.builder()
				.lodgingNo(lodging.getLodgingNo())
				.hostNo(lodging.getHost().getHostNo())
				.lodgingName(lodging.getLodgingName())
				.lodgingType(lodging.getLodgingType())
				.region(lodging.getRegion())
				.address(lodging.getAddress())
				.detailAddress(lodging.getDetailAddress())
				.zipCode(lodging.getZipCode())
				.latitude(lodging.getLatitude())
				.longitude(lodging.getLongitude())
				.description(lodging.getDescription())
				.checkInTime(lodging.getCheckInTime())
				.checkOutTime(lodging.getCheckOutTime())
				.status(lodging.getStatus())

				// 리뷰 정보는 뒤에서 따로 집계해서 넣기 때문에 기본값 세팅
				.reviewAverage(0.0)
				.reviewCount(0L)
				.build();

		// 이미지 목록이 없으면 그대로 반환
		List<LodgingImage> imageList = lodging.getImageList();
		if (imageList == null || imageList.isEmpty()) {
			return lodgingDTO;
		}

		// LodgingImage 엔티티 리스트를 파일명 리스트로 변환
		List<String> fileNameList = imageList.stream()
				.map(LodgingImage::getFileName)
				.toList();

		lodgingDTO.setUploadFileNames(fileNameList);

		return lodgingDTO;
	}

	// MultipartFile 리스트를 실제 서버에 저장하고 파일명 목록을 반환하는 공통 메서드
	private List<String> saveUploadedFiles(List<MultipartFile> files) {

		// 파일이 없으면 빈 리스트 반환
		if (files == null || files.isEmpty()) {
			return List.of();
		}

		// 첫 번째 파일이 비어 있으면 실질적으로 업로드 없음으로 판단
		if (files.get(0).isEmpty()) {
			return List.of();
		}

		return fileUtil.saveFiles(files);
	}

	// LodgingImage 리스트에서 파일명만 추출하는 공통 메서드
	private List<String> extractFileNames(List<LodgingImage> imageList) {
		if (imageList == null || imageList.isEmpty()) {
			return List.of();
		}

		return imageList.stream()
				.map(LodgingImage::getFileName)
				.toList();
	}

	// LodgingImage 엔티티에 imageNo / legacyImageNo 값을 세팅하는 메서드
	private void assignLodgingImageNumbers(Lodging lodging) {
		if (lodging.getImageList() == null || lodging.getImageList().isEmpty()) {
			return;
		}

		lodging.getImageList().stream()
				.filter(image -> image.getImageNo() == null)
				.forEach(image -> image.assignImageNo(nextLodgingImageNo()));
	}

	// Oracle 시퀀스 SEQ_LODGING_IMAGES.nextval을 직접 조회하는 메서드
	private Long nextLodgingImageNo() {
		try (
				var connection = dataSource.getConnection();
				var preparedStatement = connection.prepareStatement("select SEQ_LODGING_IMAGES.nextval from dual");
				var resultSet = preparedStatement.executeQuery()
		) {
			if (!resultSet.next()) {
				throw new IllegalStateException("SEQ_LODGING_IMAGES 값을 읽지 못했습니다.");
			}
			return resultSet.getLong(1);
		} catch (Exception exception) {
			throw new IllegalStateException("SEQ_LODGING_IMAGES 값을 읽지 못했습니다.", exception);
		}
	}

	// RoomDTO -> Room Entity 변환
	private Room toRoomEntity(RoomDTO roomDTO) {
		return Room.builder()
				.roomName(roomDTO.getRoomName())
				.roomType(roomDTO.getRoomType())
				.roomDescription(roomDTO.getRoomDescription())
				.maxGuestCount(roomDTO.getMaxGuestCount())
				.pricePerNight(roomDTO.getPricePerNight())
				.roomCount(roomDTO.getRoomCount())

				// 상태값이 없으면 기본값 AVAILABLE
				.status(roomDTO.getStatus() != null ? roomDTO.getStatus() : RoomStatus.AVAILABLE)
				.build();
	}

	// Room Entity + 이미지 목록 -> RoomDTO 변환
	private RoomDTO toRoomDTO(Room room, List<String> imageUrls) {
		return RoomDTO.builder()
				.roomNo(room.getRoomNo())
				.lodgingNo(room.getLodging().getLodgingNo())
				.roomName(room.getRoomName())
				.roomType(room.getRoomType())
				.roomDescription(room.getRoomDescription())
				.maxGuestCount(room.getMaxGuestCount())
				.pricePerNight(room.getPricePerNight())
				.roomCount(room.getRoomCount())
				.status(room.getStatus())
				.imageUrls(imageUrls)
				.build();
	}
}