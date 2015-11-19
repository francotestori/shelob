# --- !Ups

create table LINKEDIN_OWNER(
id                          bigint auto_increment not null,
name                        varchar(255),
location                    varchar(800),
industry                    varchar(255),
website                     varchar(255) not null unique,
tangela_id                  varchar(255),
searched                    boolean,
owner_state_id              bigint not null,
constraint pk_linkedin_owner primary key (id)
);

create table BUSINESS_INSTITUTION(
id                          bigint auto_increment not null,
name                        varchar(255)not null unique,
description                 varchar(1000),
sector                      varchar(255),
location                    varchar(255),
constraint pk_business_institution primary key (id)
);

create table ACADEMIC_INSTITUTION(
id                          bigint auto_increment not null,
name                        varchar(255)not null unique,
description                 varchar(255),
constraint pk_academic_institution primary key (id)
);

create table BUSINESS_BACKGROUND(
id                          bigint auto_increment not null,
business_institution_id     bigint not null,
linkedin_owner_id           bigint not null,
role                        varchar(255),
interval                    varchar(255),
description                 varchar(1600),
constraint pk_business_background primary key (id)
);

create table ACADEMIC_BACKGROUND(
id                          bigint auto_increment not null,
academic_institution_id     bigint not null,
linkedin_owner_id           bigint not null,
title                       varchar(255),
interval                    varchar(255),
description                 varchar(600),
constraint pk_academic_background primary key (id)
);

create table OWNER_STATE(
id                          bigint not null,
description                 varchar(255),
constraint pk_owner_state primary key (id)
);

create sequence linkedin_owner_seq;

create sequence business_institution_seq;

create sequence business_background_seq;

create sequence academic_institution_seq;

create sequence academic_background_seq;

create sequence owner_state_seq;

insert into OWNER_STATE VALUES (1,'Scrap OK');
insert into OWNER_STATE VALUES (2,'Scrap CONNECTION ERROR');
insert into OWNER_STATE VALUES (3,'Scrap VALIDATION ERROR');
insert into OWNER_STATE VALUES (4,'Scrap URL ERROR');

alter table LINKEDIN_OWNER add constraint fk_owner_state_id foreign key (owner_state_id) references OWNER_STATE(id) on delete restrict on update restrict;

alter table BUSINESS_BACKGROUND add constraint fk_b_linkedin_owner_id foreign key (linkedin_owner_id) references LINKEDIN_OWNER(id) on delete restrict on update restrict;

alter table BUSINESS_BACKGROUND add constraint fk_business_insitution_id foreign key (business_institution_id) references BUSINESS_INSTITUTION(id) on delete restrict on update restrict;

alter table ACADEMIC_BACKGROUND add constraint fk_a_linkedin_owner_id foreign key (linkedin_owner_id) references LINKEDIN_OWNER(id) on delete restrict on update restrict;

alter table ACADEMIC_BACKGROUND add constraint fk_academic_insitution_id foreign key (academic_institution_id) references ACADEMIC_INSTITUTION(id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists LINKEDIN_OWNER;

drop table if exists BUSINESS_INSTITUTION;

drop table if exists ACADEMIC_INSTITUTION;

drop table if exists BUSINESS_BACKGROUND;

drop table if exists ACADEMIC_BACKGROUND;

drop table if exists OWNER_STATE;
