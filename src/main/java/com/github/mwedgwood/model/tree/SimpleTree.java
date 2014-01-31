package com.github.mwedgwood.model.tree;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "simple_tree")
public class SimpleTree extends Tree {

    private String description;

    SimpleTree() {
    }

    @Column(name = "descripton")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
