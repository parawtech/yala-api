CREATE USER rkettest WITH PASSWORD 'rkettest';
CREATE DATABASE rkettest OWNER rkettest;
\c rkettest
CREATE SCHEMA rkettest_schema AUTHORIZATION rkettest;
ALTER USER rkettest SET search_path TO rkettest_schema;
GRANT ALL PRIVILEGES ON SCHEMA rkettest_schema TO rkettest;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA rkettest_schema TO rkettest;