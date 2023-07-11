package edu.oau.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import edu.oau.domain.Issue;
import edu.oau.domain.enumeration.CLASSIFICATION;
import edu.oau.domain.enumeration.ENTRYCHANNEL;
import edu.oau.domain.enumeration.ISSUESTATUS;
import edu.oau.repository.rowmapper.CategoryRowMapper;
import edu.oau.repository.rowmapper.CustomerRowMapper;
import edu.oau.repository.rowmapper.IssueRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Issue entity.
 */
@SuppressWarnings("unused")
class IssueRepositoryInternalImpl extends SimpleR2dbcRepository<Issue, Long> implements IssueRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CategoryRowMapper categoryMapper;
    private final CustomerRowMapper customerMapper;
    private final IssueRowMapper issueMapper;

    private static final Table entityTable = Table.aliased("issue", EntityManager.ENTITY_ALIAS);
    private static final Table categoryTable = Table.aliased("category", "category");
    private static final Table customerTable = Table.aliased("customer", "customer");

    public IssueRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CategoryRowMapper categoryMapper,
        CustomerRowMapper customerMapper,
        IssueRowMapper issueMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Issue.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.categoryMapper = categoryMapper;
        this.customerMapper = customerMapper;
        this.issueMapper = issueMapper;
    }

    @Override
    public Flux<Issue> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Issue> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = IssueSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CategorySqlHelper.getColumns(categoryTable, "category"));
        columns.addAll(CustomerSqlHelper.getColumns(customerTable, "customer"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(categoryTable)
            .on(Column.create("category_id", entityTable))
            .equals(Column.create("id", categoryTable))
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Issue.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Issue> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Issue> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Issue process(Row row, RowMetadata metadata) {
        Issue entity = issueMapper.apply(row, "e");
        entity.setCategory(categoryMapper.apply(row, "category"));
        entity.setCustomer(customerMapper.apply(row, "customer"));
        return entity;
    }

    @Override
    public <S extends Issue> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
