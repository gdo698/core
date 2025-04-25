-- 1. 기본 테이블 (독립 테이블)
CREATE TABLE `department` (
                              `dept_id` INT NOT NULL COMMENT '부서 고유 번호',
                              `dept_name` varchar(30) NOT NULL COMMENT '부서명',
                              PRIMARY KEY (`dept_id`)
);

CREATE TABLE `store` (
                         `store_id` int NOT NULL COMMENT '매장고유번호',
                         `store_name` varchar(225) NOT NULL COMMENT '지점 이름',
                         `store_addr` varchar(225) NOT NULL COMMENT '지점 주소',
                         `store_tel` varchar(30) NOT NULL COMMENT '지점 연락처',
                         `store_created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '지점 등록 시간',
                         `store_cert` varchar(255) NOT NULL COMMENT '사업자등록증 이미지 url',
                         `store_acc` varchar(255) NOT NULL COMMENT '통장 사본 이미지 url',
                         PRIMARY KEY (`store_id`)
);

CREATE TABLE `category` (
                            `category_id` int NOT NULL AUTO_INCREMENT COMMENT '자동생성, 카테고리 id',
                            `category_name` VARCHAR(30) NOT NULL COMMENT '식품, 용품, 신선식품, 샌드위치...',
                            `category_filter` int NULL COMMENT '대분류1, 중분류2, 소분류3 - 레벨 구분용',
                            `parent_category_id` int NULL COMMENT '부모 카테고리 ID (최상위 카테고리는 NULL)',
                            PRIMARY KEY (`category_id`),
                            CONSTRAINT `fk_category_parent` FOREIGN KEY (`parent_category_id`)
                            REFERENCES `category` (`category_id`)
                            ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `ai_model` (
                            `model_id` INT NOT NULL COMMENT '모델 ID',
                            `name` VARCHAR(100) NOT NULL COMMENT '모델 이름',
                            `type` VARCHAR(50) NOT NULL COMMENT '모델 유형',
                            `version` VARCHAR(20) NOT NULL COMMENT '버전',
                            `parameters` JSON NULL COMMENT '모델 파라미터',
                            `accuracy` DECIMAL(5,2) NULL COMMENT '정확도',
                            `training_date` DATETIME NULL COMMENT '학습 날짜',
                            `is_active` BOOLEAN NULL DEFAULT TRUE COMMENT '활성화 여부',
                            `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
                            `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '수정 시간',
                            PRIMARY KEY (`model_id`)
);

CREATE TABLE `weather_data` (
                                `weather_id` INT NOT NULL COMMENT '날씨 데이터 ID',
                                `location` VARCHAR(100) NOT NULL COMMENT '위치',
                                `date` DATE NOT NULL COMMENT '날짜',
                                `temperature` DECIMAL(5,2) NOT NULL COMMENT '평균 온도',
                                `condition` VARCHAR(50) NOT NULL COMMENT '날씨 상태',
                                `humidity` INT NULL COMMENT '습도',
                                `precipitation` DECIMAL(5,2) NULL COMMENT '강수량',
                                `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
                                PRIMARY KEY (`weather_id`)
);

