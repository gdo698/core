package com.core.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.core.erp.domain.SalesTransactionEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesAnalysisRepository extends JpaRepository<SalesTransactionEntity, Integer> {

    /**
     * 날짜별 매출 조회
     */
    @Query(value = "SELECT DATE(st.paid_at) as sale_date, " +
            "SUM(st.final_amount) as total_sales, " +
            "COUNT(st.transaction_id) as transaction_count " +
            "FROM sales_transaction st " +
            "WHERE st.paid_at BETWEEN :startDate AND :endDate " +
            "AND (:storeId IS NULL OR st.store_id = :storeId) " +
            "GROUP BY DATE(st.paid_at) " +
            "ORDER BY sale_date", nativeQuery = true)
    List<Object[]> findSalesByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("storeId") Integer storeId);

    /**
     * 지점별 매출 조회
     */
    @Query(value = "SELECT s.store_name as store_name, " +
            "SUM(st.final_amount) as total_sales, " +
            "COUNT(st.transaction_id) as transaction_count " +
            "FROM sales_transaction st " +
            "JOIN store s ON st.store_id = s.store_id " +
            "WHERE st.paid_at BETWEEN :startDate AND :endDate " +
            "GROUP BY st.store_id, s.store_name " +
            "ORDER BY total_sales DESC", nativeQuery = true)
    List<Object[]> findSalesByStore(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 시간대별 매출 조회
     */
    @Query(value = "SELECT HOUR(st.paid_at) as hour_of_day, " +
            "SUM(st.final_amount) as total_sales, " +
            "COUNT(st.transaction_id) as transaction_count " +
            "FROM sales_transaction st " +
            "WHERE st.paid_at BETWEEN :startDate AND :endDate " +
            "AND (:storeId IS NULL OR st.store_id = :storeId) " +
            "GROUP BY HOUR(st.paid_at) " +
            "ORDER BY hour_of_day", nativeQuery = true)
    List<Object[]> findSalesByHour(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("storeId") Integer storeId);

    /**
     * 카테고리별 매출 조회
     */
    @Query(value = "SELECT c.category_name, " +
            "SUM(sd.final_amount) as total_sales, " +
            "COUNT(sd.sales_detail_id) as sales_count " +
            "FROM sales_detail sd " +
            "JOIN sales_transaction st ON sd.transaction_id = st.transaction_id " +
            "JOIN product p ON sd.product_id = p.product_id " +
            "JOIN category c ON p.category_id = c.category_id " +
            "WHERE st.paid_at BETWEEN :startDate AND :endDate " +
            "AND (:storeId IS NULL OR st.store_id = :storeId) " +
            "GROUP BY c.category_id, c.category_name " +
            "ORDER BY total_sales DESC", nativeQuery = true)
    List<Object[]> findSalesByCategory(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("storeId") Integer storeId);

    /**
     * 연령대별 매출 조회
     */
    @Query(value = "SELECT st.age_group, " +
            "SUM(st.final_amount) as total_sales, " +
            "COUNT(st.transaction_id) as transaction_count " +
            "FROM sales_transaction st " +
            "WHERE st.paid_at BETWEEN :startDate AND :endDate " +
            "AND (:storeId IS NULL OR st.store_id = :storeId) " +
            "AND st.age_group IS NOT NULL " +
            "GROUP BY st.age_group " +
            "ORDER BY st.age_group", nativeQuery = true)
    List<Object[]> findSalesByAgeGroup(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("storeId") Integer storeId);

    /**
     * 성별별 매출 조회
     */
    @Query(value = "SELECT st.gender, " +
            "SUM(st.final_amount) as total_sales, " +
            "COUNT(st.transaction_id) as transaction_count " +
            "FROM sales_transaction st " +
            "WHERE st.paid_at BETWEEN :startDate AND :endDate " +
            "AND (:storeId IS NULL OR st.store_id = :storeId) " +
            "AND st.gender IS NOT NULL " +
            "AND st.gender IN (0, 1) " +
            "GROUP BY st.gender " +
            "ORDER BY st.gender", nativeQuery = true)
    List<Object[]> findSalesByGender(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("storeId") Integer storeId);
} 