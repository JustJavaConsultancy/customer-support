package edu.oau.repository.rowmapper;

import edu.oau.domain.Issue;
import edu.oau.domain.enumeration.CLASSIFICATION;
import edu.oau.domain.enumeration.ENTRYCHANNEL;
import edu.oau.domain.enumeration.ISSUESTATUS;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Issue}, with proper type conversions.
 */
@Service
public class IssueRowMapper implements BiFunction<Row, String, Issue> {

    private final ColumnConverter converter;

    public IssueRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Issue} stored in the database.
     */
    @Override
    public Issue apply(Row row, String prefix) {
        Issue entity = new Issue();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", LocalDate.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", ISSUESTATUS.class));
        entity.setClassification(converter.fromRow(row, prefix + "_classification", CLASSIFICATION.class));
        entity.setEntryChannel(converter.fromRow(row, prefix + "_entry_channel", ENTRYCHANNEL.class));
        entity.setCategoryId(converter.fromRow(row, prefix + "_category_id", Long.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", Long.class));
        return entity;
    }
}