-- 2. 1차 의존성 테이블
CREATE TABLE `employee` (
                            `emp_id` INT NOT NULL COMMENT '사원 고유 번호',
                            `store_id` INT NULL COMMENT '매장고유번호',
                            `depart_id` INT NULL COMMENT '부서 고유번호',
                            `emp_name` VARCHAR(30) NOT NULL COMMENT '사원 이름',
                            `emp_role` varchar(30) NOT NULL COMMENT '사원 직급(ROLE)',
                            `emp_gender` INT NOT NULL COMMENT '성별',
                            `emp_phone` VARCHAR(30) NOT NULL COMMENT '사원전화번호',
                            `emp_addr` VARCHAR(30) NOT NULL COMMENT '사원 주소',
                            `emp_birth` VARCHAR(30) NOT NULL COMMENT '생년월일',
                            `login_id` VARCHAR(30) NOT NULL UNIQUE COMMENT '사원 계정 ID',
                            `login_pwd` VARCHAR(30) NOT NULL COMMENT '사원 계정 비밀번호',
                            `emp_img` VARCHAR(255) NULL COMMENT '사원 프로필 사진',
                            `emp_bank` ENUM('국민', '우리', '신한', '농협') NOT NULL COMMENT '급여 은행명',
                            `emp_acount` VARCHAR(30) NOT NULL COMMENT '급여 계좌번호',
                            `emp_status` VARCHAR(30) NOT NULL COMMENT '근무 상태',
                            `hire_date` DATETIME NOT NULL COMMENT '회원 입사일',
                            `work_type` int NOT NULL COMMENT '1.정규직 2.계약직 3.점주',
                            `email_auth` BOOLEAN NULL COMMENT '이메일 인증 완료 여부',
                            `emp_ext` INT NULL COMMENT '사무실 내선 번호',
                            PRIMARY KEY (`emp_id`),
                            FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`),
                            FOREIGN KEY (`depart_id`) REFERENCES `department` (`dept_id`)
);

CREATE TABLE `product` (
                           `product_id` int NOT NULL COMMENT 'autoincrement',
                           `category_id` int NOT NULL COMMENT '자동생성 , 카테고리 id',
                           `pro_name` varchar(255) NOT NULL COMMENT '제품 이름',
                           `pro_barcode` bigint NOT NULL COMMENT '바코드 넘버',
                           `pro_cost` int NOT NULL COMMENT '원가',
                           `pro_sell_cost` int NOT NULL COMMENT '판매가',
                           `pro_created_at` DATETIME NOT NULL COMMENT '생성했을때 시각',
                           `pro_update_at` DATETIME NULL COMMENT '수정했을때 시각',
                           `pro_image` varchar(225) NULL COMMENT '이미지 링크',
                           `is_promo`	int	NULL	COMMENT '0기본 1 이벤트 2 이벤트',
                           PRIMARY KEY (`product_id`),
                           FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
);

-- 3. 2차 의존성 테이블
CREATE TABLE `email_token` (
                               `etoken_id` int NOT NULL COMMENT '고유 토큰 번호',
                               `emp_id` int NOT NULL COMMENT '고유번호',
                               `token` varchar(100) NOT NULL COMMENT '이메일로 발송한 인증 토큰',
                               `expire_at` datetime NOT NULL COMMENT '유효 기간',
                               `is_used` boolean NOT NULL DEFAULT false COMMENT '인증 사용 여부',
                               `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '토큰 생성 시각',
                               PRIMARY KEY (`etoken_id`),
                               FOREIGN KEY (`emp_id`) REFERENCES `employee` (`emp_id`)
);

CREATE TABLE `pw_reset_token` (
                                  `prtoken_id` int NOT NULL COMMENT '고유 토큰 번호',
                                  `emp_id` int NOT NULL COMMENT '고유번호',
                                  `reset_token` varchar(100) NOT NULL COMMENT '이메일로 발송한 임시 토큰',
                                  `expire_at` datetime NOT NULL COMMENT '유효 시간',
                                  `is_used` boolean NOT NULL DEFAULT false COMMENT '재설정 링크 사용여부',
                                  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '토큰 발급 시간',
                                  PRIMARY KEY (`prtoken_id`),
                                  FOREIGN KEY (`emp_id`) REFERENCES `employee` (`emp_id`)
);

CREATE TABLE `store_stock` (
                               `stock_id` int NOT NULL COMMENT '재고넘버',
                               `store_id` int NOT NULL COMMENT '매장 고유번호',
                               `product_id` int NOT NULL COMMENT '상품 고유번호',
                               `quantity` int NOT NULL COMMENT '재고 갯수',
                               `last_in_date` DATETIME NULL COMMENT '입고 완료 날짜',
                               `stock_status` int NOT NULL DEFAULT 1 COMMENT '1. 정상 2. 유통기한 임박 3. 유통기한경과 4.재고부족 5. 입고예정 7.폐기 대기',
                               PRIMARY KEY (`stock_id`),
                               FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`),
                               FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
);

CREATE TABLE `purchase_order` (
                                  `order_id` int NOT NULL COMMENT '발주 고유 번호',
                                  `store_id` int NOT NULL COMMENT '매장고유번호',
                                  `order_date` DATETIME NOT NULL COMMENT '발주한 등록 시간',
                                  `oreder_status` int NOT NULL COMMENT '1.대기,2입고대기 3.완료',
                                  `total_amount` int NOT NULL COMMENT '발주 총 금액',
                                  `total_quantity` int NOT NULL COMMENT '발주 총 갯수',
                                  PRIMARY KEY (`order_id`),
                                  FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`)
);

