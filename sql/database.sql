-- 通用账户系统 - SQLite 数据库初始化脚本

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    gender TINYINT DEFAULT 0,
    org_id BIGINT,
    dept_id BIGINT,
    post_ids VARCHAR(500),
    status TINYINT DEFAULT 1,
    is_admin TINYINT DEFAULT 0,
    login_ip VARCHAR(50),
    login_date DATETIME,
    pwd_update_time DATETIME,
    deleted TINYINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(100) NOT NULL,
    data_scope TINYINT DEFAULT 4,
    custom_dept_ids TEXT,
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    deleted TINYINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 菜单表
CREATE TABLE IF NOT EXISTS sys_menu (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    menu_code VARCHAR(50) NOT NULL UNIQUE,
    menu_name VARCHAR(100) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    path VARCHAR(200),
    component VARCHAR(200),
    type TINYINT NOT NULL,
    permission VARCHAR(100),
    icon VARCHAR(100),
    sort INT DEFAULT 0,
    visible TINYINT DEFAULT 1,
    status TINYINT DEFAULT 1,
    deleted TINYINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 部门表
CREATE TABLE IF NOT EXISTS sys_dept (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    dept_name VARCHAR(100) NOT NULL,
    parent_id BIGINT,
    ancestors TEXT,
    sort INT DEFAULT 0,
    leader VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    status TINYINT DEFAULT 1,
    deleted TINYINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 开放平台应用表
CREATE TABLE IF NOT EXISTS sys_app (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    app_key VARCHAR(50) NOT NULL UNIQUE,
    app_secret VARCHAR(255) NOT NULL,
    app_name VARCHAR(100) NOT NULL,
    owner_id BIGINT,
    callback_url VARCHAR(255),
    allowed_ips TEXT,
    rate_limit INT DEFAULT 1000,
    status TINYINT DEFAULT 1,
    expires_at DATETIME,
    last_used_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 用户-角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

-- 角色-菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id)
);

-- 数据权限规则表
CREATE TABLE IF NOT EXISTS sys_data_scope_rule (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    role_id BIGINT NOT NULL,
    table_name VARCHAR(100) NOT NULL,
    column_name VARCHAR(100) NOT NULL,
    scope_type TINYINT NOT NULL,
    priority INT DEFAULT 0
);

-- 登录日志表
CREATE TABLE IF NOT EXISTS sys_login_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id BIGINT,
    username VARCHAR(50),
    ip VARCHAR(50),
    user_agent VARCHAR(500),
    location VARCHAR(100),
    result TINYINT DEFAULT 0,
    msg TEXT,
    login_date DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 操作日志表
CREATE TABLE IF NOT EXISTS sys_op_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id BIGINT,
    username VARCHAR(50),
    op_name VARCHAR(100),
    op_type VARCHAR(20),
    op_module VARCHAR(50),
    op_method VARCHAR(200),
    op_params TEXT,
    op_result TINYINT DEFAULT 0,
    cost_time INT,
    ip VARCHAR(50),
    user_agent VARCHAR(500),
    location VARCHAR(100),
    op_date DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ========== 索引创建 ==========

-- 用户表索引
CREATE INDEX IF NOT EXISTS idx_sys_user_username ON sys_user(username);
CREATE INDEX IF NOT EXISTS idx_sys_user_org_dept ON sys_user(org_id, dept_id);
CREATE INDEX IF NOT EXISTS idx_sys_user_status ON sys_user(status);

-- 角色表索引
CREATE INDEX IF NOT EXISTS idx_sys_role_code ON sys_role(role_code);

-- 菜单表索引
CREATE INDEX IF NOT EXISTS idx_sys_menu_parent ON sys_menu(parent_id);
CREATE INDEX IF NOT EXISTS idx_sys_menu_type ON sys_menu(type);

-- 部门表索引
CREATE INDEX IF NOT EXISTS idx_sys_dept_parent ON sys_dept(parent_id);

-- 应用表索引
CREATE INDEX IF NOT EXISTS idx_sys_app_key ON sys_app(app_key);

-- ========== 初始数据插入 ==========

-- 超级管理员角色
INSERT OR IGNORE INTO sys_role (id, role_code, role_name, data_scope, sort, status) VALUES
(1, 'super_admin', '超级管理员', 1, 0, 1);

-- 默认部门
INSERT OR IGNORE INTO sys_dept (id, dept_name, parent_id, ancestors, sort, status) VALUES
(1, '技术部', 0, '0', 1, 1),
(2, '产品部', 0, '0', 2, 1),
(3, '运营部', 0, '0', 3, 1);

-- 默认菜单
INSERT OR IGNORE INTO sys_menu (id, menu_code, menu_name, parent_id, path, component, type, permission, icon, sort, status) VALUES
(1, 'system', '系统管理', 0, '/system', null, 1, '', 'SettingOutlined', 0, 1),
(2, 'system:user', '用户管理', 1, '/system/user', 'system/User', 2, 'user:*:*', 'UserOutlined', 1, 1),
(3, 'system:dept', '部门管理', 1, '/system/dept', 'system/Dept', 2, 'dept:*:*', 'TeamOutlined', 2, 1),
(4, 'system:role', '角色管理', 1, '/system/role', 'system/Role', 2, 'role:*:*', 'SafetyCertificateOutlined', 3, 1),
(5, 'system:menu', '菜单管理', 1, '/system/menu', 'system/Menu', 2, 'menu:*:*', 'AppstoreOutlined', 4, 1),
(6, 'dashboard', '工作台', 0, '/dashboard', 'Dashboard', 2, 'dashboard:*:*', 'DashboardOutlined', 999, 1);

-- 默认用户（密码为：123456）
INSERT OR IGNORE INTO sys_user (id, username, password, email, phone, nickname, dept_id, status, is_admin, created_at) VALUES
(1, 'admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5lWkJ8RfL.7F2', 'admin@example.com', '13800138000', '系统管理员', 1, 1, 1, datetime('now'));

-- 分配超级管理员角色
INSERT OR IGNORE INTO sys_user_role (user_id, role_id) VALUES
(1, 1);

-- ========== 视图创建 ==========

-- 获取用户角色视图
CREATE VIEW IF NOT EXISTS v_user_roles AS
SELECT u.id as user_id, u.username, r.role_code, r.role_name
FROM sys_user u
LEFT JOIN sys_user_role ur ON u.id = ur.user_id
LEFT JOIN sys_role r ON ur.role_id = r.id
WHERE u.deleted = 0 AND r.deleted = 0;

-- 获取用户权限视图
CREATE VIEW IF NOT EXISTS v_user_permissions AS
SELECT DISTINCT u.id as user_id, u.username, m.permission
FROM sys_user u
LEFT JOIN sys_user_role ur ON u.id = ur.user_id
LEFT JOIN sys_role r ON ur.role_id = r.id
LEFT JOIN sys_role_menu rm ON r.id = rm.role_id
LEFT JOIN sys_menu m ON rm.menu_id = m.id
WHERE u.deleted = 0 AND m.deleted = 0 AND m.permission IS NOT NULL;