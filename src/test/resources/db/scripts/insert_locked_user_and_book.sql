insert into t_user(user_id, email, name, login, password, account_non_locked, enabled, login_date)
values (66, "karr@mail.ru", "user", "alex", "$2a$10$suA9HnXlQoDSH0AQ/.Y60.JX/FxBkb1FIh.aX2I3vJ0RpfSZqHR.q",
        false, true, 1652629914);

insert into t_user_role value (66, 1);

insert into t_book(book_id, author, genre, publishing_house, title, year, owner_book)
values (4, "author", "genre2", "publishing_house", "title3", 2000, 66);