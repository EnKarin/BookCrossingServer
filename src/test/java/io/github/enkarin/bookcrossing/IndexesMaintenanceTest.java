package io.github.enkarin.bookcrossing;

import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.mfvanek.pg.checks.predicates.FilterTablesByNamePredicate;
import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.DbObject;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.index.IndexWithColumns;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableNameAware;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
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
                .startsWith("PostgreSQL 16.2");
    }

    @Test
    void databaseStructureCheckForBookCrossingSchema() {
        assertThat(checks)
                .hasSameSizeAs(Diagnostic.values());

        checks.forEach(check -> {
            final List<? extends DbObject> checkResult = check.check(PG_CONTEXT);

            switch (check.getDiagnostic()) {
                case TABLES_WITHOUT_DESCRIPTION -> assertThat(checkResult)
                        .asInstanceOf(list(Table.class))
                        .filteredOn(skipFlywayTable())
                        .isEmpty();

                case COLUMNS_WITHOUT_DESCRIPTION -> assertThat(checkResult)
                        .asInstanceOf(list(Column.class))
                        .filteredOn(skipFlywayTable())
                        .hasSize(43);

                case INDEXES_WITH_BOOLEAN -> assertThat(checkResult)
                        .asInstanceOf(list(IndexWithColumns.class))
                        .filteredOn(skipFlywayTable())
                        .isEmpty();

                case TABLES_WITH_MISSING_INDEXES, UNUSED_INDEXES -> assertThat(checkResult)
                        .hasSizeLessThanOrEqualTo(1); // TODO skip runtime checks after https://github.com/mfvanek/pg-index-health/issues/456

                default -> assertThat(checkResult)
                        .as(check.getDiagnostic().name())
                        .isEmpty();

            }
        });
    }

    @Nonnull
    private static Predicate<TableNameAware> skipFlywayTable() {
        return FilterTablesByNamePredicate.of(String.format("%s.flyway_schema_history", SCHEMA_NAME));
    }
}
