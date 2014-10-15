package com.github.mwedgwood.model.tree;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "complex_tree_node")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "tree-cache")
public class ComplexTreeNode extends SimpleTreeNode {


    private String anotherField;

    ComplexTreeNode() {
    }

    protected ComplexTreeNode(String name) {
        super(name);
    }

    public String getAnotherField() {
        return anotherField;
    }

    public void setAnotherField(String anotherField) {
        this.anotherField = anotherField;
    }
}
