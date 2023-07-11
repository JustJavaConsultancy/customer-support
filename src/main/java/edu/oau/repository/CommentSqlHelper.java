package edu.oau.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class CommentSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("created_date", table, columnPrefix + "_created_date"));
        columns.add(Column.aliased("subject", table, columnPrefix + "_subject"));
        columns.add(Column.aliased("comment", table, columnPrefix + "_comment"));

        columns.add(Column.aliased("issue_id", table, columnPrefix + "_issue_id"));
        return columns;
    }
}
