package com.github.mwedgwood.model.tree;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.*;


@Entity
@Table(name = "tree")
@DynamicUpdate
@DynamicInsert
@BatchSize(size = 100)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "tree-cache")
public class Tree {

    private Integer id;
    private TreeNode element;
    private Tree parent;
    private List<Tree> children = new LinkedList<>();

    // used by hibernate
    Tree() {
    }

    public static <R extends Tree, S extends TreeNode> R createRoot(S treeElement, Class<R> type) {
        R root;
        try {
            root = type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        root.setElement(treeElement);
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "element_id")
    public TreeNode getElement() {
        return element;
    }

    void setElement(TreeNode element) {
        this.element = element;
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
    @BatchSize(size = 100)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "tree-cache")
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

    public <R extends Tree> R addChildTree(TreeNode treeNode) {
        R newChild;
        try {
            //noinspection unchecked
            newChild = (R) this.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        newChild.setElement(treeNode);

        this.addChildTree(newChild);
        return newChild;
    }

    public Tree removeChildTree(Tree childTree) {
        children.remove(childTree);
        return this;
    }

    @Transient
    public String getPath() {
        List<TreeNode> parts = new ArrayList<>(Arrays.asList(this.getElement()));
        for (Tree root = this.getParent(); root != null; root = root.getParent()) {
            parts.add(root.getElement());
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
        StringBuilder stringBuilder = new StringBuilder(prefix).append((isTail ? "└── " : "├── ")).append(tree.element).append("\n");
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

    Tree findTree(Tree currentTree, String elementName) {
        if (currentTree.element.getName().equals(elementName)) {
            return currentTree;
        }
        for (Tree child : currentTree.children) {
            Tree tree = findTree(child, elementName);
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

    public List<Tree> toList() {
        return toList(this, new ArrayList<Tree>());
    }

    List<Tree> toList(Tree tree, List<Tree> allNodes) {
        allNodes.add(tree);
        for (Tree child : tree.getChildren()) {
            toList(child, allNodes);
        }
        return allNodes;
    }
}
