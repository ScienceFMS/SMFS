-- 创建用户表
CREATE TABLE IF NOT EXISTS t_user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    real_name VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    status INTEGER DEFAULT 0,
    login_fail_count INTEGER DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建初始管理员和教师账号
--INSERT INTO t_user (username, password, role, real_name, status) 
--VALUES ('admin1', '123456', 'admin', '管理员', 0)
--ON CONFLICT (username) DO NOTHING;

--INSERT INTO t_user (username, password, role, real_name, status) 
--VALUES ('teacher1', '123456', 'teacher', '张教师', 0)
--ON CONFLICT (username) DO NOTHING; 