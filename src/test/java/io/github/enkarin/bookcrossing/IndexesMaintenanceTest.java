package io.github.enkarin.bookcrossing;

import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.index.IndexWithColumns;
import io.github.mfvanek.pg.model.predicates.SkipFlywayTablesPredicate;
import io.github.mfvanek.pg.model.table.Table;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

class IndexesMaintenanceTest extends BookCrossingBaseTests {

    private static final String SCHEMA_NAME = "bookcrossing";
    private static final PgContext PG_CONTEXT = PgContext.of(SCHEMA_NAME);

    @Autowired
    private List<DatabaseCheckOnHost<? extends DbObject>> checks;

    @Test
    void checkPostgresVersion() {
        assertThat(jdbcTemplate.queryForObject("select version();", String.class))
                .startsWith("PostgreSQL 16.4");
    }

    @Test
    void databaseStructureCheckForBookCrossingSchema() {
        assertThat(checks)
                .hasSameSizeAs(Diagnostic.values());

        checks.stream()
                .filter(DatabaseCheckOnHost::isStatic)
                .forEach(check -> {
                    final ListAssert<? extends DbObject> checkAssert = assertThat(check.check(PG_CONTEXT, SkipFlywayTablesPredicate.of(PG_CONTEXT)))
                            .as(check.getDiagnostic().name());

                    switch (check.getDiagnostic()) {
                        case COLUMNS_WITH_FIXED_LENGTH_VARCHAR -> checkAssert.hasSize(15);
                        case COLUMNS_WITHOUT_DESCRIPTION -> checkAssert.hasSize(43);
                        case TABLES_NOT_LINKED_TO_OTHERS -> checkAssert
                                .asInstanceOf(list(Table.class))
                                .hasSize(1)
                                .containsExactly(Table.of(PG_CONTEXT, "t_refresh"));
                        case PRIMARY_KEYS_THAT_MOST_LIKELY_NATURAL_KEYS -> checkAssert
                                .asInstanceOf(list(IndexWithColumns.class))
                                .hasSize(2)
                                .containsExactly(
                                        IndexWithColumns.ofSingle(PG_CONTEXT, "t_action_mail_user", "t_action_mail_user_pkey", 0L,
                                                Column.ofNotNull(PG_CONTEXT, "t_action_mail_user", "confirmation_mail")),
                                        IndexWithColumns.ofSingle(PG_CONTEXT, "t_refresh", "t_refresh_pkey", 0L,
                                                Column.ofNotNull(PG_CONTEXT, "t_refresh", "refresh_id"))
                                );
                        default -> checkAssert.isEmpty();
                    }
                });
    }
}
