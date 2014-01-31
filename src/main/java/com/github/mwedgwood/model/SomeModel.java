package com.github.mwedgwood.model;

import javax.persistence.*;

@Entity
@Table(name = "some_model")
public class SomeModel {

    private Integer id;
    private String someField;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "some_field")
    public String getSomeField() {
        return someField;
    }

    public void setSomeField(String someField) {
        this.someField = someField;
    }
}
