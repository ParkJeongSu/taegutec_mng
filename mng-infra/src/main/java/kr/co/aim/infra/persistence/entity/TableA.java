package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TableA", schema = "dbo")
public class TableA {

    @Id
    @Column(name = "ID")
    private Integer id;

    public TableA() {}

    public TableA(Integer id) {
        this.id = id;
    }

    // Getter, Setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}