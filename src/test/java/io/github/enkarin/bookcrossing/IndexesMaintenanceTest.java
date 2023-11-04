package io.github.enkarin.bookcrossing;

import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.mfvanek.pg.checks.host.ColumnsWithJsonTypeCheckOnHost;
import io.github.mfvanek.pg.checks.host.ColumnsWithSerialTypesCheckOnHost;
import io.github.mfvanek.pg.checks.host.DuplicatedIndexesCheckOnHost;
import io.github.mfvanek.pg.checks.host.ForeignKeysNotCoveredWithIndexCheckOnHost;
import io.github.mfvanek.pg.checks.host.IndexesWithNullValuesCheckOnHost;
import io.github.mfvanek.pg.checks.host.IntersectedIndexesCheckOnHost;
import io.github.mfvanek.pg.checks.host.InvalidIndexesCheckOnHost;
import io.github.mfvanek.pg.checks.host.TablesWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.checks.host.TablesWithoutPrimaryKeyCheckOnHost;
import io.github.mfvanek.pg.checks.predicates.FilterTablesByNamePredicate;
import io.github.mfvanek.pg.model.PgContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class IndexesMaintenanceTest extends BookCrossingBaseTests {

    private static final String SCHEMA_NAME = "bookcrossing";
    private static final PgContext PG_CONTEXT = PgContext.of(SCHEMA_NAME);
    @Autowired
    private InvalidIndexesCheckOnHost invalidIndexesCheck;
    @Autowired
    private DuplicatedIndexesCheckOnHost duplicatedIndexesCheck;
    @Autowired
    private IntersectedIndexesCheckOnHost intersectedIndexesCheck;
    @Autowired
    private ForeignKeysNotCoveredWithIndexCheckOnHost foreignKeysNotCoveredWithIndexCheck;
    @Autowired
    private TablesWithoutPrimaryKeyCheckOnHost tablesWithoutPrimaryKeyCheck;
    @Autowired
    private IndexesWithNullValuesCheckOnHost indexesWithNullValuesCheck;
    @Autowired
    private ColumnsWithJsonTypeCheckOnHost columnsWithJsonTypeCheck;
    @Autowired
    private ColumnsWithSerialTypesCheckOnHost columnsWithSerialTypesCheck;
    @Autowired
    private TablesWithoutDescriptionCheckOnHost tablesWithoutDescriptionCheck;

    @Test
    void checkPostgresVersion() {
        assertThat(jdbcTemplate.queryForObject("select version();", String.class))
                .startsWith("PostgreSQL 16.0");
    }

    @Test
    void getInvalidIndexesShouldReturnNothing() {
        assertThat(invalidIndexesCheck.check(PG_CONTEXT))
                .isEmpty();
    }

    @Test
    void getDuplicatedIndexesShouldReturnNothing() {
        assertThat(duplicatedIndexesCheck.check(PG_CONTEXT))
                .isEmpty();
    }

    @Test
    void getIntersectedIndexesShouldReturnNothing() {
        assertThat(intersectedIndexesCheck.check(PG_CONTEXT))
                .isEmpty();
    }

    @Test
    void getForeignKeysNotCoveredWithIndexShouldReturnNothing() {
        assertThat(foreignKeysNotCoveredWithIndexCheck.check(PG_CONTEXT))
                .isEmpty();
    }

    @Test
    void getIndexesWithNullValuesShouldReturnNothing() {
        assertThat(indexesWithNullValuesCheck.check(PG_CONTEXT))
                .isEmpty();
    }

    @Test
    void getTablesWithoutPrimaryKeyShouldReturnNothing() {
        assertThat(tablesWithoutPrimaryKeyCheck.check(PG_CONTEXT))
                .isEmpty();
    }

    @Test
    void shouldNotContainsColumnsOfJsonType() {
        assertThat(columnsWithJsonTypeCheck.check(PG_CONTEXT))
                .isEmpty();
    }

    @Test
    void shouldNotContainsColumnsWithSerialTypes() {
        assertThat(columnsWithSerialTypesCheck.check(PG_CONTEXT))
                .isEmpty();
    }

    @Test
    void allTablesShouldHaveDescription() {
        assertThat(tablesWithoutDescriptionCheck.check(PG_CONTEXT))
                .filteredOn(FilterTablesByNamePredicate.of(String.format("%s.flyway_schema_history", SCHEMA_NAME)))
                .isEmpty();
    }
}
