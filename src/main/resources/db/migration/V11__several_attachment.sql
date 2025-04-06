alter table bookcrossing.t_attach add column book_id integer not null;
alter table bookcrossing.t_attach drop constraint attach_foreign_book;
alter table bookcrossing.t_attach
    add constraint attach_foreign_book
        foreign key (book_id) references bookcrossing.t_book (book_id);

create index if not exists attach_foreign_book_idx on bookcrossing.t_attach(book_id);

alter table bookcrossing.t_book add column title_attachment integer;
alter table bookcrossing.t_book
    add constraint book_foreign_title_attachment
        foreign key (title_attachment) references bookcrossing.t_attach (attach_id);

create index if not exists book_foreign_title_attachment_idx on bookcrossing.t_book(title_attachment) where title_attachment is not null;