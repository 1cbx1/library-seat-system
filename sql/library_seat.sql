CREATE DATABASE IF NOT EXISTS library_seat DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE library_seat;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS sys_log;
DROP TABLE IF EXISTS violation_record;
DROP TABLE IF EXISTS sensitive_word;
DROP TABLE IF EXISTS message_board;
DROP TABLE IF EXISTS forum_collect;
DROP TABLE IF EXISTS forum_comment;
DROP TABLE IF EXISTS forum_post;
DROP TABLE IF EXISTS banner;
DROP TABLE IF EXISTS announcement;
DROP TABLE IF EXISTS evaluation;
DROP TABLE IF EXISTS check_out;
DROP TABLE IF EXISTS check_in;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS seat;
DROP TABLE IF EXISTS study_room;
DROP TABLE IF EXISTS sys_user;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE sys_user (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) NOT NULL COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码',
  real_name VARCHAR(50) COMMENT '真实姓名',
  phone VARCHAR(20) COMMENT '手机号',
  email VARCHAR(100) COMMENT '邮箱',
  avatar VARCHAR(255) DEFAULT '/images/default-avatar.png' COMMENT '头像',
  role VARCHAR(20) DEFAULT 'user' COMMENT '角色：admin/user',
  credit_score INT DEFAULT 100 COMMENT '信用分',
  status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1正常',
  deleted TINYINT DEFAULT 0 COMMENT '删除标记',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE study_room (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '自习室ID',
  room_code VARCHAR(20) NOT NULL COMMENT '自习室编号',
  room_name VARCHAR(100) NOT NULL COMMENT '自习室名称',
  location VARCHAR(100) COMMENT '位置',
  floor VARCHAR(20) COMMENT '楼层',
  capacity INT DEFAULT 0 COMMENT '座位容量',
  hour_price DECIMAL(10,2) DEFAULT 0.00 COMMENT '小时单价',
  open_time VARCHAR(50) DEFAULT '08:00-22:00' COMMENT '开放时间',
  status TINYINT DEFAULT 1 COMMENT '状态：0关闭 1开放',
  image_url VARCHAR(255) COMMENT '图片',
  description TEXT COMMENT '描述',
  deleted TINYINT DEFAULT 0 COMMENT '删除标记',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_room_code (room_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自习室表';

CREATE TABLE seat (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '座位ID',
  seat_code VARCHAR(20) NOT NULL COMMENT '座位编号',
  room_id BIGINT NOT NULL COMMENT '所属自习室ID',
  status TINYINT DEFAULT 1 COMMENT '状态：1可预约 2已占用 3已预约 4维修/锁定',
  has_power TINYINT DEFAULT 0 COMMENT '是否有电源',
  has_window TINYINT DEFAULT 0 COMMENT '是否靠窗',
  deleted TINYINT DEFAULT 0 COMMENT '删除标记',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_seat_code (seat_code),
  KEY idx_room_id (room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位表';

CREATE TABLE reservation (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  reservation_no VARCHAR(40) NOT NULL COMMENT '预约编号',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  seat_id BIGINT NOT NULL COMMENT '座位ID',
  room_id BIGINT NOT NULL COMMENT '自习室ID',
  start_time DATETIME NOT NULL COMMENT '开始时间',
  end_time DATETIME NOT NULL COMMENT '结束时间',
  status VARCHAR(20) DEFAULT 'pending' COMMENT '状态',
  audit_status VARCHAR(20) DEFAULT 'pending' COMMENT '审核状态',
  audit_reply VARCHAR(255) COMMENT '审核回复',
  auditor_id BIGINT COMMENT '审核人ID',
  audit_time DATETIME COMMENT '审核时间',
  cancel_reason VARCHAR(255) COMMENT '取消原因',
  deleted TINYINT DEFAULT 0 COMMENT '删除标记',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_reservation_no (reservation_no),
  KEY idx_user_id (user_id),
  KEY idx_seat_id (seat_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

CREATE TABLE check_in (
  id BIGINT NOT NULL AUTO_INCREMENT,
  reservation_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  check_in_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  check_in_method VARCHAR(20) DEFAULT 'manual',
  status TINYINT DEFAULT 1,
  deleted TINYINT DEFAULT 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到表';

CREATE TABLE check_out (
  id BIGINT NOT NULL AUTO_INCREMENT,
  reservation_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  check_out_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  status TINYINT DEFAULT 1,
  deleted TINYINT DEFAULT 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签退表';

CREATE TABLE evaluation (
  id BIGINT NOT NULL AUTO_INCREMENT,
  reservation_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  room_id BIGINT NOT NULL,
  score INT DEFAULT 5,
  content TEXT,
  deleted TINYINT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

CREATE TABLE announcement (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  content TEXT,
  author_id BIGINT NOT NULL,
  status TINYINT DEFAULT 1,
  top TINYINT DEFAULT 0,
  deleted TINYINT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

CREATE TABLE banner (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  image_url VARCHAR(255) NOT NULL,
  link_url VARCHAR(255),
  sort INT DEFAULT 0,
  status TINYINT DEFAULT 1,
  deleted TINYINT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

CREATE TABLE forum_post (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  content TEXT NOT NULL,
  user_id BIGINT NOT NULL,
  tag VARCHAR(50),
  like_count INT DEFAULT 0,
  view_count INT DEFAULT 0,
  collect_count INT DEFAULT 0,
  comment_count INT DEFAULT 0,
  status TINYINT DEFAULT 1,
  deleted TINYINT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛帖子表';

CREATE TABLE forum_comment (
  id BIGINT NOT NULL AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  parent_id BIGINT DEFAULT 0,
  deleted TINYINT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子评论表';

CREATE TABLE forum_collect (
  id BIGINT NOT NULL AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_post_user (post_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子收藏表';

CREATE TABLE message_board (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  content TEXT NOT NULL,
  user_id BIGINT NOT NULL,
  reply_content TEXT,
  reply_id BIGINT,
  status TINYINT DEFAULT 0,
  deleted TINYINT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言表';

CREATE TABLE violation_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  reservation_id BIGINT,
  type VARCHAR(50) NOT NULL,
  reason VARCHAR(255),
  deduct_score INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='违约记录表';

CREATE TABLE sensitive_word (
  id BIGINT NOT NULL AUTO_INCREMENT,
  word VARCHAR(50) NOT NULL,
  type VARCHAR(20) DEFAULT 'content',
  status TINYINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词表';

CREATE TABLE sys_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT,
  username VARCHAR(50),
  operation VARCHAR(100),
  method VARCHAR(200),
  params TEXT,
  ip VARCHAR(64),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

INSERT INTO sys_user (username, password, real_name, phone, email, role, credit_score, status) VALUES
('admin', '$2a$10$.ioZPJfmlMumh6wVPTvf8O/O4m/vBVRdAHCllor7cyNnhwRVHc34i', '系统管理员', '13900000000', 'admin@library.local', 'admin', 100, 1),
('test', '$2a$10$.emtjFFZP7BNtc2lVmgmIenruJe0zrKGm.YFidqeH3XBCWUndj3TG', '测试用户', '13800138000', 'test@library.local', 'user', 100, 1);

INSERT INTO study_room (room_code, room_name, location, floor, capacity, hour_price, open_time, image_url, description) VALUES
('ROOM001', '闻书自习室', '图书馆一层东区', '1层', 50, 1.00, '08:00-22:00', 'https://picsum.photos/seed/reading-room/800/420', '安静舒适，适合长时间阅读与复习'),
('ROOM002', '静心阁', '图书馆一层西区', '1层', 40, 0.80, '08:00-22:00', 'https://picsum.photos/seed/quiet-room/800/420', '采光柔和，适合专注学习'),
('ROOM003', '博学厅', '图书馆二层南区', '2层', 60, 1.20, '08:00-22:30', 'https://picsum.photos/seed/study-hall/800/420', '空间宽敞，座位带电源'),
('ROOM004', '明德堂', '图书馆二层北区', '2层', 45, 1.00, '09:00-21:30', 'https://picsum.photos/seed/library-seat/800/420', '环境整洁，适合小组备考'),
('ROOM005', '致远楼VIP自习室', '图书馆三层', '3层', 80, 1.50, '08:00-23:00', 'https://picsum.photos/seed/vip-study/800/420', '开放时间长，适合考研自习');

INSERT INTO seat (seat_code, room_id, status, has_power, has_window) VALUES
('ROOM001-01', 1, 1, 1, 1), ('ROOM001-02', 1, 1, 1, 0), ('ROOM001-03', 1, 1, 1, 1), ('ROOM001-04', 1, 1, 0, 0), ('ROOM001-05', 1, 1, 1, 1),
('ROOM001-06', 1, 1, 1, 0), ('ROOM001-07', 1, 1, 0, 1), ('ROOM001-08', 1, 1, 1, 0), ('ROOM001-09', 1, 1, 1, 1), ('ROOM001-10', 1, 4, 0, 0),
('ROOM002-01', 2, 1, 1, 1), ('ROOM002-02', 2, 1, 1, 0), ('ROOM002-03', 2, 1, 0, 1), ('ROOM002-04', 2, 1, 1, 1), ('ROOM002-05', 2, 1, 1, 0),
('ROOM003-01', 3, 1, 1, 1), ('ROOM003-02', 3, 1, 1, 0), ('ROOM003-03', 3, 1, 0, 1), ('ROOM003-04', 3, 1, 1, 1), ('ROOM003-05', 3, 1, 1, 0),
('ROOM004-01', 4, 1, 1, 1), ('ROOM004-02', 4, 1, 0, 0), ('ROOM004-03', 4, 1, 1, 1), ('ROOM005-01', 5, 1, 1, 1), ('ROOM005-02', 5, 1, 1, 0);

INSERT INTO announcement (title, content, author_id, status, top) VALUES
('图书馆座位预约系统正式上线', '欢迎使用座位预约系统，请合理安排学习时间，按时签到签退。', 1, 1, 1),
('自习室开放时间调整通知', '即日起自习室开放时间调整为 08:00-22:00，节假日另行通知。', 1, 1, 0);

INSERT INTO banner (title, image_url, link_url, sort, status) VALUES
('安静学习空间', 'https://picsum.photos/seed/library-banner-1/1200/320', '/studyrooms', 1, 1),
('线上预约座位', 'https://picsum.photos/seed/library-banner-2/1200/320', '/studyrooms', 2, 1);

INSERT INTO forum_post (title, content, user_id, tag) VALUES
('欢迎来到学习交流区', '这里可以分享学习计划、备考经验和自习室使用感受。', 2, '公告'),
('推荐一个高效学习方法', '番茄工作法对保持专注很有帮助，建议大家尝试。', 2, '学习');

INSERT INTO message_board (title, content, user_id, reply_content, reply_id, status) VALUES
('希望增加靠窗座位筛选', '预约时如果能筛选靠窗和电源会更方便。', 2, '建议已收到，后续版本会优化筛选能力。', 1, 1);

INSERT INTO sensitive_word (word, type, status) VALUES
('广告', 'content', 1),
('辱骂', 'content', 1);
