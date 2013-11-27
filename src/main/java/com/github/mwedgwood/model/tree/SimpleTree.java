package com.github.mwedgwood.model.tree;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("SIMPLE_TREE")
public class SimpleTree extends Tree<TreeElement> {

    SimpleTree() {
    }
}
