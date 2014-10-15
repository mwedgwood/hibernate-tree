package com.github.mwedgwood.model.tree;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "simple_tree_node")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "tree-cache")
public class SimpleTreeNode extends TreeNode {

    SimpleTreeNode() {
    }

    protected SimpleTreeNode(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return getName();
    }

}
