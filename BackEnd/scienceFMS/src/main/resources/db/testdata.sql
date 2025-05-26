-- 用户数据
INSERT INTO t_user (username, password, role, real_name, email, phone, status, login_fail_count, create_time, update_time) VALUES
('admin1', '123456', 'admin', '管理员', 'admin@example.com', '13900000000', 0, 0, NOW(), NOW()),
('teacher1', '123456', 'teacher', '张教授', 'zhang@example.com', '13911111111', 0, 0, NOW(), NOW()),
('teacher2', '123456', 'teacher', '李教授', 'li@example.com', '13922222222', 0, 0, NOW(), NOW()),
('teacher3', '123456', 'teacher', '王教授', 'wang@example.com', '13933333333', 0, 0, NOW(), NOW());

-- 教师数据（更新后包含更多字段）
INSERT INTO t_teacher (username, real_name, gender, birth_date, title, department, position, research_area, email, phone, office_location, avatar_url, create_time, update_time, is_deleted) VALUES
('teacher1', '张教授', '男', '1975-01-15', '教授', '计算机科学与技术学院', '教授', '人工智能、大数据分析', 'zhang@example.com', '13911111111', '科研楼A座301室', 'http://example.com/avatars/teacher1.jpg', NOW(), NOW(), 0),
('teacher2', '李教授', '女', '1980-05-22', '副教授', '电子信息工程学院', '副教授', '嵌入式系统、物联网技术', 'li@example.com', '13922222222', '电子楼B座205室', 'http://example.com/avatars/teacher2.jpg', NOW(), NOW(), 0),
('teacher3', '王教授', '男', '1978-11-30', '讲师', '软件工程学院', '讲师', '软件工程、系统架构设计', 'wang@example.com', '13933333333', '软件楼C座102室', 'http://example.com/avatars/teacher3.jpg', NOW(), NOW(), 0);

-- 教师教育背景数据（新增表）
INSERT INTO t_teacher_education (teacher_id, degree, institution, major, start_year, end_year, create_time, update_time, is_deleted) VALUES
-- 张教授的教育背景
(1, '博士', '北京大学', '计算机科学', 2000, 2005, NOW(), NOW(), 0),
(1, '硕士', '清华大学', '软件工程', 1997, 2000, NOW(), NOW(), 0),
(1, '学士', '浙江大学', '计算机科学与技术', 1993, 1997, NOW(), NOW(), 0),
-- 李教授的教育背景
(2, '博士', '复旦大学', '电子信息工程', 2003, 2008, NOW(), NOW(), 0),
(2, '硕士', '上海交通大学', '通信工程', 2000, 2003, NOW(), NOW(), 0),
(2, '学士', '哈尔滨工业大学', '电子信息科学与技术', 1996, 2000, NOW(), NOW(), 0),
-- 王教授的教育背景
(3, '博士', '南京大学', '软件工程', 2005, 2010, NOW(), NOW(), 0),
(3, '硕士', '武汉大学', '计算机应用技术', 2002, 2005, NOW(), NOW(), 0),
(3, '学士', '华中科技大学', '软件工程', 1998, 2002, NOW(), NOW(), 0);

-- 科研项目数据
INSERT INTO t_research_project (project_name, project_code, source, funding, start_date, end_date, attachments, teacher_id, role, create_time, update_time, is_deleted) VALUES
('基于深度学习的智能图像识别技术研究', 'NSFC-2022A01', '国家自然科学基金', 800000.00, '2022-01-01', '2024-12-31', 'http://example.com/files/project1.pdf', 1, '负责人', NOW(), NOW(), 0),
('区块链在供应链金融中的应用研究', 'PNSFC-2021B03', '省自然科学基金', 300000.00, '2021-07-01', '2023-06-30', 'http://example.com/files/project2.pdf', 1, '负责人', NOW(), NOW(), 0),
('智慧校园建设关键技术研究', 'SCHOOL-2022C05', '校级项目', 50000.00, '2022-03-01', '2023-02-28', 'http://example.com/files/project3.pdf', 2, '负责人', NOW(), NOW(), 0),
('企业智能决策支持系统开发', 'HZ-2022D01', '横向项目', 450000.00, '2022-05-15', '2023-05-14', 'http://example.com/files/project4.pdf', 3, '负责人', NOW(), NOW(), 0),
('5G网络安全防护技术研究', 'NSFC-2020A23', '国家自然科学基金', 900000.00, '2020-01-01', '2022-12-31', 'http://example.com/files/project5.pdf', 2, '参与者', NOW(), NOW(), 0),
('新型传感器关键技术研究', 'NSFC-2023B42', '国家自然科学基金', 1200000.00, '2023-01-01', '2025-12-31', 'http://example.com/files/project6.pdf', 3, '负责人', NOW(), NOW(), 0),
('人工智能辅助教学系统研发', 'HZ-2023E08', '横向项目', 350000.00, '2023-03-10', '2024-03-09', 'http://example.com/files/project7.pdf', 1, '参与者', NOW(), NOW(), 0);