CREATE TABLE `purchase_order_item` (
                                       `item_id` int NOT NULL COMMENT '발주 개별코드',
                                       `order_id` int NOT NULL COMMENT '발주 고유 번호',
                                       `product_id` int NOT NULL COMMENT '상품번호',
                                       `order_quantity` int NOT NULL COMMENT '발주 수량',
                                       `unit_price` int NOT NULL COMMENT '발주 단가',
                                       `total_price` int NOT NULL COMMENT '발주 수량 *단가',
                                       `order_state` int NOT NULL COMMENT '1.발주완료 2. 발주 취소 3.입고대기 4. 입고완료',
                                       `is_abnormal` boolean NOT NULL DEFAULT false COMMENT '오발주 감지여부',
                                       `is_fully_received` boolean NOT NULL DEFAULT false COMMENT '입고여부',
                                       `received_quantity` int NULL COMMENT '실제 입고수량',
                                       PRIMARY KEY (`item_id`),
                                       FOREIGN KEY (`order_id`) REFERENCES `purchase_order` (`order_id`),
                                       FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
);

CREATE TABLE `disposal` (
                            `disposal_id` int NOT NULL COMMENT '폐기넘버',
                            `stock_id` int NOT NULL COMMENT '재고넘버',
                            `disposal_date` DATETIME NOT NULL COMMENT '폐기날짜',
                            `quantity` int NOT NULL COMMENT '폐기 수량',
                            `processed_by` varchar(30) NOT NULL COMMENT '폐기한 사람',
                            `total_loss_amount` int NOT NULL COMMENT '폐기 총 금액',
                            `reason` varchar(30) NOT NULL DEFAULT '유통기한만료' COMMENT '폐기사유',
                            PRIMARY KEY (`disposal_id`),
                            FOREIGN KEY (`stock_id`) REFERENCES `store_stock` (`stock_id`)
);

CREATE TABLE `sales` (
                         `sales_id` int NOT NULL COMMENT '개별 매출 고유ID',
                         `store_id` int NOT NULL COMMENT '매장고유번호',
                         `emp_id` int NOT NULL COMMENT '고유번호',
                         `product_id` int NOT NULL COMMENT 'autoincrement',
                         `total_sales` int NOT NULL DEFAULT 0 COMMENT '정가 기준 총 매출(할인 전)',
                         `payment_method` varchar(20) NOT NULL COMMENT '현금, 카드 등',
                         `sale_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '판매가 일어난 시간',
                         `quantity` int NOT NULL DEFAULT 1 COMMENT '판매수량',
                         `is_refunded` boolean NOT NULL DEFAULT false COMMENT 'true : 환불됨',
                         `discount_price` int NOT NULL DEFAULT 0 COMMENT '할인된 금액',
                         `created_at` datetime NOT NULL COMMENT '데이터 기록 시간',
                         `final_amount` int NOT NULL DEFAULT 0 COMMENT '실제 결제된 금액(정가 - 할인)',
                         `cost_price` int NOT NULL DEFAULT 0 COMMENT '상품의 원가',
                         `real_income` int NOT NULL DEFAULT 0 COMMENT '점주가 실제 수령하는 금액',
                         `is_settled` boolean NOT NULL DEFAULT false COMMENT 'true : 정산 처리 완료',
                         `transaction_id` int NULL COMMENT 'POS 한 건의 거래 ID',
                         PRIMARY KEY (`sales_id`),
                         FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`),
                         FOREIGN KEY (`emp_id`) REFERENCES `employee` (`emp_id`),
                         FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
);

CREATE TABLE `sales_hourly` (
                                `sales_hourly_id` int NOT NULL COMMENT '시간대별 매출통계 고유번호',
                                `store_id` int NOT NULL COMMENT '매장고유번호',
                                `sale_date` date NOT NULL COMMENT '기준 날짜',
                                `hour` tinyint NOT NULL COMMENT '0-23시(24시간 기준)',
                                `quantity` int NOT NULL DEFAULT 0 COMMENT '해당 시간에 판매된 수량',
                                `total_sales` int NOT NULL DEFAULT 0 COMMENT '해당 시간대 총 매출액',
                                `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '데이터 생성 시간',
                                PRIMARY KEY (`sales_hourly_id`),
                                FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`)
);

