package com.github.mwedgwood.model.tree;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "complex_tree")
public class ComplexTree extends Tree {

    private String stuff;
    private String moreStuff;

    ComplexTree() {
    }

    @Column(name = "stuff")
    public String getStuff() {
        return stuff;
    }

    public void setStuff(String stuff) {
        this.stuff = stuff;
    }

    @Column(name = "more_stuff")
    public String getMoreStuff() {
        return moreStuff;
    }

    public void setMoreStuff(String moreStuff) {
        this.moreStuff = moreStuff;
    }
}
