create table hibernate_sequence (
    next_val bigint
);

insert into hibernate_sequence values ( 1 );

create table t_action_mail_user (
    confirmation_mail varchar(255) not null,
    type integer,
    user_id integer not null,
    primary key (confirmation_mail)
);

create table t_attach (
    name varchar(255) not null,
    data longblob,
    expansion varchar(255),
    primary key (name)
);

create table t_book (
    book_id integer not null,
    author varchar(255),
    genre varchar(255),
    publishing_house varchar(255),
    title varchar(255) not null,
    year integer not null,
    attach_id varchar(255),
    owner_book integer,
    primary key (book_id)
);

create table t_bookmarks (
    user_id integer not null,
    book_id integer not null,
    primary key (user_id, book_id)
);

create table t_correspondence (
    second_user_id integer not null,
    first_user_id integer not null,
    primary key (first_user_id, second_user_id)
);

create table t_messages (
    message_id bigint not null,
    alert_sent bit not null,
    declaim bit not null,
    departure_date bigint not null,
    shown_first_user bit not null,
    shown_second_user bit not null,
    text varchar(255),
    correspondence_first_user_id integer,
    correspondence_second_user_id integer,
    sender_user_id integer,
    primary key (message_id)
);

create table t_refresh (
    refresh varchar(255) not null,
    date bigint not null,
    user varchar(255),
    primary key (refresh)
);

create table t_role (
    role_id integer not null,
    name varchar(255),
    primary key (role_id)
);

create table t_user (
    user_id integer not null,
    account_non_locked bit not null,
    city varchar(255),
    email varchar(255),
    enabled bit not null,
    login varchar(255),
    login_date bigint not null,
    name varchar(255),
    password varchar(255),
    primary key (user_id)
);

create table t_user_role (
    user_id integer not null,
    role_id integer not null,
    primary key (user_id, role_id)
);

alter table t_action_mail_user
    add constraint action_foreign_user
        foreign key (user_id) references t_user (user_id);

alter table t_book
    add constraint book_foreign_attach
        foreign key (attach_id) references t_attach (name);

alter table t_book
    add constraint book_foreign_user
        foreign key (owner_book) references t_user (user_id);

alter table t_bookmarks
    add constraint bookmarks_foreign_book
        foreign key (book_id) references t_book (book_id);

alter table t_bookmarks
    add constraint bookmarks_foreign_user
        foreign key (user_id) references t_user (user_id);

alter table t_correspondence
    add constraint correspondence_foreign_first_user
        foreign key (first_user_id) references t_user (user_id);

alter table t_correspondence
    add constraint correspondence_foreign_second_user
        foreign key (second_user_id) references t_user (user_id);

alter table t_messages
    add constraint messages_foreign_correspondence
        foreign key (correspondence_first_user_id, correspondence_second_user_id)
            references t_correspondence (first_user_id, second_user_id);

alter table t_messages
    add constraint messages_foreign_user
        foreign key (sender_user_id) references t_user (user_id);

alter table t_user_role
    add constraint user_role_foreign_role
        foreign key (role_id) references t_role (role_id);

alter table t_user_role
    add constraint user_role_foreign_user
        foreign key (user_id) references t_user (user_id);

insert into t_role (role_id, name) values (0, 'ROLE_ADMIN'), (1, 'ROLE_USER');

insert into t_user(user_id, email, name, login, password, account_non_locked, enabled, login_date)
values (0, "al@mail.ru", "superUser", "admin", "$2a$10$suA9HnXlQoDSH0AQ/.Y60.JX/FxBkb1FIh.aX2I3vJ0RpfSZqHR.q",
       true, true, 1652629914);

insert into  t_user_role value (0, 0);

insert into  t_user_role value (0, 1);