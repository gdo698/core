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

-- 11. 특정 지점의 스케줄 전체 조회

SELECT * FROM shift_schedule a
         Join part_timer b on a.part_timer_id =b.part_timer_id
         WHERE store_id = 1;

-- 12. 특정 알바의 스케줄 조회

-- 인사

-- 1. 사원 전체 조회
SELECT
    e.emp_id,
    e.emp_name,
    d.dept_name,
    e.emp_phone,
    e.work_type,
    e.hire_date,
    e.emp_status
FROM employee e
         LEFT JOIN department d ON e.depart_id = d.dept_id
ORDER BY e.emp_id;

-- 2. 사원 세부 조회(필터 조건 설정)
-- 부서별, 매장별, 근무형태별 필터 예시
SELECT
    e.emp_id,
    e.emp_name,
    d.dept_name,
    e.work_type,
    e.emp_status
FROM employee e
         LEFT JOIN department d ON e.depart_id = d.dept_id
WHERE
    d.dept_id = 3  -- 특정 사원 조회(emp_id : 1-10까지 입력 가능)
  AND e.work_type = '정규직'  -- 근무형태별 조회
ORDER BY e.emp_id;

-- 3. 사원 상세 정보 조회
SELECT
    e.*,
    d.dept_name,
    CONCAT(YEAR(CURDATE()) - YEAR(e.hire_date), '년') AS 근속연수,
    al.total_days AS 연차총일수,
    al.rem_days AS 잔여연차
FROM employee e
         LEFT JOIN department d ON e.depart_id = d.dept_id
         LEFT JOIN annual_leave al ON e.emp_id = al.emp_id AND al.year = YEAR(CURDATE())
WHERE e.emp_id = 1;  -- 특정 사원 조회(emp_id : 1-10까지 입력 가능)

-- 3. 사원 정보 수정
UPDATE employee
SET
    emp_name = '홍길동',
    emp_phone = '010-1234-5678',
    emp_addr = '서울시 강남구 역삼동',
    emp_bank = '국민',
    emp_acount = '123-456-789',
    emp_status = '1',  -- 1: 재직
    emp_ext = 1234     -- 내선번호
WHERE emp_id = 1;

-- 4. 연차 신청
INSERT INTO leave_req (req_id, emp_id, req_date, req_reason, req_status, created_at)
VALUES (11, 1, '2024-07-15', '개인 사유', '1', NOW());

-- 결재선 추가 (결재자 설정)
INSERT INTO appr_line (line_id, emp_id, req_id, appr_order, is_delegate)
VALUES (11, 5, 11, 1, FALSE);  -- 5번 사원을 결재자로 지정, 새로운 req_id 사용

-- 5. 연차 승인
-- 특정 연차 신청을 승인하는 쿼리
UPDATE leave_req
SET req_status = '승인'  -- '승인', '반려', '대기' 등의 상태
WHERE req_id = 11;

-- 승인 로그 기록
INSERT INTO appr_log (log_id, req_id, emp_id, appr_status, appr_at, note)
VALUES (11, 11, 5, 1, NOW(), '승인합니다');  -- 새로운 log_id와 req_id 사용

-- 연차 사용 기록 업데이트 (승인되면 사용한 연차 증가, 잔여 연차 감소)
UPDATE annual_leave
SET
    used_days = used_days + 1,
    rem_days = total_days - used_days - 1,
    uadate_at = NOW()
WHERE emp_id = (SELECT emp_id FROM leave_req WHERE req_id = 11)
  AND year = YEAR(CURDATE());

-- 관리자용 연차 신청 목록 조회
-- 검색 필터 넣을지 말지 아직 미정
SELECT
    lr.req_id,
    e.emp_id,
    e.emp_name,
    d.dept_name,
    lr.req_date AS 신청연차일자,
    lr.req_reason AS 연차사유,
    lr.req_status AS 승인상태,
    lr.created_at AS 신청일시,
    al.rem_days AS 잔여연차,
    CASE
        WHEN lr.req_status = '대기' THEN '승인 대기중'
        WHEN lr.req_status = '승인' THEN '승인 완료'
        WHEN lr.req_status = '반려' THEN '반려됨'
        ELSE lr.req_status
        END AS 상태설명,
    appr.emp_name AS 결재자
FROM leave_req lr
         JOIN employee e ON lr.emp_id = e.emp_id
         LEFT JOIN department d ON e.depart_id = d.dept_id
         LEFT JOIN annual_leave al ON e.emp_id = al.emp_id AND al.year = YEAR(CURDATE())
         LEFT JOIN (
    SELECT
        al.req_id,
        e.emp_name
    FROM appr_line al
             JOIN employee e ON al.emp_id = e.emp_id
    WHERE al.appr_order = 1
) appr ON lr.req_id = appr.req_id
WHERE
    1=1  -- 필터 조건이 없을 때도 쿼리가 동작하도록
ORDER BY lr.created_at;  -- 최근 신청 순으로 정렬






