package com.github.mwedgwood.model.tree;

import javax.persistence.*;


@Entity
@Table(name = "tree_element")
@Inheritance(strategy = InheritanceType.JOINED)
public class TreeElement {

    private Integer id;
    private String name;
    private String description;

    TreeElement() {
    }

    public TreeElement(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return getName();
    }

}
