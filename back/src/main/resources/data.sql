DROP DATABASE IF EXISTS quest_web;
CREATE DATABASE quest_web;
USE quest_web;
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS address;
CREATE TABLE user (
    id int(11) NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NULL,
    creation_date DATETIME NULL,
    updated_date DATETIME NULL,
    PRIMARY KEY (id)
);
SET FOREIGN_KEY_CHECKS = 1;
ALTER TABLE user AUTO_INCREMENT = 1;
CREATE TABLE address (
     id int(11) NOT NULL AUTO_INCREMENT,
     user_id int(11) NOT NULL,
     street VARCHAR(100) NOT NULL,
     postal_code VARCHAR(30) NOT NULL,
     city VARCHAR(50) NOT NULL,
     country VARCHAR(50) NOT NULL,
     creation_date DATETIME NULL,
     updated_date DATETIME NULL,
     PRIMARY KEY (id),
     FOREIGN KEY (user_id) REFERENCES user (id)
);
CREATE TABLE event (
   id int(11) NOT NULL AUTO_INCREMENT,
   name VARCHAR(100) NOT NULL,
   type VARCHAR(100) NOT NULL,
   date DATETIME NOT NULL,
   image LONGTEXT NOT NULL,
   address_id int(11) NOT NULL,
   PRIMARY KEY (id),
   FOREIGN KEY (address_id) REFERENCES address (id)
);
CREATE TABLE artwork (
     id int(11) NOT NULL AUTO_INCREMENT,
     user_id int(11) NOT NULL,
     title VARCHAR(255) NOT NULL,
     price FLOAT(2) NOT NULL,
     technique VARCHAR(50) NOT NULL,
     image LONGTEXT NOT NULL,
     creation_date DATETIME NULL,
     updated_date DATETIME NULL,
     PRIMARY KEY (id),
     FOREIGN KEY (user_id) REFERENCES user (id)
);
INSERT INTO user (id, username, password, role)
SELECT 1,
    'default_admin',
    '$2a$10$a7AvXfDWvi/WfY.E1UL1BuZQXif01EosSjc9YQjhvDMpmjbshPVqS',
    2
WHERE NOT EXISTS (
        SELECT *
        FROM user
        WHERE username = 'default_admin'
    );
INSERT INTO user (id, username, password, role)
SELECT 2,
    'default_user',
    '$2a$10$JM6I4cvalc8uLf8yF3cqpu6sqde3kzxUb8SYPQA00GA9pkOzWLe9i',
    0
WHERE NOT EXISTS (
        SELECT *
        FROM user
        WHERE username = 'default_user'
    );
INSERT INTO user (id, username, password, role)
SELECT 3,
    'default_artist',
    '$2a$10$uV8fB1ugmuHPNXaP66bhLOs6zUgSMIAT/xF8anWsxYvGOARdii2HC',
    1
WHERE NOT EXISTS (
        SELECT *
        FROM user
        WHERE username = 'default_artist'
    );
-- INSERT INTO address (id, street, postal_code, city, country, user_id)
-- SELECT 1, 'admin_street', '11111', 'admin_city', 'admin_country', 1
-- WHERE NOT EXISTS (SELECT * FROM address WHERE id=1);
INSERT INTO address (
        id,
        street,
        postal_code,
        city,
        country,
        creation_date,
        updated_date,
        user_id
    )
SELECT 1,
    'user_street',
    '00000',
    'user_city',
    'user_country',
    now(),
    now(),
    2
WHERE NOT EXISTS (
        SELECT *
        FROM address
        WHERE id = 1
    );
INSERT INTO user (id, username, password, role)
SELECT 4,
    'another_artist',
    '$2a$10$rGL7tlaDy7gJMGUU1U0W6e2HKgrM7RUznqR8unI.Lbpaxvqhwucpq',
    1
WHERE NOT EXISTS (
        SELECT *
        FROM user
        WHERE username = 'another_artist'
    );