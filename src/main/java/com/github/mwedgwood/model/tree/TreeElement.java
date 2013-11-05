package com.github.mwedgwood.model.tree;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;


@Entity
@Table(name = "tree_element")
@DiscriminatorColumn(name = "element_type")
@DiscriminatorValue("BASE_ELEMENT")
public class TreeElement {

    private Integer treeId;
    private String name;
    private String description;

    private Tree<? extends TreeElement> tree;

    TreeElement() {
    }

    public TreeElement(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    @Id
    @GenericGenerator(name = "treeIdGenerator", strategy = "foreign",
            parameters = @Parameter(name = "property", value = "tree"))
    @GeneratedValue(generator = "treeIdGenerator")
    @Column(name = "tree_id")
    public Integer getTreeId() {
        return treeId;
    }

    void setTreeId(Integer treeId) {
        this.treeId = treeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    public Tree<? extends TreeElement> getTree() {
        return tree;
    }

    <T extends TreeElement> void setTree(Tree<T> tree) {
        this.tree = tree;
    }

}
