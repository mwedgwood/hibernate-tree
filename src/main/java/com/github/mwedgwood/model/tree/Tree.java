package com.github.mwedgwood.model.tree;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@NamedQueries({
        @NamedQuery(name = "findRootNode", query = "select id from Tree t where t.class = :aClass and t.parent = null"),
        @NamedQuery(name = "findAllNodesWithTheirChildren", query = "from Tree t left join fetch t.children where t.class = :aClass")
})

@Entity
@Table(name = "tree")
@DiscriminatorColumn(name = "tree_type")
@DiscriminatorValue("BASE_TREE")
public abstract class Tree<T extends TreeElement> {

    private Integer id;
    private T element;
    @JsonBackReference
    private Tree<T> parent;
    @JsonManagedReference
    private List<Tree<T>> children = new LinkedList<>();

    // used by hibernate
    Tree() {
    }

    protected Tree(T element, Tree<T> parent) {
        this.element = element;
        this.parent = parent;
        this.element.setTree(this);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    void setId(Integer id) {
        this.id = id;
    }

    @OneToOne(mappedBy = "tree", cascade = CascadeType.ALL)
    public T getElement() {
        return element;
    }

    void setElement(T element) {
        this.element = element;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Tree<T> getParent() {
        return parent;
    }

    private Tree<T> setParent(Tree<T> parent) {
        this.parent = parent;
        return this;
    }

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @OrderColumn(name = "children_order")
    public List<Tree<T>> getChildren() {
        return children;
    }

    public void setChildren(List<Tree<T>> children) {
        this.children = children;
    }

    public Tree<T> addChildTree(Tree<T> childTree) {
        children.add(childTree);
        return childTree.setParent(this);
    }

    public Tree<T> removeChildTree(Tree<T> childTree) {
        children.remove(childTree);
        return this;
    }

    @Transient
    public String getPath() {
        List<T> parts = new ArrayList<>(Arrays.asList(this.getElement()));
        for (Tree<T> root = this.getParent(); root != null; root = root.getParent()) {
            parts.add(root.getElement());
        }
        return Joiner.on(".").join(Lists.reverse(parts));
    }

    public Integer calculateDepth() {
        Integer depth = 0;
        for (Tree root = this.getParent(); root != null; root = root.getParent()) {
            depth++;
        }
        return depth;
    }

    @Override
    public String toString() {
        return getPath();
    }

    public String prettyPrint() {
        return prettyPrint(this, "", true).trim();
    }

    private String prettyPrint(Tree<T> tree, String prefix, boolean isTail) {
        StringBuilder stringBuilder = new StringBuilder(prefix).append((isTail ? "└── " : "├── ")).append(tree.element).append("\n");
        if (!Hibernate.isInitialized(tree.children)) return stringBuilder.toString();

        for (Iterator<Tree<T>> iterator = tree.children.iterator(); iterator.hasNext(); ) {
            stringBuilder.append(prettyPrint(iterator.next(), prefix + (isTail ? "    " : "│   "), !iterator.hasNext()));
        }
        return stringBuilder.toString();
    }

    /*
     * NOTE: this will find the first leftmost element with the specified names if there are multiple elements with the same name.
     */
    public Tree<T> findTree(String elementName) {
        return findTree(this, elementName);
    }

    Tree<T> findTree(Tree<T> currentTree, String elementName) {
        if (currentTree.element.getName().equals(elementName)) {
            return currentTree;
        }
        for (Tree<T> child : currentTree.children) {
            Tree<T> tree = findTree(child, elementName);
            if (tree != null) {
                return tree;
            }
        }
        return null;
    }

    public void move(final Tree<T> newParent) {
        if (newParent == null) throw new AssertionError("newParent can not be null");

        this.parent.children.remove(this);
        this.parent = newParent;
        newParent.children.add(this);
    }
}
