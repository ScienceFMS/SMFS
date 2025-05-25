-- 教师表
CREATE TABLE t_teacher (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    real_name VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    position VARCHAR(50),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INTEGER DEFAULT 0
);

-- 科研项目表
CREATE TABLE t_research_project (
    id SERIAL PRIMARY KEY,
    project_name VARCHAR(255) NOT NULL,
    project_code VARCHAR(100), -- 项目编号
    source VARCHAR(200) NOT NULL, -- 来源
    funding DECIMAL(15,2) NOT NULL, -- 经费
    start_date DATE NOT NULL, -- 起始时间
    end_date DATE NOT NULL, -- 终止时间
    attachments VARCHAR(500), -- 附件URLs(逗号分隔)
    teacher_id INTEGER NOT NULL REFERENCES t_teacher(id),
    role VARCHAR(20) DEFAULT '负责人', -- 负责人/参与者
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INTEGER DEFAULT 0,
    UNIQUE(project_code)
);

-- 获奖信息表
CREATE TABLE t_award (
    id SERIAL PRIMARY KEY,
    award_name VARCHAR(255) NOT NULL,
    award_level VARCHAR(50) NOT NULL, -- 等级
    issuing_unit VARCHAR(200) NOT NULL, -- 颁发单位
    award_date DATE NOT NULL, -- 获奖时间
    certificate_image VARCHAR(255), -- 证书图片URL
    related_project_id INTEGER REFERENCES t_research_project(id), -- 关联项目ID
    teacher_id INTEGER NOT NULL REFERENCES t_teacher(id),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INTEGER DEFAULT 0
);

-- 专利/著作权表
CREATE TABLE t_intellectual_property (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL, -- 专利/著作权
    subtype VARCHAR(50), -- 专利类型(发明/实用新型等)
    auth_number VARCHAR(100) NOT NULL, -- 授权号
    apply_date DATE NOT NULL, -- 申请时间
    auth_date DATE, -- 授权时间
    inventor_rank INTEGER DEFAULT 1, -- 发明人排名
    teacher_id INTEGER NOT NULL REFERENCES t_teacher(id),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INTEGER DEFAULT 0,
    UNIQUE(auth_number)
);

-- 出访记录表
CREATE TABLE t_visit_record (
    id SERIAL PRIMARY KEY,
    purpose VARCHAR(255) NOT NULL, -- 目的
    location VARCHAR(200) NOT NULL, -- 地点
    start_date DATE NOT NULL, -- 开始时间
    end_date DATE NOT NULL, -- 结束时间
    result TEXT, -- 成果
    itinerary_file VARCHAR(255), -- 行程单文件URL
    report_file VARCHAR(255), -- 成果报告URL
    reimbursement_status VARCHAR(20) DEFAULT '未报销', -- 报销状态
    reimbursement_amount DECIMAL(12,2), -- 报销金额
    teacher_id INTEGER NOT NULL REFERENCES t_teacher(id),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INTEGER DEFAULT 0
);