----------------------------------
-- SELECT TEST TABLES
----------------------------------
DROP TABLE IF EXISTS SELECT_TABLE;
CREATE TABLE SELECT_TABLE(ID INT PRIMARY KEY, NAME VARCHAR(255) DEFAULT '');

-- SELECT_TABLE
INSERT INTO SELECT_TABLE VALUES(1, 'ABC');
INSERT INTO SELECT_TABLE VALUES(2, 'abc');
INSERT INTO SELECT_TABLE VALUES(3, 'cde');


----------------------------------
-- INSERT TEST TABLES
----------------------------------
DROP TABLE IF EXISTS INSERT_TABLE;
CREATE TABLE INSERT_TABLE(ID INT AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR(255) DEFAULT '');


----------------------------------
-- UPDATE TEST TABLES
----------------------------------
DROP TABLE IF EXISTS UPDATE_TABLE;
CREATE TABLE UPDATE_TABLE(ID INT PRIMARY KEY, NAME VARCHAR(255) DEFAULT '');

-- SELECT_TABLE
INSERT INTO UPDATE_TABLE VALUES(1, 'ABC');


----------------------------------
-- DELETE TEST TABLES
----------------------------------
DROP TABLE IF EXISTS DELETE_TABLE;
CREATE TABLE DELETE_TABLE(ID INT PRIMARY KEY, NAME VARCHAR(255) DEFAULT '');

-- SELECT_TABLE
INSERT INTO DELETE_TABLE VALUES(1, 'ABC');


----------------------------------
-- JOIN TEST TABLES
----------------------------------
DROP TABLE IF EXISTS JOIN_TABLE_1;
CREATE TABLE JOIN_TABLE_1(ID INT PRIMARY KEY, NAME VARCHAR(255) DEFAULT '', TABLE_2_ID INT);

DROP TABLE IF EXISTS JOIN_TABLE_2;
CREATE TABLE JOIN_TABLE_2(ID INT PRIMARY KEY, NAME VARCHAR(255) DEFAULT '');

DROP TABLE IF EXISTS JOIN_TABLE_3;
CREATE TABLE JOIN_TABLE_3(ID INT PRIMARY KEY, NAME VARCHAR(255) DEFAULT '', TABLE_2_ID INT);

-- JOIN_USER
INSERT INTO JOIN_TABLE_1 VALUES(1, 'table1_name1', 1);
INSERT INTO JOIN_TABLE_1 VALUES(2, 'table1_name2', 2);
INSERT INTO JOIN_TABLE_1 VALUES(3, 'table1_name3', 3);
-- JOIN_ROLE
INSERT INTO JOIN_TABLE_2 VALUES(1, 'table2_name1');
INSERT INTO JOIN_TABLE_2 VALUES(2, 'table2_name2');
INSERT INTO JOIN_TABLE_2 VALUES(3, 'table2_name3');
-- JOIN_USER_ROLE
INSERT INTO JOIN_TABLE_3 VALUES(1, 'table3_name1', 1);
INSERT INTO JOIN_TABLE_3 VALUES(2, 'table3_name2', 1);
INSERT INTO JOIN_TABLE_3 VALUES(3, 'table3_name3', 2);
INSERT INTO JOIN_TABLE_3 VALUES(4, 'table3_name4', 3);
