alter table bookcrossing.t_attach add column book_id integer not null;
ALTER TABLE bookcrossing.t_attach DROP CONSTRAINT attach_foreign_book;
alter table bookcrossing.t_attach
    add constraint attach_foreign_book
        foreign key (book_id) references bookcrossing.t_book (book_id);

alter table bookcrossing.t_book add column title_attachment integer;
alter table bookcrossing.t_book
    add constraint book_foreign_title_attachment
        foreign key (title_attachment) references bookcrossing.t_attach (attach_id);