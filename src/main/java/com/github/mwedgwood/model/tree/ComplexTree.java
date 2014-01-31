package com.github.mwedgwood.model.tree;

import com.github.mwedgwood.model.SomeModel;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name = "complex_tree")
public class ComplexTree extends Tree {

    private SomeModel someModel;

    ComplexTree() {
    }

    @OneToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "some_model_id")
    public SomeModel getSomeModel() {
        return someModel;
    }

    public ComplexTree setSomeModel(SomeModel someModel) {
        this.someModel = someModel;
        return this;
    }
}
