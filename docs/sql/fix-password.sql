USE crm_db;
-- 重置所有用户密码为 @Abc1234
-- BCrypt 加密串："@Abc1234" → $2a$10$QjmwfUPHT/x.4oQcMdgwXOxzxhZJASsYtjEOivqb95ZIPYlPL4ifi
UPDATE sys_user SET password = '$2a$10$QjmwfUPHT/x.4oQcMdgwXOxzxhZJASsYtjEOivqb95ZIPYlPL4ifi' WHERE username IN ('admin', 'sale01', 'market01', 'service01');
SELECT username, password FROM sys_user;
