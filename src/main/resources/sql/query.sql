-- 1. 카테고리 계층 구조 조회
SELECT
    c1.category_name AS '대분류',
    c2.category_name AS '중분류',
    c3.category_name AS '소분류'
FROM category c1
         LEFT JOIN category c2 ON c2.parent_category_id = c1.category_id
         LEFT JOIN category c3 ON c3.parent_category_id = c2.category_id
WHERE c1.parent_category_id IS NULL
ORDER BY c1.category_id, c2.category_id, c3.category_id;

-- 2. 상품별 상세 정보 조회
SELECT
    p.product_id,
    p.pro_name,
    c.category_name,
    pd.manufacturer,
    pd.shelf_life,
    pd.storage_method,
    p.pro_cost,
    p.pro_sell_cost
FROM product p
         JOIN category c ON p.category_id = c.category_id
         JOIN product_details pd ON p.product_id = pd.product_id
ORDER BY p.product_id;

-- 3. 카테고리별 상품 수 및 평균 가격
SELECT
    c.category_name,
    COUNT(p.product_id) AS '상품 수',
    AVG(p.pro_sell_cost) AS '평균 판매가',
    MIN(p.pro_sell_cost) AS '최저가',
    MAX(p.pro_sell_cost) AS '최고가'
FROM category c
         LEFT JOIN product p ON c.category_id = p.category_id
GROUP BY c.category_id, c.category_name
ORDER BY COUNT(p.product_id) DESC;

-- 4. 유통기한 임박 상품 조회
SELECT
    p.product_id,
    p.pro_name,
    pd.shelf_life,
    pd.storage_method
FROM product p
         JOIN product_details pd ON p.product_id = pd.product_id
WHERE pd.shelf_life IS NOT NULL
ORDER BY pd.shelf_life;

-- 5. 알레르기 정보가 있는 상품 조회
SELECT
    p.product_id,
    p.pro_name,
    pd.allergens
FROM product p
         JOIN product_details pd ON p.product_id = pd.product_id
WHERE pd.allergens IS NOT NULL
ORDER BY p.product_id;

-- 6. 보관방법별 상품 수
SELECT
    pd.storage_method,
    COUNT(p.product_id) AS '상품 수'
FROM product p
         JOIN product_details pd ON p.product_id = pd.product_id
GROUP BY pd.storage_method
ORDER BY COUNT(p.product_id) DESC;

-- 7. 제조사별 상품 현황
SELECT
    pd.manufacturer,
    COUNT(p.product_id) AS '상품 수',
    GROUP_CONCAT(p.pro_name) AS '상품 목록'
FROM product p
         JOIN product_details pd ON p.product_id = pd.product_id
GROUP BY pd.manufacturer
ORDER BY COUNT(p.product_id) DESC;

-- 8. 판매가 대비 원가 비율이 높은 상품
SELECT
    product_id,
    pro_name,
    pro_cost,
    pro_sell_cost,
    ROUND((pro_sell_cost - pro_cost) / pro_cost * 100, 2) AS '마진율(%)'
FROM product
ORDER BY (pro_sell_cost - pro_cost) / pro_cost DESC;

-- 9. 카테고리별 재고 현황 (재고 통계와 연계)
SELECT
    c.category_name,
    SUM(ist.stock_value) AS '총 재고 가치',
    AVG(ist.turnover_rate) AS '평균 회전율',
    SUM(ist.low_stock_count) AS '재고 부족 상품 수'
FROM category c
         JOIN product p ON c.category_id = p.category_id
         JOIN inventory_statistics ist ON p.category_id = ist.category_id
GROUP BY c.category_id, c.category_name
ORDER BY SUM(ist.stock_value) DESC;

-- 10. 시간대별 판매 현황 (판매 통계와 연계)
SELECT
    DATE_FORMAT(ss.hour, '%H:00') AS '시간대',
    SUM(ss.total_sales) AS '총 매출',
    AVG(ss.avg_transaction) AS '평균 거래액',
    SUM(ss.transaction_count) AS '총 거래 건수'
FROM sales_statistics ss
GROUP BY DATE_FORMAT(ss.hour, '%H:00')
ORDER BY DATE_FORMAT(ss.hour, '%H:00');