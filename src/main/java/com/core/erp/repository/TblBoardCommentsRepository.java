// CORE-ERP-POS-Backend/src/main/java/com/core/erp/repository/TblBoardCommentsRepository.java
package com.core.erp.repository;

import com.core.erp.domain.TblBoardCommentsEntity;
import com.core.erp.domain.TblBoardPostsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TblBoardCommentsRepository extends JpaRepository<TblBoardCommentsEntity, Integer> {
    // 게시글 ID로 답변 조회
    List<TblBoardCommentsEntity> findByPostOrderByComCreatedAtDesc(TblBoardPostsEntity post);
    
    // 게시글 ID로 답변 존재 여부 확인
    boolean existsByPost(TblBoardPostsEntity post);
}