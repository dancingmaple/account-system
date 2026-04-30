-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    gender INTEGER DEFAULT 0,
    org_id INTEGER,
    dept_id INTEGER,
    post_ids VARCHAR(255),
    status INTEGER DEFAULT 1,
    is_admin INTEGER DEFAULT 0,
    login_ip VARCHAR(50),
    login_date DATETIME,
    pwd_update_time DATETIME,
    deleted INTEGER DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 部门表
CREATE TABLE IF NOT EXISTS sys_dept (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    dept_name VARCHAR(50) NOT NULL,
    parent_id INTEGER DEFAULT 0,
    ancestors VARCHAR(500),
    sort INTEGER DEFAULT 0,
    leader VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    status INTEGER DEFAULT 1,
    deleted INTEGER DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    role_code VARCHAR(50) UNIQUE NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    data_scope INTEGER DEFAULT 1,
    custom_dept_ids VARCHAR(500),
    sort INTEGER DEFAULT 0,
    status INTEGER DEFAULT 1,
    deleted INTEGER DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 菜单表
CREATE TABLE IF NOT EXISTS sys_menu (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    menu_code VARCHAR(50) UNIQUE,
    menu_name VARCHAR(50) NOT NULL,
    parent_id INTEGER DEFAULT 0,
    path VARCHAR(255),
    component VARCHAR(255),
    type INTEGER DEFAULT 1,
    permission VARCHAR(100),
    icon VARCHAR(50),
    sort INTEGER DEFAULT 0,
    visible INTEGER DEFAULT 1,
    status INTEGER DEFAULT 1,
    deleted INTEGER DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    role_id INTEGER NOT NULL,
    menu_id INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 初始化管理员用户 (密码: 123456 的 BCrypt 哈希)
INSERT OR IGNORE INTO sys_user (id, username, password, nickname, email, status, is_admin, created_at)
VALUES (1, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '管理员', 'admin@example.com', 1, 1, datetime('now'));

-- 初始化角色
INSERT OR IGNORE INTO sys_role (id, role_code, role_name, data_scope, sort, status, created_at)
VALUES (1, 'admin', '超级管理员', 1, 1, 1, datetime('now')),
       (2, 'user', '普通用户', 3, 2, 1, datetime('now'));

-- 初始化用户角色关联
INSERT OR IGNORE INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 初始化菜单
INSERT OR IGNORE INTO sys_menu (id, menu_code, menu_name, parent_id, path, component, type, permission, icon, sort, status, created_at) VALUES
(1, 'system', '系统管理', 0, '/system', NULL, 0, NULL, 'Setting', 1, 1, datetime('now')),
(2, 'system:user', '用户管理', 1, 'user', 'system/user/index', 1, 'user:list', 'User', 1, 1, datetime('now')),
(3, 'system:role', '角色管理', 1, 'role', 'system/role/index', 1, 'role:list', 'UserFilled', 2, 1, datetime('now')),
(4, 'system:menu', '菜单管理', 1, 'menu', 'system/menu/index', 1, 'menu:list', 'Menu', 3, 1, datetime('now')),
(5, 'system:dept', '部门管理', 1, 'dept', 'system/dept/index', 1, 'dept:list', 'OfficeBuilding', 4, 1, datetime('now'));

-- 初始化角色菜单关联 (给admin角色所有权限)
INSERT OR IGNORE INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5);