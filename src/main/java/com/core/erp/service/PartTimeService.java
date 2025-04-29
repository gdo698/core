package com.core.erp.service;

import com.core.erp.domain.PartTimerEntity;
import com.core.erp.domain.StoreEntity;
import com.core.erp.dto.PartTimerDTO;
import com.core.erp.dto.PartTimerSearchDTO;
import com.core.erp.repository.PartTimerRepository;
import com.core.erp.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartTimeService {

    private final PartTimerRepository partTimerRepository;
    private final StoreRepository storeRepository;
    private final String uploadDir = "/upload/parttimer/";

    public List<PartTimerDTO> searchPartTimers(
            Integer storeId,
            Integer departId,
            PartTimerSearchDTO searchDTO) {

        Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize());
        Page<PartTimerEntity> result;

        if (departId != null && departId >= 1 && departId <= 10) {
            // 본사 권한: 전체 매장 검색
            result = partTimerRepository.searchHeadquarterSide(
                    searchDTO.getPartName(),
                    searchDTO.getPartStatus(),
                    storeId, // storeId는 optional
                    searchDTO.getPartTimerId(),
                    pageable
            );
        } else if (departId != null && departId == 13) {
            // 점주 권한: 본인 매장만 검색
            result = partTimerRepository.searchStoreSide(
                    storeId,
                    searchDTO.getPartName(),
                    searchDTO.getPartStatus(),
                    searchDTO.getPartTimerId(),
                    pageable
            );
        } else {
            throw new RuntimeException("권한이 없습니다.");
        }

        // Entity → DTO 변환
        return result.map(PartTimerDTO::new).getContent();
    }

    public Page<PartTimerDTO> findAllPartTimers(Integer storeId, Integer departId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<PartTimerEntity> result;

        System.out.println("storeId: " + storeId);
        System.out.println("departId: " + departId);

        if (departId != null && departId >= 1 && departId <= 10) {
            // 본사 전체 조회
            result = partTimerRepository.findAll(pageable);
        } else if (departId != null && departId == 13) {
            // 점주 자기 매장 조회
            result = partTimerRepository.findByStoreStoreId(storeId, pageable);
        } else {
            throw new RuntimeException("권한이 없습니다.");
        }

        return result.map(PartTimerDTO::new);
    }

    public PartTimerDTO findPartTimerById(Integer partTimerId) {
        PartTimerEntity entity = partTimerRepository.findById(partTimerId)
                .orElseThrow(() -> new RuntimeException("해당 아르바이트를 찾을 수 없습니다."));

        return new PartTimerDTO(entity);
    }

    public void registerPartTimer(Integer storeId, PartTimerDTO partTimerDTO) {
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("지점을 찾을 수 없습니다."));

        PartTimerEntity entity = new PartTimerEntity(partTimerDTO, store);

        // 파일 업로드 처리
        String uploadedPath = uploadFile(partTimerDTO.getFile());
        if (uploadedPath != null) {
            entity.setPartImg(uploadedPath); // PartTimerEntity에 이미지 경로 저장
        }
     /*
        // 🔽 [PROD - S3 업로드 예정]
        String s3Url = s3Uploader.upload(partTimerDTO.getFile(), "parttimer");
        entity.setPartImg(s3Url);
    */

        partTimerRepository.save(entity);
    }

    public void updatePartTimer(Integer storeId, Integer partTimerId, PartTimerDTO partTimerDTO) {
        // 1. 기존 아르바이트 엔티티 찾기
        PartTimerEntity entity = partTimerRepository.findById(partTimerId)
                .orElseThrow(() -> new RuntimeException("해당 아르바이트를 찾을 수 없습니다."));

        // 2. 지점 매칭 체크 (본인 지점만 수정 가능하게 할 경우)
        if (entity.getStore().getStoreId() != storeId) {
            throw new RuntimeException("본인 지점의 아르바이트만 수정할 수 있습니다.");
        }

        // 3. DTO 값으로 Entity 업데이트
        entity.setPartName(partTimerDTO.getPartName());
        entity.setPartGender(partTimerDTO.getPartGender());
        entity.setPartPhone(partTimerDTO.getPartPhone());
        entity.setPartAddress(partTimerDTO.getPartAddress());
        entity.setResignDate(partTimerDTO.getResignDate());
        entity.setSalaryType(partTimerDTO.getSalaryType());
        entity.setHourlyWage(partTimerDTO.getHourlyWage());
        entity.setAccountBank(partTimerDTO.getAccountBank());
        entity.setAccountNumber(partTimerDTO.getAccountNumber());
        entity.setPartStatus(partTimerDTO.getPartStatus());
        entity.setPosition(partTimerDTO.getPosition());
        entity.setWorkType(partTimerDTO.getWorkType());

        // 파일 새로 업로드 했을 경우
        if (partTimerDTO.getFile() != null && !partTimerDTO.getFile().isEmpty()) {
            String uploadedPath = uploadFile(partTimerDTO.getFile());
            entity.setPartImg(uploadedPath);
        }

         /*
         // 🔽 [PROD - S3 업로드 예정]
         if (partTimerDTO.getFile() != null && !partTimerDTO.getFile().isEmpty()) {
            String s3Url = s3Uploader.upload(partTimerDTO.getFile(), "parttimer");
            entity.setPartImg(s3Url);
    }
    */
    }

    public void deletePartTimer(Integer storeId, Integer partTimerId) {
        // 1. 삭제할 아르바이트 엔티티 찾기
        PartTimerEntity entity = partTimerRepository.findById(partTimerId)
                .orElseThrow(() -> new RuntimeException("해당 아르바이트를 찾을 수 없습니다."));

        // 2. 지점 매칭 체크
        if (entity.getStore().getStoreId() != storeId) {
            throw new RuntimeException("본인 지점의 아르바이트만 삭제할 수 있습니다.");
        }

        // 3. 삭제 수행
        partTimerRepository.delete(entity);
    }

    private String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // [DEV MODE] 로컬 저장
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            String savedFilename = UUID.randomUUID().toString() + ext;

            // 실제 파일 저장
            file.transferTo(new File(uploadDir + savedFilename));

            // 저장 경로 반환 (로컬 테스트 시 사용)
            return "/upload/parttimer/" + savedFilename;

        /*
        // [PROD MODE] S3 연동 (운영 시 사용 예정)
        // String s3Url = s3Uploader.upload(file, "parttimer");
        // return s3Url;
        */

        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }
}
