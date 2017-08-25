package de.webtuples.indexer.reader;

import java.util.Date;

import javax.annotation.PostConstruct;

import de.webtuples.indexer.joblaunch.JobParameterKey;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;

import de.webtuples.indexer.config.PersistenceConfiguration;

abstract class BaseItemReader<T> extends JdbcCursorItemReader {

    @Autowired
    private PersistenceConfiguration persistenceConfiguration;

    @Value("#{jobParameters['" + JobParameterKey.INDEX_FROM_DATE + "']}")
    private Date indexFromDate;

    @PostConstruct
    public void init() {
        setDataSource(persistenceConfiguration.dataSource());
        setSql(getSqlQueryReader());

        RowMapper<T> rowMapper = buildRowMapper();

        setRowMapper(rowMapper);
        setPreparedStatementSetter(
                ps -> ps.setDate(1, new java.sql.Date(indexFromDate.getTime())));
    }

    protected abstract String getSqlQueryReader();

    protected abstract RowMapper<T> buildRowMapper();
}
