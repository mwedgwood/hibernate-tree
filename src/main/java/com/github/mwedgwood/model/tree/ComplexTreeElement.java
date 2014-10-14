package com.github.mwedgwood.model.tree;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "complex_tree_element")
public class ComplexTreeElement extends TreeElement {

    private String anotherField;

    public ComplexTreeElement(String name, String description) {
        super(name, description);
    }

    @Column(name = "another_field")
    public String getAnotherField() {
        return anotherField;
    }

    public void setAnotherField(String anotherField) {
        this.anotherField = anotherField;
    }
}
