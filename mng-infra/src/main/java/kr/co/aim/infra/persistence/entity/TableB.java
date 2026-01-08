package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"TableB\"", schema = "TESTSCHEMA")
public class TableB {

    @Id
    @Column(name = "ID")
    private Integer id;

    public TableB() {}

    public TableB(Integer id) {
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