CREATE TABLE `sales_stats` (
                               `sales_stats_id` int NOT NULL COMMENT '통계 고유 번호',
                               `store_id` int NOT NULL COMMENT '매장고유번호',
                               `product_id` int NOT NULL COMMENT 'autoincrement',
                               `sale_date` date NOT NULL COMMENT '해당 매출 날짜',
                               `quantity` int NOT NULL DEFAULT 0 COMMENT '해당 날짜에 판매된 수량',
                               `total_sales` int NOT NULL DEFAULT 0 COMMENT '해당 날짜 매출 총액',
                               `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '데이터 생성 시간',
                               PRIMARY KEY (`sales_stats_id`),
                               FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`),
                               FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
);

CREATE TABLE `sales_statistics` (
                                    `stats_id` INT NOT NULL COMMENT '통계 ID',
                                    `store_id` int NOT NULL COMMENT '매장고유번호',
                                    `category_id` int NOT NULL COMMENT '자동생성 , 카테고리 id',
                                    `date` DATE NOT NULL COMMENT '날짜',
                                    `hour` DATETIME NULL COMMENT '시간 (시간별 통계 시)',
                                    `total_sales` DECIMAL(10,2) NOT NULL COMMENT '총 매출액',
                                    `transaction_count` INT NOT NULL COMMENT '거래 건수',
                                    `avg_transaction` DECIMAL(10,2) NOT NULL COMMENT '평균 거래액',
                                    `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
                                    PRIMARY KEY (`stats_id`),
                                    FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`),
                                    FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
);

CREATE TABLE `inventory_statistics` (
                                        `stats_id` INT NOT NULL COMMENT '통계 ID',
                                        `store_id` int NOT NULL COMMENT '매장고유번호',
                                        `category_id` int NOT NULL COMMENT '자동생성 , 카테고리 id',
                                        `date` DATE NOT NULL COMMENT '날짜',
                                        `turnover_rate` DECIMAL(5,2) NOT NULL COMMENT '재고 회전율',
                                        `stock_value` DECIMAL(10,2) NOT NULL COMMENT '재고 가치',
                                        `low_stock_count` INT NOT NULL COMMENT '부족 재고 상품 수',
                                        `excess_stock_count` INT NOT NULL COMMENT '과잉 재고 상품 수',
                                        `expired_soon_count` INT NOT NULL COMMENT '유통기한 임박 상품 수',
                                        `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
                                        PRIMARY KEY (`stats_id`),
                                        FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`),
                                        FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
);

CREATE TABLE `order_stats` (
                               `ostats_id` int NOT NULL COMMENT '발주 통계 고유 번호',
                               `store_id` int NOT NULL COMMENT '매장고유번호',
                               `product_id` int NOT NULL COMMENT 'autoincrement',
                               `order_date` date NOT NULL COMMENT '발주한 날짜',
                               `quantity` int NOT NULL DEFAULT 0 COMMENT '발주된 수량',
                               `total_price` int NOT NULL DEFAULT 0 COMMENT '총 발주된 금액',
                               `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '데이터 생성 시간',
                               PRIMARY KEY (`ostats_id`),
                               FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`),
                               FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
);

CREATE TABLE `demand_prediction` (
                                     `prediction_id` INT NOT NULL COMMENT '예측 ID',
                                     `store_id` int NOT NULL COMMENT '매장고유번호',
                                     `product_id` int NOT NULL COMMENT 'autoincrement',
                                     `date` DATE NOT NULL COMMENT '예측 날짜',
                                     `predicted_quantity` INT NOT NULL COMMENT '예측 수량',
                                     `confidence_level` DECIMAL(5,2) NOT NULL COMMENT '신뢰도 (0-1)',
                                     `weather_factor` DECIMAL(5,2) NULL COMMENT '날씨 요인',
                                     `seasonal_factor` DECIMAL(5,2) NULL COMMENT '계절 요인',
                                     `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
                                     PRIMARY KEY (`prediction_id`),
                                     FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`),
                                     FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
);

