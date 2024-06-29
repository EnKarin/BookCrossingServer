package io.github.enkarin.bookcrossing;

import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.mfvanek.pg.checks.host.BtreeIndexesOnArrayColumnsCheckOnHost;
import io.github.mfvanek.pg.checks.host.ColumnsWithJsonTypeCheckOnHost;
import io.github.mfvanek.pg.checks.host.ColumnsWithSerialTypesCheckOnHost;
import io.github.mfvanek.pg.checks.host.ColumnsWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.checks.host.DuplicatedIndexesCheckOnHost;
import io.github.mfvanek.pg.checks.host.ForeignKeysNotCoveredWithIndexCheckOnHost;
import io.github.mfvanek.pg.checks.host.FunctionsWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.checks.host.IndexesWithBooleanCheckOnHost;
import io.github.mfvanek.pg.checks.host.IndexesWithNullValuesCheckOnHost;
import io.github.mfvanek.pg.checks.host.IntersectedIndexesCheckOnHost;
import io.github.mfvanek.pg.checks.host.InvalidIndexesCheckOnHost;
import io.github.mfvanek.pg.checks.host.NotValidConstraintsCheckOnHost;
import io.github.mfvanek.pg.checks.host.PrimaryKeysWithSerialTypesCheckOnHost;
import io.github.mfvanek.pg.checks.host.SequenceOverflowCheckOnHost;
import io.github.mfvanek.pg.checks.host.TablesWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.checks.host.TablesWithoutPrimaryKeyCheckOnHost;
import io.github.mfvanek.pg.checks.predicates.FilterTablesByNamePredicate;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.TableNameAware;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("PMD.TooManyFields")
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
    private TablesWithoutDescriptionCheckOnHost tablesWithoutDescriptionCheck;
    @Autowired
    private ColumnsWithoutDescriptionCheckOnHost columnsWithoutDescriptionCheck;
    @Autowired
    private ColumnsWithJsonTypeCheckOnHost columnsWithJsonTypeCheck;
    @Autowired
    private ColumnsWithSerialTypesCheckOnHost columnsWithSerialTypesCheck;
    @Autowired
    private FunctionsWithoutDescriptionCheckOnHost functionsWithoutDescriptionCheck;
    @Autowired
    private IndexesWithBooleanCheckOnHost indexesWithBooleanCheck;
    @Autowired
    private NotValidConstraintsCheckOnHost notValidConstraintsCheck;
    @Autowired
    private BtreeIndexesOnArrayColumnsCheckOnHost btreeIndexesOnArrayColumnsCheck;
    @Autowired
    private SequenceOverflowCheckOnHost sequenceOverflowCheck;
    @Autowired
    private PrimaryKeysWithSerialTypesCheckOnHost primaryKeysWithSerialTypesCheck;

    @Test
    void checkPostgresVersion() {
        assertThat(jdbcTemplate.queryForObject("select version();", String.class))
                .startsWith("PostgreSQL 16.2");
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
    void getTablesWithoutPrimaryKeyShouldReturnNothing() {
        assertThat(tablesWithoutPrimaryKeyCheck.check(PG_CONTEXT))
                .isEmpty();
    }

    @Test
    void getIndexesWithNullValuesShouldReturnNothing() {
        assertThat(indexesWithNullValuesCheck.check(PG_CONTEXT))
                .isEmpty();
    }

    @Test
    void allTablesShouldHaveDescription() {
        assertThat(tablesWithoutDescriptionCheck.check(PG_CONTEXT))
                .filteredOn(skipFlywayTable())
                .isEmpty();
    }

    @Test
    void allNewColumnsShouldHaveDescription() {
        assertThat(columnsWithoutDescriptionCheck.check(PG_CONTEXT))
                .filteredOn(skipFlywayTable())
                .hasSize(43);
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
    void getFunctionsWithoutDescriptionShouldReturnNothing() {
        assertThat(functionsWithoutDescriptionCheck.check(PG_CONTEXT))
                .isEmpty();
    }

    @Test
    void indexesWithBooleanShouldReturnNothing() {
        assertThat(indexesWithBooleanCheck.check(PG_CONTEXT))
                .filteredOn(skipFlywayTable())
                .isEmpty();
    }

    @Test
    void notValidConstraintsShouldReturnNothing() {
        assertThat(notValidConstraintsCheck.check(PG_CONTEXT))
                .isEmpty();
    }

    @Test
    void btreeIndexesOnArrayColumnsShouldReturnNothing() {
        assertThat(btreeIndexesOnArrayColumnsCheck.check(PG_CONTEXT))
                .isEmpty();
    }

    @Test
    void sequenceOverflowShouldReturnNothing() {
        assertThat(sequenceOverflowCheck.check(PG_CONTEXT))
                .isEmpty();
    }

    @Test
    void getPrimaryKeysWithSerialTypesShouldReturnNothing() {
        assertThat(primaryKeysWithSerialTypesCheck.check(PG_CONTEXT))
                .isEmpty();
    }

    @Nonnull
    private static Predicate<TableNameAware> skipFlywayTable() {
        return FilterTablesByNamePredicate.of(String.format("%s.flyway_schema_history", SCHEMA_NAME));
    }
}
