package com.github.mwedgwood.model.tree;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("BASE_ELEMENT")
public class ComplexTreeElement extends TreeElement{

    public ComplexTreeElement(String name, String description) {
        super(name, description);
    }
}