CREATE TABLE `anomaly_detection` (
                                     `anomaly_id` INT NOT NULL COMMENT '이상 ID',
                                     `store_id` int NOT NULL COMMENT '매장고유번호',
                                     `type` VARCHAR(50) NOT NULL COMMENT '이상 유형',
                                     `detection_time` DATETIME NOT NULL COMMENT '탐지 시간',
                                     `severity` INT NOT NULL COMMENT '심각도 (1-5)',
                                     `description` TEXT NOT NULL COMMENT '설명',
                                     `is_resolved` BOOLEAN NULL DEFAULT FALSE COMMENT '해결 여부',
                                     `resolution_notes` TEXT NULL COMMENT '해결 노트',
                                     `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
                                     `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '수정 시간',
                                     PRIMARY KEY (`anomaly_id`),
                                     FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`)
);

CREATE TABLE `issue_log` (
                             `issue_id` int NOT NULL COMMENT '개별 이슈 고유 ID',
                             `product_id` int NULL COMMENT 'autoincrement',
                             `store_id` int NOT NULL COMMENT '매장고유번호',
                             `issue_title` varchar(50) NOT NULL COMMENT 'ex) 재고부족, 발주지연',
                             `issue_desc` varchar(225) NOT NULL COMMENT '이슈 설명',
                             `issue_type` varchar(30) NOT NULL COMMENT '재고, 유통기한, 발주 등',
                             `created_at` datetime NOT NULL COMMENT '이슈가 등록된 시간',
                             PRIMARY KEY (`issue_id`),
                             FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`),
                             FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
);

CREATE TABLE `annual_leave` (
                                `leave_id` INT NOT NULL COMMENT '연차번호',
                                `emp_id` INT NOT NULL COMMENT '사원 고유 번호',
                                `year` INT NOT NULL COMMENT '근속 연도',
                                `total_days` INT NOT NULL COMMENT '보유 연차 총합',
                                `used_days` INT NOT NULL COMMENT '사용한연차',
                                `rem_days` INT NULL COMMENT '잔여연차 개수',
                                `uadate_at` DATETIME NULL COMMENT '최근 수정 날짜',
                                PRIMARY KEY (`leave_id`),
                                FOREIGN KEY (`emp_id`) REFERENCES `employee` (`emp_id`)
);

CREATE TABLE `leave_req` (
                             `req_id` INT NOT NULL COMMENT '연차신청고유번호',
                             `emp_id` INT NOT NULL COMMENT '사원 고유 번호',
                             `req_date` DATE NOT NULL COMMENT '요청날짜',
                             `req_reason` VARCHAR(255) NULL COMMENT '연차사유',
                             `req_status` INT NULL DEFAULT 1 COMMENT '현재 상태',
                             `created_at` DATETIME NULL COMMENT '생성날짜',
                             PRIMARY KEY (`req_id`),
                             FOREIGN KEY (`emp_id`) REFERENCES `employee` (`emp_id`)
);

CREATE TABLE `appr_line` (
                             `line_id` INT NOT NULL COMMENT '결재자고유번호',
                             `emp_id` INT NOT NULL COMMENT '사원 고유번호',
                             `req_id` INT NOT NULL COMMENT '연차신청고유번호',
                             `appr_order` INT NOT NULL COMMENT '결재순서',
                             `is_delegate` BOOLEAN NOT NULL COMMENT '대결자여부',
                             PRIMARY KEY (`line_id`),
                             FOREIGN KEY (`emp_id`) REFERENCES `employee` (`emp_id`),
                             FOREIGN KEY (`req_id`) REFERENCES `leave_req` (`req_id`)
);

CREATE TABLE `appr_log` (
                            `log_id` INT NOT NULL COMMENT '로그 고유번호',
                            `req_id` INT NOT NULL COMMENT '연차신청고유번호',
                            `emp_id` INT NOT NULL COMMENT '사원고유번호',
                            `appr_status` INT NOT NULL COMMENT '1.승인2. 반려3. 대기',
                            `appr_at` DATETIME NOT NULL COMMENT '결제시간',
                            `note` VARCHAR(255) NULL COMMENT '사유',
                            PRIMARY KEY (`log_id`),
                            FOREIGN KEY (`req_id`) REFERENCES `leave_req` (`req_id`),
                            FOREIGN KEY (`emp_id`) REFERENCES `employee` (`emp_id`)
);

