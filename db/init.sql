CREATE DATABASE IF NOT EXISTS `backendtest`;
USE backendtest;
CREATE TABLE role
(
    roleid      int AUTO_INCREMENT
        PRIMARY KEY,
    description varchar(255) NULL,
    name        varchar(255) NULL
);
CREATE TABLE user
(
    userid         bigint AUTO_INCREMENT
        PRIMARY KEY,
    active         bit          NULL,
    address        varchar(255) NULL,
    create_date    datetime     NULL,
    full_name      varchar(255) NULL,
    member_type    varchar(255) NULL,
    password       varchar(255) NULL,
    phone          varchar(255) NULL,
    reference_code varchar(255) NULL,
    salary         int          NULL,
    username       varchar(255) NULL
);

CREATE TABLE user_role
(
    userid bigint NOT NULL,
    roleid int    NOT NULL,
    PRIMARY KEY (userid, roleid),
    CONSTRAINT FKbo5ik0bthje7hum554xb17ry6
        FOREIGN KEY (roleid) REFERENCES role (roleid),
    CONSTRAINT FKf61k1a8unv8fuf3fnf2n13nkk
        FOREIGN KEY (userid) REFERENCES user (userid)
);

INSERT INTO role (description, name) VALUES ('user role', 'USER');
