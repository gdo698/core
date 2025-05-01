// CORE-ERP-POS-Backend/src/main/java/com/core/erp/repository/TblBoardPostsRepository.java
package com.core.erp.repository;

import com.core.erp.domain.TblBoardPostsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TblBoardPostsRepository extends JpaRepository<TblBoardPostsEntity, Integer> {
    // 게시판 타입별 조회
    List<TblBoardPostsEntity> findByBoardTypeOrderByBoardCreatedAtDesc(int boardType);
    
    // 답변 유무를 포함한 조회
    @Query("SELECT p, CASE WHEN COUNT(c) > 0 THEN true ELSE false END AS hasComment " +
           "FROM TblBoardPostsEntity p LEFT JOIN TblBoardCommentsEntity c ON p.postId = c.post.postId " +
           "WHERE p.boardType = :boardType " +
           "GROUP BY p.postId " +
           "ORDER BY p.boardCreatedAt DESC")
    List<Object[]> findByBoardTypeWithCommentStatus(@Param("boardType") int boardType);
}