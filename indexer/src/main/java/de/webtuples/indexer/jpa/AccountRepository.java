

package de.webtuples.indexer.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Component
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("select p from #{#entityName} p where p.customerId = ?1")
    List<Account> findByAccountsByCustomerId(Long customerId);

}
