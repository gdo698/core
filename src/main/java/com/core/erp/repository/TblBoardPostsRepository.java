// CORE-ERP-POS-Backend/src/main/java/com/core/erp/repository/TblBoardPostsRepository.java
package com.core.erp.repository;

import com.core.erp.domain.TblBoardPostsEntity;
import org.springframework.data.domain.Pageable;
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
    
    // 최근 게시글 4개 조회 (위젯용)
    List<TblBoardPostsEntity> findTop4ByOrderByBoardCreatedAtDesc();
    
    // 대시보드 위젯을 위한 모든 게시판에서 최근 게시글 n개 조회
    @Query("SELECT p FROM TblBoardPostsEntity p ORDER BY p.boardCreatedAt DESC")
    List<TblBoardPostsEntity> findRecentPostsForDashboard(Pageable pageable);
    
    // 대시보드 위젯을 위한 게시판 타입별 최근 게시글 조회
    @Query("SELECT p FROM TblBoardPostsEntity p WHERE p.boardType IN :boardTypes ORDER BY p.boardCreatedAt DESC")
    List<TblBoardPostsEntity> findRecentPostsByBoardTypes(@Param("boardTypes") List<Integer> boardTypes, Pageable pageable);
}