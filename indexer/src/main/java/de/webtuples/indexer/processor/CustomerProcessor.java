
package de.webtuples.indexer.processor;


import de.webtuples.indexer.jpa.Account;
import de.webtuples.indexer.jpa.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerProcessor.class);

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Customer process(Customer customer) throws Exception {

        List<Account> accounts = accountRepository.findByAccountsByCustomerId(customer.getCustomerId());

        for (Account account : accounts) {

            // Account Data


        return customer;
    }


}
