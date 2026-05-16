-- tdep-case-service 完整案件管理建库脚本，可直接在 MySQL 中执行。
CREATE DATABASE IF NOT EXISTS tdep_case
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE tdep_case;

SOURCE schema.sql;
