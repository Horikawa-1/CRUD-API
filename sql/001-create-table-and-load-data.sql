drop table IF EXISTS users;

create TABLE users (
 id int unsigned AUTO_INCREMENT,
 name VARCHAR(20) NOT NULL,
 PRIMARY KEY(id)
);


insert into users (name) values ("Honma");
insert into users (name) values ("Nakashima");
insert into users (name) values ("Itou");
