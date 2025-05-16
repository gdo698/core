package com.core.erp.service;

import com.core.erp.domain.DisplayLocationEntity;
import com.core.erp.dto.CustomPrincipal;
import com.core.erp.dto.display.DisplayLocationDTO;
import com.core.erp.repository.DisplayLocationRepository;
import com.core.erp.repository.ProductLocationMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisplayLocationService {

    private final DisplayLocationRepository displayLocationRepository;
    private final ProductLocationMappingRepository mappingRepository;

    // 전체 저장 (bulk)
    @Transactional
    public void saveAll(CustomPrincipal user, List<DisplayLocationDTO> list) {
        Integer storeId = resolveStoreId(user, null);

        for (DisplayLocationDTO dto : list) {
            String code = dto.getLocationCode();
            if (code == null || code.trim().isEmpty()) {
                throw new IllegalArgumentException("위치 코드는 비워둘 수 없습니다. '00'처럼 입력해주세요.");
            }
        }

        mappingRepository.deleteByStoreId(storeId);

        displayLocationRepository.deleteByStoreId(storeId);
        List<DisplayLocationEntity> entities = list.stream()
                .map(dto -> new DisplayLocationEntity(storeId, dto))
                .toList();
        displayLocationRepository.saveAll(entities);
    }


    // 조회
    public List<DisplayLocationDTO> findByStore(CustomPrincipal user, Integer requestStoreId) {
        Integer storeId = resolveStoreId(user, requestStoreId);
        List<DisplayLocationEntity> locations = displayLocationRepository.findByStoreId(storeId);
        return locations.stream()
                .map(entity -> {
                    DisplayLocationDTO dto = new DisplayLocationDTO();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).collect(Collectors.toList());
    }

    // storeId 권한 판단
    private Integer resolveStoreId(CustomPrincipal user, Integer requestStoreId) {
        if ("ROLE_MASTER".equals(user.getRole())) {
            if (requestStoreId == null) {
                throw new IllegalArgumentException("본사 관리자는 storeId를 명시해야 합니다.");
            }
            return requestStoreId;
        } else {
            return user.getStoreId();
        }
    }
}
