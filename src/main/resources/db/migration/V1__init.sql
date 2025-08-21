create schema if not exists bookcrossing;

create sequence if not exists bookcrossing.t_book_seq start 1 increment 1;
create sequence if not exists bookcrossing.t_messages_seq start 1 increment 1;
create sequence if not exists bookcrossing.t_user_seq start 1 increment 1;

create table if not exists bookcrossing.t_action_mail_user (
    confirmation_mail varchar(255) not null,
    type integer,
    user_id integer not null,
    primary key (confirmation_mail)
);

comment on table bookcrossing.t_action_mail_user is 'Confirmation token table';

create table if not exists bookcrossing.t_attach (
    attach_id integer not null,
    data bytea,
    expansion varchar(255),
    primary key (attach_id)
);

comment on table bookcrossing.t_attach is 'Table of book images';

create table if not exists bookcrossing.t_book (
    book_id integer not null,
    author varchar(255),
    genre varchar(255),
    publishing_house varchar(255),
    title varchar(255) not null,
    year integer not null,
    owner_book integer,
    primary key (book_id)
);

comment on table bookcrossing.t_book is 'User books table';

create table if not exists bookcrossing.t_bookmarks (
    user_id integer not null,
    book_id integer not null,
    primary key (user_id, book_id)
);

comment on table bookcrossing.t_bookmarks is 'User bookmarks table';

create table if not exists bookcrossing.t_correspondence (
    second_user_id integer not null,
    first_user_id integer not null,
    primary key (first_user_id, second_user_id)
);

comment on table bookcrossing.t_correspondence is 'Table of conversations between two users';

create table if not exists bookcrossing.t_messages (
    message_id bigint not null,
    alert_sent boolean not null,
    declaim boolean not null,
    departure_date bigint not null,
    shown_first_user boolean not null,
    shown_second_user boolean not null,
    text varchar(255),
    correspondence_first_user_id integer,
    correspondence_second_user_id integer,
    sender_user_id integer,
    primary key (message_id)
);

comment on table bookcrossing.t_messages is 'Table of chat messages';

create table if not exists bookcrossing.t_refresh (
    refresh_id varchar(255) not null,
    date bigint not null,
    r_user varchar(255),
    primary key (refresh_id)
);

comment on table bookcrossing.t_refresh is 'Table of tokens for updating access';

create table if not exists bookcrossing.t_role (
    role_id integer not null,
    name varchar(255),
    primary key (role_id)
);

comment on table bookcrossing.t_role is 'Access roles table';

create table if not exists bookcrossing.t_user (
    user_id integer not null,
    account_non_locked boolean not null,
    city varchar(255),
    email varchar(255),
    enabled boolean not null,
    login varchar(255),
    login_date bigint not null,
    name varchar(255),
    password varchar(255),
    primary key (user_id)
);

comment on table bookcrossing.t_user is 'Basic user data';

create table if not exists bookcrossing.t_user_role (
    user_id integer not null,
    role_id integer not null,
    primary key (user_id, role_id)
);

comment on table bookcrossing.t_user_role is 'Table of user and role accordance';

alter table if exists bookcrossing.t_action_mail_user
    add constraint action_foreign_user
        foreign key (user_id) references bookcrossing.t_user (user_id) ON DELETE CASCADE;

create index if not exists action_foreign_user on bookcrossing.t_action_mail_user (user_id);

alter table if exists bookcrossing.t_attach
    add constraint attach_foreign_book
        foreign key (attach_id) references bookcrossing.t_book (book_id);

alter table if exists bookcrossing.t_book
    add constraint book_foreign_user
        foreign key (owner_book) references bookcrossing.t_user (user_id);

create index if not exists book_foreign_user on bookcrossing.t_book (owner_book)
    where owner_book is not null;

alter table if exists bookcrossing.t_bookmarks
    add constraint bookmarks_foreign_book
        foreign key (book_id) references bookcrossing.t_book (book_id);

create index if not exists bookmarks_foreign_book on bookcrossing.t_bookmarks (book_id);

alter table if exists bookcrossing.t_bookmarks
    add constraint bookmarks_foreign_user
        foreign key (user_id) references bookcrossing.t_user (user_id);

alter table if exists bookcrossing.t_correspondence
    add constraint correspondence_foreign_first_user
        foreign key (first_user_id) references bookcrossing.t_user (user_id);

alter table if exists bookcrossing.t_correspondence
    add constraint correspondence_foreign_second_user
        foreign key (second_user_id) references bookcrossing.t_user (user_id);

create index if not exists correspondence_foreign_second_user on bookcrossing.t_correspondence (second_user_id);

alter table if exists bookcrossing.t_messages
    add constraint messages_foreign_correspondence
        foreign key (correspondence_first_user_id, correspondence_second_user_id)
            references bookcrossing.t_correspondence (first_user_id, second_user_id);

create index if not exists messages_foreign_correspondence on bookcrossing.t_messages (correspondence_first_user_id, correspondence_second_user_id)
    where correspondence_first_user_id is not null;

alter table if exists bookcrossing.t_messages
    add constraint messages_foreign_user
        foreign key (sender_user_id) references bookcrossing.t_user (user_id);

create index if not exists messages_foreign_user on bookcrossing.t_messages (sender_user_id)
    where sender_user_id is not null;

alter table if exists bookcrossing.t_user_role
    add constraint user_role_foreign_role
        foreign key (role_id) references bookcrossing.t_role (role_id);

create index if not exists user_role_foreign_role on bookcrossing.t_user_role (role_id);

alter table if exists bookcrossing.t_user_role
    add constraint user_role_foreign_user
        foreign key (user_id) references bookcrossing.t_user (user_id);

insert into bookcrossing.t_role (role_id, name) values (0, 'ROLE_ADMIN'), (1, 'ROLE_USER');

insert into bookcrossing.t_user(user_id, email, name, login, password, account_non_locked, enabled, login_date)
values (0, 'al@mail.ru', 'superUser', 'admin', '$2a$10$suA9HnXlQoDSH0AQ/.Y60.JX/FxBkb1FIh.aX2I3vJ0RpfSZqHR.q',
       true, true, 1652629914);

insert into bookcrossing.t_user_role values (0, 0);
