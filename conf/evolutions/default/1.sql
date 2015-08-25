# --- !Ups

-- create table TANGELA_OWNER(
-- id                          bigint auto_increment not null,
-- tangela_id                  bigint not null,
-- name                        varchar(255),
-- bio                         varchar(400),
-- role                        varchar(255),
-- follower_count              bigint,
-- angelList_url               varchar(255),
-- image                       varchar(255),
-- blog_url                    varchar(255),
-- bio_url                     varchar(255),
-- twitter_url                 varchar(255),
-- facebook_url                varchar(255),
-- linkedIn_url                varchar(255),
-- investor                    boolean,
-- constraint pk_tangela_owner primary key (id)
-- );

create table TANGELA_OWNER(
tangela_id                  varchar(255) UNIQUE ,
name                        varchar(255),
bio                         varchar(400),
role                        varchar(255),
linkedIn_url                varchar(255)
)
AS SELECT * FROM CSVREAD('C:\\users-argentina.csv');

-- create table TEST(
-- id                          bigint not null,
-- name                        varchar(255),
-- bio                         varchar(400),
-- role                        varchar(255),
-- follower_count              bigint,
-- angelList_url               varchar(255),
-- image                       varchar(255),
-- blog_url                    varchar(255),
-- bio_url                     varchar(255),
-- twitter_url                 varchar(255),
-- facebook_url                varchar(255),
-- linkedIn_url                varchar(255),
-- what_ive_built              varchar(1000),
-- what_i_do                   varchar(1000),
-- investor                    boolean,
-- )
-- AS SELECT * FROM CSVREAD('C:\\users-argentina.csv');

create table LINKEDIN_OWNER(
id                          bigint auto_increment not null,
-- tangela_owner_id            bigint not null,
name                        varchar(255),
location                    varchar(255),
industry                    varchar(255),
website                     varchar(255),
constraint pk_linkedin_owner primary key (id)
);

create table BUSINESS_INSTITUTION(
id                          bigint auto_increment not null,
name                        varchar(255),
description                 varchar(255),
website                     varchar(255),
sector                      varchar(255),
location                    varchar(255),
size                        varchar(255),
constraint pk_business_institution primary key (id)
);

create table ACADEMIC_INSTITUTION(
id                          bigint auto_increment not null,
name                        varchar(255),
description                 varchar(255),
website                     varchar(255),
constraint pk_academic_institution primary key (id)
);

create table BUSINESS_BACKGROUND(
id                          bigint auto_increment not null,
business_institution_id     bigint not null,
linkedin_owner_id           bigint not null,
role                        varchar(255),
start_date                  varchar(255),
end_date                    varchar(255),
description                 varchar(600),
constraint pk_business_background primary key (id)
);

create table ACADEMIC_BACKGROUND(
id                          bigint auto_increment not null,
academic_institution_id     bigint not null,
linkedin_owner_id           bigint not null,
title                       varchar(255),
start_date                  varchar(255),
end_date                    varchar(255),
description                 varchar(600),
constraint pk_academic_background primary key (id)
);

create sequence tangela_owner_seq;

create sequence linkedin_owner_seq;

create sequence business_institution_seq;

create sequence business_background_seq;

create sequence academic_institution_seq;

create sequence academic_background_seq;

-- alter table LINKEDIN_OWNER add constraint fk_tangela_owner_id foreign key (tangela_owner_id) references TANGELA_OWNER(id) on delete restrict on update restrict;

alter table BUSINESS_BACKGROUND add constraint fk_b_linkedin_owner_id foreign key (linkedin_owner_id) references LINKEDIN_OWNER(id) on delete restrict on update restrict;

alter table BUSINESS_BACKGROUND add constraint fk_business_insitution_id foreign key (business_institution_id) references BUSINESS_INSTITUTION(id) on delete restrict on update restrict;

alter table ACADEMIC_BACKGROUND add constraint fk_a_linkedin_owner_id foreign key (linkedin_owner_id) references LINKEDIN_OWNER(id) on delete restrict on update restrict;

alter table ACADEMIC_BACKGROUND add constraint fk_academic_insitution_id foreign key (academic_institution_id) references ACADEMIC_INSTITUTION(id) on delete restrict on update restrict;

# --- !Downs