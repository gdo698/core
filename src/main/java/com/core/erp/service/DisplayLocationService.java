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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisplayLocationService {

    private final DisplayLocationRepository displayLocationRepository;
    private final ProductLocationMappingRepository mappingRepository;

    @Transactional
    public void saveAll(CustomPrincipal user, List<DisplayLocationDTO> newList) {
        Integer storeId = resolveStoreId(user, null);

        // 유효성 체크
        for (DisplayLocationDTO dto : newList) {
            validateLocationDTO(dto);
        }

        // 기존 데이터 조회
        List<DisplayLocationEntity> oldList = displayLocationRepository.findByStoreId(storeId);
        Map<Long, DisplayLocationEntity> oldMap = oldList.stream()
                .filter(e -> e.getLocationId() != null)
                .collect(Collectors.toMap(DisplayLocationEntity::getLocationId, e -> e));

        Set<Long> newIds = newList.stream()
                .map(DisplayLocationDTO::getLocationId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 삭제 대상: old 중 new에 없는 것
        List<DisplayLocationEntity> toDelete = oldList.stream()
                .filter(e -> !newIds.contains(e.getLocationId()))
                .collect(Collectors.toList());

        // 저장 or 수정
        for (DisplayLocationDTO dto : newList) {
            if (dto.getLocationId() == null) {
                DisplayLocationEntity newEntity = new DisplayLocationEntity();
                newEntity.setStoreId(storeId);
                applyChanges(newEntity, dto);
                displayLocationRepository.save(newEntity);
            } else {
                DisplayLocationEntity target = oldMap.get(dto.getLocationId());
                if (target != null) {
                    applyChanges(target, dto);
                }
            }
        }

        for (DisplayLocationEntity del : toDelete) {
            mappingRepository.deleteByLocationId(del.getLocationId());
            displayLocationRepository.deleteById(del.getLocationId());
        }
    }

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

    private void validateLocationDTO(DisplayLocationDTO dto) {
        String code = dto.getLocationCode();
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("위치 코드는 비워둘 수 없습니다. '00'처럼 입력해주세요.");
        }
    }

    private void applyChanges(DisplayLocationEntity entity, DisplayLocationDTO dto) {
        entity.setLocationCode(dto.getLocationCode());
        entity.setLabel(dto.getLabel());
        entity.setX(dto.getX());
        entity.setY(dto.getY());
        entity.setWidth(dto.getWidth());
        entity.setHeight(dto.getHeight());
        entity.setType(dto.getType());
    }

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