CREATE TABLE `dashboard_layout` (
                                    `layout_id` INT NOT NULL COMMENT '레이아웃 ID',
                                    `emp_id` int NOT NULL COMMENT '고유번호',
                                    `widget_code` VARCHAR(30) NOT NULL COMMENT '위젯 코드',
                                    `grid_positions` VARCHAR(50) NOT NULL COMMENT '위치 번호들 (쉼표로 구분, 예: "1,2,6,7")',
                                    `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
                                    `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '수정 시간',
                                    PRIMARY KEY (`layout_id`),
                                    FOREIGN KEY (`emp_id`) REFERENCES `employee` (`emp_id`)
);

CREATE TABLE `tbl_board_posts` (
                                   `post_id` INT NOT NULL COMMENT '게시글 고유 번호 (기본 키)',
                                   `emp_id` int NOT NULL COMMENT '사원 고유 번호',
                                   `board_type` INT NOT NULL COMMENT '공지시항 , 건의사항, 점포문의 분리코드',
                                   `title` VARCHAR(255) NOT NULL COMMENT '게시글 제목',
                                   `content` VARCHAR(255) NOT NULL COMMENT '게시글 본문 내용',
                                   `created_at` DATETIME NOT NULL COMMENT '게시글 작성 시간',
                                   PRIMARY KEY (`post_id`),
                                   FOREIGN KEY (`emp_id`) REFERENCES `employee` (`emp_id`)
);

CREATE TABLE `tbl_board_comments` (
                                      `comment_id` INT NOT NULL COMMENT '답변 고유 번호 (기본 키)',
                                      `post_id` INT NOT NULL COMMENT '게시글 고유 번호 (기본 키)',
                                      `content` VARCHAR(255) NOT NULL COMMENT '답변 본문 내용',
                                      `created_at` DATETIME NOT NULL COMMENT '답변 작성 시간',
                                      PRIMARY KEY (`comment_id`),
                                      FOREIGN KEY (`post_id`) REFERENCES `tbl_board_posts` (`post_id`)
);

CREATE TABLE `part_timer` (
                              `part_timer_id`	int	NOT NULL	COMMENT '아르바이트 고유 ID',
                              `store_id`	int	NOT NULL	COMMENT '매장고유번호',
                              `name`	VARCHAR(50)	NOT NULL	COMMENT '이름',
                              `gender`	TINYINT	NOT NULL	COMMENT '성별 (1: 남자, 2: 여자)',
                              `phone`	VARCHAR(30)	NOT NULL	COMMENT '연락처',
                              `addres`	varchar(50)	NOT NULL	COMMENT '주소',
                              `birth_date`	DATE	NOT NULL	COMMENT '생년월일',
                              `hire_date`	DATETIME	NOT NULL	COMMENT '입사일',
                              `resign_date`	DATETIME	NULL	COMMENT '퇴사일',
                              `salary_type`	TINYINT	NOT NULL	COMMENT '급여 형태 (1: 시급, 2: 월급)',
                              `hourly_wage`	INT	NULL	COMMENT '시급 (시급제일 경우 필수)',
                              `account_bank`	VARCHAR(30)	NOT NULL	COMMENT '급여 은행명',
                              `account_number`	VARCHAR(30)	NOT NULL	COMMENT '급여 계좌번호',
                              `part_status`	TINYINT	NOT NULL	DEFAULT 1	COMMENT '재직 상태 (1: 재직, 2: 퇴사, 3: 휴직)',
                              `created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '등록 시각',
                              PRIMARY KEY (`part_timer_id`),
                              FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`)

);

CREATE TABLE `shift_schedule` (
                                  `schedule_id` int NOT NULL COMMENT '스케줄 고유 번호',
                                  `part_timer_id`	int	NOT NULL	COMMENT '아르바이트 고유 ID',
                                  `work_date` DATETIME NOT NULL COMMENT '근무시작일',
                                  `start_time` DATETIME NOT NULL COMMENT '근무 시작시간',
                                  `end_time` DATETIME NOT NULL COMMENT '근무 마감시간',
                                  PRIMARY KEY (`schedule_id`),
                                  FOREIGN KEY (`part_timer_id`) REFERENCES `part_timer` (`part_timer_id`)
);

