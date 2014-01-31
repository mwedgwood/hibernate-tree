package com.github.mwedgwood.model.tree;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import javax.persistence.*;
import java.util.*;

@NamedQueries({
        @NamedQuery(name = "findRootNode", query = "select id from Tree t where t.class = :aClass and t.parent = null"),
        @NamedQuery(name = "findAllNodesWithTheirChildren", query = "from Tree t left join fetch t.children where t.class = :aClass")
})

@Entity
@Table(name = "tree")
@DynamicUpdate
@DynamicInsert
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Tree {

    private Integer id;
    private String name;
    private Tree parent;
    private List<Tree> children = new LinkedList<>();

    // used by hibernate
    Tree() {
    }

    public static <R extends Tree> R createRoot(String name, Class<R> type) {
        R root;
        try {
            root = type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        root.setName(name);
        return root;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Tree getParent() {
        return parent;
    }

    private Tree setParent(Tree parent) {
        this.parent = parent;
        return this;
    }

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @OrderColumn(name = "children_order")
    public List<Tree> getChildren() {
        return children;
    }

    public void setChildren(List<Tree> children) {
        this.children = children;
    }

    public Tree addChildTree(Tree childTree) {
        children.add(childTree);
        return childTree.setParent(this);
    }

    public <R extends Tree> R addChildTree(String name) {
        R newChild;
        try {
            //noinspection unchecked
            newChild = (R) this.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        newChild.setName(name);

        this.addChildTree(newChild);
        return newChild;
    }

    public Tree removeChildTree(Tree childTree) {
        children.remove(childTree);
        return this;
    }

    @Transient
    public String getPath() {
        List<String> parts = new ArrayList<>(Arrays.asList(name));
        for (Tree root = this.getParent(); root != null; root = root.getParent()) {
            parts.add(root.name);
        }
        return Joiner.on(".").join(Lists.reverse(parts));
    }

    @Transient
    public Integer getDepth() {
        return parent == null ? 0 : (parent.getDepth() + 1);
    }

    @Override
    public String toString() {
        return getPath();
    }

    public String prettyPrint() {
        return prettyPrint(this, "", true).trim();
    }

    private String prettyPrint(Tree tree, String prefix, boolean isTail) {
        StringBuilder stringBuilder = new StringBuilder(prefix).append((isTail ? "└── " : "├── ")).append(tree.name).append("\n");
        if (!Hibernate.isInitialized(tree.children)) return stringBuilder.toString();

        for (Iterator<Tree> iterator = tree.children.iterator(); iterator.hasNext(); ) {
            stringBuilder.append(prettyPrint(iterator.next(), prefix + (isTail ? "    " : "│   "), !iterator.hasNext()));
        }
        return stringBuilder.toString();
    }

    /*
     * NOTE: this will find the first leftmost element with the specified names if there are multiple elements with the same name.
     */
    public Tree findTree(String elementName) {
        return findTree(this, elementName);
    }

    Tree findTree(Tree currentTree, String name) {
        if (currentTree.name.equals(name)) {
            return currentTree;
        }
        for (Tree child : currentTree.children) {
            Tree tree = findTree(child, name);
            if (tree != null) {
                return tree;
            }
        }
        return null;
    }

    public void move(final Tree newParent) {
        if (newParent == null) throw new AssertionError("newParent can not be null");

        this.parent.children.remove(this);
        this.parent = newParent;
        newParent.children.add(this);
    }
}
