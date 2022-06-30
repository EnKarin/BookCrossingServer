insert into t_user(user_id, email, name, login, password, city, account_non_locked, enabled, login_date)
values (50, "karr@mail.ru", "user", "user", "$2a$10$suA9HnXlQoDSH0AQ/.Y60.JX/FxBkb1FIh.aX2I3vJ0RpfSZqHR.q",
        "Novosibirsk", true, true, 1652629914);

insert into t_user_role value (50, 1);

insert into t_user(user_id, email, name, login, password, account_non_locked, enabled, login_date)
values (66, "karr@mail.ru", "user", "alex", "$2a$10$suA9HnXlQoDSH0AQ/.Y60.JX/FxBkb1FIh.aX2I3vJ0RpfSZqHR.q",
        true, true, 1652629914);

insert into t_user_role value (66, 1);