CREATE DATABASE asm2_quanglmFX22872;

USE asm2_quanglmFX22872;

CREATE TABLE category (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) DEFAULT NULL,
    number_choose INT DEFAULT NULL
);

INSERT INTO category (name, number_choose) 
VALUES ('DEVELOPER', 4), ('SYSTEM ENGINEER', 2), ('DevOps',2), ('Tester', 1), ('BrSE', 3), ('PM', 1);

CREATE TABLE job_type (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) DEFAULT NULL
);

INSERT INTO job_type (name)
VALUES ('FULL TIME'), ('PART TIME'), ('FREELANCER'), ('INTERNSHIP'), ('TEMPORARY');

CREATE TABLE role (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(255) DEFAULT NULL
);

INSERT INTO role (role_name) 
VALUES ('Ứng viên'), ('Nhà tuyển dụng');

CREATE TABLE cv (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255),
    user_id INT
);

INSERT INTO cv (file_name)
VALUES ('cv.pdf');

CREATE TABLE user (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    address VARCHAR(255) DEFAULT NULL,
    description VARCHAR(255) DEFAULT NULL,
    email VARCHAR(255) DEFAULT NULL,
    full_name VARCHAR(255) DEFAULT NULL,
    image VARCHAR(255) DEFAULT NULL,
    password VARCHAR(255) DEFAULT NULL,
    phone_number VARCHAR(255) DEFAULT NULL,
    status INT DEFAULT NULL,
    role_id INT,
    cv_id INT
);

INSERT INTO user (address, description, email, full_name, image, password, phone_number, status, role_id, cv_id)
VALUES ('Tiền Giang', 'Học viên Funix', 'quang@luv2code.com', 'Lê Minh Quang', 'resources/assets/images/image_1.jpg', 'abc', '080123456', 1, 1, 1),
('Sài Gòn', 'Học Funix', 'minh@luv2code.com', 'Lê Minh', 'resources/assets/images/image_2.jpg', 'abc', '080123457', 1, 1, 1),
('Quảng Nam', 'FPT HR', 'fpt@gmail.com', 'FPT Software', 'resources/assets/images/logo-fpt.jpg', 'abc', '080987654', 1, 2, NULL),
('Hà Nội', 'Funix HR', 'unix@gmail.com', 'unix', 'resources/assets/images/logo-funix.jpg', 'abc', '050345678', 1, 2, NULL),
('Mỹ Tho', 'Cựu học viên Funix', 'quang1@luv2code.com', 'Lê Quang 1', 'resources/assets/images/image_3.jpg', 'abc', '080123456', 1, 1, 1),
('Châu Thành', 'SPKT', 'spkt@luv2code.com', 'Lê Minh 1', 'resources/assets/images/image_4.jpg', 'abc', '080123457', 1, 1, 1);

CREATE TABLE applypost(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at VARCHAR(255) DEFAULT NULL,
    name_cv VARCHAR(255),
    status INT DEFAULT NULL,
    text VARCHAR(255) DEFAULT NULL,
    recruiment_id INT,
    user_id INT,
    CONSTRAINT FK_USER_ROLE FOREIGN KEY (role_id) REFERENCES role (id)
);

INSERT INTO apply_post (created_at, name_cv, status, text, recruitment_id, user_id)
VALUES('2023-11-23', 'CV.pdf', 1, 'Hello', 1, 1),
('2023-11-23', 'cv.pdf', 1, 'Hello', 1, 2),
('2023-11-23', 'cv.pdf', 1, 'Hello', 2, 1),
('2023-11-23', 'cv.pdf', 1, 'Hello', 3, 1),
('2023-11-23', 'cv.pdf', 1, 'Hello', 4, 1),
('2023-11-23', 'cv.pdf', 1, 'Hello', 1, 5),
('2023-11-23', 'cv.pdf', 1, 'Hello', 4, 6);

CREATE TABLE follow_company (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    company_id INT,
    user_id INT
);

CREATE TABLE recruitment (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	address VARCHAR(255) DEFAULT NULL,
	created_at VARCHAR(255) DEFAULT NULL,
	description VARCHAR(255) DEFAULT NULL,
	experience VARCHAR(255) DEFAULT NULL,
	quantity INT DEFAULT NULL,
	salary VARCHAR(255) DEFAULT NULL,
	status INT DEFAULT NULL,
	title VARCHAR(255) DEFAULT NULL,
	type VARCHAR(255) DEFAULT NULL,
	view INT DEFAULT NULL,
	deadline VARCHAR(255) DEFAULT NULL,
	category_id INT,
	company_id INT
);

INSERT INTO recruitment (address, created_at, description, experience, quantity, salary, 
    status, title, view, deadline, type_id, category_id, company_id)
VALUES ('Đà Nẵng', '2023-10-23', '.NET > 2 năm kinh nghiệm', '2 năm', 12, '12 triệu', 
		1, 'Tuyển lập trình viên .NET', 0, '2023-11-23', 1, 1, 1),
        ('Hà Nội', '2023-10-23', 'JAVA > 1 năm kinh nghiệm', '1 năm', 13, '13 triệu', 
		1, 'Tuyển lập trình viên JAVA', 0, '2023-11-23', 1, 1, 1),
        ('Hồ Chí Minh', '2023-10-23', 'PHP > 2 năm kinh nghiệm', '2.5 năm', 11, '11 triệu', 
		1, 'Tuyển lập trình viên PHP', 0, '2023-11-23', 1, 1, 1),
        ('Đà Nẵng', '2023-10-23', 'NODEJS > 2 năm kinh nghiệm', '2 năm', 15, '15 triệu', 
		1, 'Tuyển lập trình viên NODEJS', 0, '2023-11-23', 1, 1, 2);

CREATE TABLE save_job (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    recruitment_id INT,
    user_id INT
);


CREATE TABLE company (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    address VARCHAR(255) DEFAULT NULL,
    description VARCHAR(255) DEFAULT NULL,
    email VARCHAR(255) DEFAULT NULL,
    logo VARCHAR(255) DEFAULT NULL,
    company_name VARCHAR(255) DEFAULT NULL,
    phone_number VARCHAR(255) DEFAULT NULL,
    status INT DEFAULT NULL,
    user_id INT
);

INSERT INTO company (address, description, email, logo, company_name, phone_number, status, user_id)
VALUES ('Đà Nẵng', 'FPT Software Đà Nẵng', 'tuyendungnhansu@gmail.com','resources/assets/images/logo-fpt.jpg', 'FPT Software', '0394073712', 1, 3),
('Hà Nội', 'Funix', 'tuyendungFunix@gmail.com','resources/assets/images/logo-funix.jpg', 'Funix', '050123789', 1, 4);
