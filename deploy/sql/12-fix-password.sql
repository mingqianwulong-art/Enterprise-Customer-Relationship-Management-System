USE crm_db;
UPDATE sys_user SET password = '$2a$10$OeBvU4BldpgRMIAts6WdlOb.g1PRrlVavP55QCX1rmGNaHvtnLTdC' WHERE username IN ('admin', 'sale01', 'market01', 'service01');
SELECT username, password FROM sys_user;
