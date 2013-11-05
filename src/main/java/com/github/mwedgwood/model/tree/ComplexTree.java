package com.github.mwedgwood.model.tree;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("COMPLEX_TREE")
public class ComplexTree extends Tree<ComplexTreeElement> {

    private ComplexTree(ComplexTreeElement element, ComplexTree parent) {
        super(element, parent);
    }

    public static ComplexTree createRoot(String name, String description) {
        return new ComplexTree(new ComplexTreeElement(name, description), null);
    }

    public ComplexTree addChild(ComplexTreeElement element) {
        return (ComplexTree) addChildTree(new ComplexTree(element, this));
    }

}