CREATE TABLE `Salary` (
                          `salary_id` INT NOT NULL COMMENT '급여 고유 번호',
                          `emp_id` INT NULL COMMENT '사원 고유 번호',
                          `part_timer_id`	int	NULL	COMMENT '아르바이트 고유 ID',
                          `store_id`	int NULL	COMMENT '매장고유번호',
                          `calculated_at` DATETIME NOT NULL COMMENT '정산날짜',
                          `base_salary` INT NOT NULL COMMENT '기본급 (월급 or 시급 * 총 근무시간)',
                          `bonus` INT NOT NULL COMMENT '상여급',
                          `deduct_total` INT NOT NULL COMMENT '공제 금액 (4대보험, 지각/결근 등)',
                          `deduct_extra` INT NULL COMMENT '기타공제금액',
                          `net_salary` INT NOT NULL COMMENT '실 수령액',
                          `pay_date` DATETIME NOT NULL COMMENT '급여일자',
                          `pay_status` INT NULL COMMENT '1.지급대기 2. 지급완료',
                          PRIMARY KEY (`salary_id`),
                          FOREIGN KEY (`emp_id`) REFERENCES `employee` (`emp_id`),
                          FOREIGN KEY (`part_timer_id`) REFERENCES `part_timer` (`part_timer_id`),
                          FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`)

);
CREATE TABLE `attendance` (
                              `attend_id` INT NOT NULL COMMENT '근태 기록 고유번호',
                              `emp_id` INT NULL COMMENT '사원 고유 번호',
                              `leave_id` INT NULL COMMENT '연차번호',
                              `part_timer_id`	int	NULL	COMMENT '아르바이트 고유 ID',
                              `store_id`	int NULL	COMMENT '매장고유번호',
                              `work_date` DATETIME NOT NULL COMMENT '근무일자',
                              `in_time` DATETIME NOT NULL COMMENT '출근시간',
                              `out_time` DATETIME NULL COMMENT '퇴근시간',
                              `attend_status` INT NOT NULL COMMENT '1: 출근, 2: 지각, 3: 조퇴, 4: 결근, 5: 연차, 6: 병가',
                              PRIMARY KEY (`attend_id`),
                              FOREIGN KEY (`emp_id`) REFERENCES `employee` (`emp_id`),
                              FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`),
                              FOREIGN KEY (`part_timer_id`) REFERENCES `part_timer` (`part_timer_id`),
                              FOREIGN KEY (`leave_id`) REFERENCES `annual_leave` (`leave_id`)
);

CREATE TABLE `product_details` (
                                   `pro_detail_id`	INT	NOT NULL AUTO_INCREMENT,
                                   `product_id`	int	NOT NULL	COMMENT 'autoincrement',
                                   `manufacturer`	VARCHAR(100)	NOT NULL	COMMENT '제조사',
                                   `manu_num`	VARCHAR(30)	NULL	COMMENT '제조사번호',
                                   `shelf_life`	VARCHAR(50)	NULL	COMMENT '제조일로부터 12개월..',
                                   `allergens`	VARCHAR(255)	NULL	COMMENT '알러지',
                                   `storage_method`	VARCHAR(100)	NULL	COMMENT '보관방법, 냉동냉장,..',
                                   PRIMARY KEY (`pro_detail_id`),
                                   FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
);

CREATE TABLE `stock_in_history` (
                                    `history_id` INT NOT NULL COMMENT '입고고유번호',
                                    `store_id` int NOT NULL COMMENT '매장고유번호',
                                    `part_timer_id`	int	NOT NULL	COMMENT '아르바이트 고유 ID',
                                    `product_id` int NOT NULL COMMENT '상품고유번호',
                                    `order_id` int NOT NULL COMMENT '발주 고유 번호',
                                    `in_quantity` INT NOT NULL COMMENT '입고수량',
                                    `in_date` DATETIME NOT NULL COMMENT '입고날짜',
                                    `expire_date` DATETIME NULL COMMENT '유통기한',
                                    `history_status` INT NOT NULL DEFAULT 1 COMMENT '1. 입고대기 2. 입고완료 3. 부분입고 4. 오입고 5. 폐기 6. 반품',
                                    PRIMARY KEY (`history_id`),
                                    FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`),
                                    FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
                                    FOREIGN KEY (`order_id`) REFERENCES `purchase_order` (`order_id`),
                                    FOREIGN KEY (`part_timer_id`) REFERENCES `part_timer` (`part_timer_id`)
);
