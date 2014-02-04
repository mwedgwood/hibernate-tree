package com.github.mwedgwood.model.tree;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "complex_tree_element")
@DiscriminatorValue("COMPLEX_ELEMENT")
public class ComplexTreeElement extends TreeElement {

    private String anotherField;

    ComplexTreeElement() {
    }

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
