package com.github.mwedgwood.model.tree;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("COMPLEX_ELEMENT")
public class ComplexTreeElement extends TreeElement {

    ComplexTreeElement() {
    }

    public ComplexTreeElement(String name, String description) {
        super(name, description);
    }
}
