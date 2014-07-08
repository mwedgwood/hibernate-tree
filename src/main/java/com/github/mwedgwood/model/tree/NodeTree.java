package com.github.mwedgwood.model.tree;

import org.hibernate.Hibernate;

import java.util.*;

public class NodeTree {

    private Node node;

    private NodeTree parent;
    private List<NodeTree> children = new ArrayList<>();

    public static NodeTree fromList(List<Node> nodes) {
        Map<Integer, NodeTree> parents = new HashMap<>();
        NodeTree root = null;

        for (Node node : nodes) {
            Integer parentId = node.getParentId();
            NodeTree tree = new NodeTree(node);

            if (parentId == null) {
                root = tree;
            }

            NodeTree parent = parents.get(parentId);
            if (parent != null) {
                parent.addChild(tree);
            }

            parents.put(node.getId(), tree);
        }
        return root;
    }

    private NodeTree() {
    }

    private NodeTree(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    NodeTree setNode(Node node) {
        this.node = node;
        return this;
    }

    public NodeTree getParent() {
        return parent;
    }

    NodeTree setParent(NodeTree parent) {
        this.parent = parent;
        return this;
    }

    public List<NodeTree> getChildren() {
        return children;
    }

    NodeTree setChildren(List<NodeTree> children) {
        this.children = children;
        return this;
    }

    NodeTree addChild(NodeTree tree) {
        tree.parent = this;
        this.children.add(tree);

        Integer order = this.children.indexOf(tree);
        tree.getNode().setOrder(order);
        return this;
    }

    public List<NodeTree> toList() {
        return toList(this, new ArrayList<NodeTree>());
    }

    List<NodeTree> toList(NodeTree tree, List<NodeTree> allNodes) {
        allNodes.add(tree);
        for (NodeTree child : tree.getChildren()) {
            toList(child, allNodes);
        }
        return allNodes;
    }

    public String prettyPrint() {
        return prettyPrint(this, "", true).trim();
    }

    private String prettyPrint(NodeTree tree, String prefix, boolean isTail) {
        StringBuilder stringBuilder = new StringBuilder(prefix).append((isTail ? "└── " : "├── ")).append(tree.node).append("\n");
        if (!Hibernate.isInitialized(tree.children)) return stringBuilder.toString();

        for (Iterator<NodeTree> iterator = tree.children.iterator(); iterator.hasNext(); ) {
            stringBuilder.append(prettyPrint(iterator.next(), prefix + (isTail ? "    " : "│   "), !iterator.hasNext()));
        }
        return stringBuilder.toString();
    }

}
