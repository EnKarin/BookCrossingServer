package io.github.enkarin.bookcrossing;

import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.DbObject;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableNameAware;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Predicate;

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
                    // TODO remove after https://github.com/mfvanek/pg-index-health/issues/477
                    //  and https://github.com/mfvanek/pg-index-health/issues/478
                    final Predicate<DbObject> skipFlywayTable = dbObject -> {
                        if (dbObject instanceof TableNameAware table) {
                            return !table.getTableName().equalsIgnoreCase(PG_CONTEXT.enrichWithSchema("flyway_schema_history"));
                        }
                        return true;
                    };
                    final ListAssert<? extends DbObject> checkAssert = assertThat(check.check(PG_CONTEXT, skipFlywayTable))
                            .as(check.getDiagnostic().name());

                    if (check.getDiagnostic() == Diagnostic.COLUMNS_WITHOUT_DESCRIPTION) {
                        checkAssert.hasSize(43);
                    } else if (check.getDiagnostic() == Diagnostic.TABLES_NOT_LINKED_TO_OTHERS) {
                        checkAssert
                                .asInstanceOf(list(Table.class))
                                .hasSize(1)
                                .containsExactly(Table.of(PG_CONTEXT.enrichWithSchema("t_refresh"), 0L));
                    } else {
                        checkAssert.isEmpty();
                    }
                });
    }
}
