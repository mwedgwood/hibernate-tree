package com.github.mwedgwood.model.tree;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("SIMPLE_TREE")
public class SimpleTree extends Tree<TreeElement> {

    private SimpleTree(TreeElement element, SimpleTree parent) {
        super(element, parent);
    }

    public static SimpleTree createRoot(String name, String description) {
        return new SimpleTree(new TreeElement(name, description), null);
    }

    public SimpleTree addChild(TreeElement element) {
        return (SimpleTree) addChildTree(new SimpleTree(element, this));
    }

}
