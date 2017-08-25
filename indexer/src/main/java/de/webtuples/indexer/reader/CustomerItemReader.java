
package de.webtuples.indexer.reader;

import java.sql.Date;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;


@Component
@StepScope
public class CustomerItemReader extends BaseItemReader<Customer> {

    private String sqlQueryReader = "select distinct CUSTOMER.customer_pk where CUSTOMER.TIME_UPDATED > ?";


    @Override
    public String getSqlQueryReader() {
        return sqlQueryReader;
    }

    @Override
    public RowMapper<Customer> buildRowMapper() {
        return (resultSet, rownum) -> {

            Customer customer = new Customer();
             ...
            return customer;
        };
    }
}
