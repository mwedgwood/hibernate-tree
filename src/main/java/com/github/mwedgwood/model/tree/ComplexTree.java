package com.github.mwedgwood.model.tree;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("COMPLEX_TREE")
public class ComplexTree extends Tree<ComplexTreeElement> {

    ComplexTree() {
    }
}
