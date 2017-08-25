

package de.webtuples.indexer.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ACCOUNT")
public class Account {

    @Id
    @Column(name = "ACCOUNT_PK")
    private Long id;


    public Long getId() {
        return id;
    }

}