-- 获奖信息数据
INSERT INTO t_award (award_name, award_level, issuing_unit, award_date, certificate_image, related_project_id, teacher_id, create_time, update_time, is_deleted) VALUES
('全国科技进步奖', '二等奖', '国家科技部', '2022-12-10', 'http://example.com/images/award1.jpg', 1, 1, NOW(), NOW(), 0),
('省科技创新奖', '一等奖', '省科技厅', '2021-09-15', 'http://example.com/images/award2.jpg', 2, 1, NOW(), NOW(), 0),
('教育部高等学校科学研究优秀成果奖', '科技进步奖', '教育部', '2022-06-20', 'http://example.com/images/award3.jpg', 3, 2, NOW(), NOW(), 0),
('计算机应用技术优秀论文奖', '优秀奖', '中国计算机学会', '2021-11-05', 'http://example.com/images/award4.jpg', 5, 2, NOW(), NOW(), 0),
('产学研合作创新奖', '特等奖', '工业和信息化部', '2022-08-18', 'http://example.com/images/award5.jpg', 4, 3, NOW(), NOW(), 0),
('科学技术发明奖', '三等奖', '中国科学院', '2023-02-15', 'http://example.com/images/award6.jpg', 6, 3, NOW(), NOW(), 0),
('互联网创新应用奖', '金奖', '中国互联网协会', '2022-11-20', 'http://example.com/images/award7.jpg', 7, 1, NOW(), NOW(), 0);

-- 知识产权数据
INSERT INTO t_intellectual_property (name, type, subtype, auth_number, apply_date, auth_date, inventor_rank, teacher_id, create_time, update_time, is_deleted) VALUES
('一种基于深度学习的图像识别方法', '专利', '发明专利', 'CN123456789A', '2021-03-15', '2022-05-20', 1, 1, NOW(), NOW(), 0),
('区块链数据安全传输系统', '专利', '实用新型专利', 'CN987654321B', '2020-08-10', '2021-02-25', 2, 1, NOW(), NOW(), 0),
('智能校园管理软件V1.0', '著作权', '软件著作权', 'R-2022-L-123456', '2022-01-20', '2022-03-10', 1, 2, NOW(), NOW(), 0),
('人工智能算法设计与实现', '著作权', '图书著作权', 'I-2021-A-654321', '2020-11-05', '2021-06-15', 1, 2, NOW(), NOW(), 0),
('物联网设备安全认证方法', '专利', '发明专利', 'CN456789123C', '2021-07-22', '2023-01-05', 1, 3, NOW(), NOW(), 0),
('云计算资源调度优化方法', '专利', '发明专利', 'CN202389475D', '2022-05-10', '2023-06-25', 1, 1, NOW(), NOW(), 0),
('计算机网络实验教程', '著作权', '图书著作权', 'I-2022-B-789012', '2022-08-15', '2022-10-20', 1, 3, NOW(), NOW(), 0),
('数据分析与可视化工具软件V2.0', '著作权', '软件著作权', 'R-2022-L-654789', '2022-06-12', '2022-09-05', 2, 2, NOW(), NOW(), 0);

-- 出访记录数据
INSERT INTO t_visit_record (purpose, location, start_date, end_date, result, itinerary_file, report_file, reimbursement_status, reimbursement_amount, teacher_id, create_time, update_time, is_deleted) VALUES
('参加IEEE国际会议', '美国·旧金山', '2022-10-15', '2022-10-22', '发表论文并建立合作关系', 'http://example.com/files/itinerary1.pdf', 'http://example.com/files/report1.pdf', '已报销', 15000.00, 1, NOW(), NOW(), 0),
('访问哈佛大学交流', '美国·波士顿', '2023-03-05', '2023-03-20', '签署合作协议', 'http://example.com/files/itinerary2.pdf', 'http://example.com/files/report2.pdf', '报销中', 25000.00, 1, NOW(), NOW(), 0),
('参加人工智能峰会', '英国·伦敦', '2022-06-10', '2022-06-18', '做主题演讲', 'http://example.com/files/itinerary3.pdf', 'http://example.com/files/report3.pdf', '已报销', 18000.00, 2, NOW(), NOW(), 0),
('访问东京大学', '日本·东京', '2022-08-20', '2022-09-05', '开展联合研究项目', 'http://example.com/files/itinerary4.pdf', 'http://example.com/files/report4.pdf', '已报销', 12000.00, 2, NOW(), NOW(), 0),
('参加技术研讨会', '德国·柏林', '2023-01-10', '2023-01-18', '建立国际合作关系', 'http://example.com/files/itinerary5.pdf', 'http://example.com/files/report5.pdf', '未提交', 0.00, 3, NOW(), NOW(), 0),
('出席亚太科技论坛', '新加坡', '2023-04-12', '2023-04-18', '宣讲最新研究成果', 'http://example.com/files/itinerary6.pdf', 'http://example.com/files/report6.pdf', '已报销', 10000.00, 3, NOW(), NOW(), 0),
('参加SIGMOD数据库会议', '加拿大·多伦多', '2023-07-01', '2023-07-10', '发表高水平论文', 'http://example.com/files/itinerary7.pdf', 'http://example.com/files/report7.pdf', '报销中', 20000.00, 1, NOW(), NOW(), 0),
('赴清华大学学术交流', '中国·北京', '2023-05-05', '2023-05-08', '商讨合作研究项目', 'http://example.com/files/itinerary8.pdf', 'http://example.com/files/report8.pdf', '已报销', 3000.00, 2, NOW(), NOW(), 0);