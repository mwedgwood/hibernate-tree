package com.github.mwedgwood.repository;

import com.github.mwedgwood.model.tree.Node;
import com.github.mwedgwood.model.tree.NodeTree;
import org.hibernate.Session;

import java.util.List;

public class NodeTreeRepository implements Repository {

    private final Session session;
    private final NodeRepository nodeRepository;

    public NodeTreeRepository(Session session) {
        this.session = session;
        this.nodeRepository = new NodeRepository(session);
    }

    protected Session getCurrentSession() {
        return session;
    }

    protected Class<Node> getPersistentClass() {
        return Node.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NodeTree findById(Integer id) {
        List<Node> nodes = session.createSQLQuery(
                "WITH RECURSIVE children(id, description, name, group_id, parent_id, children_order, depth) AS (\n" +
                        "    SELECT t.id, t.description, t.name, t.group_id, t.parent_id, t.children_order, 0\n" +
                        "    FROM node t\n" +
                        "    WHERE t.id = :id\n" +
                        "  UNION ALL\n" +
                        "    SELECT a.id, a.description, a.name, a.group_id, a.parent_id, a.children_order, depth+1\n" +
                        "    FROM node a\n" +
                        "    JOIN children b ON (a.parent_id = b.id)\n" +
                        ")\n" +
                        " \n" +
                        "SELECT t.id, t.name, t.description, t.parent_id, t.group_id, t.children_order\n" +
                        "FROM children t\n" +
                        "ORDER BY COALESCE(t.parent_id, t.id), t.depth, t.children_order;"
        ).addEntity(Node.class).setInteger("id", id).list();
        return NodeTree.fromList(nodes);
    }

    @Override
    public RepositoryResult findAll() {
        return null;
    }

    public NodeTree findByGroupId(Integer groupId) {
        return NodeTree.fromList(nodeRepository.findByGroupId(groupId));
    }

    @Override
    public RepositoryResult findByExample(Object example) {
        return null;
    }

    @Override
    public void save(Object entity) {

    }

    @Override
    public void delete(Object entity) {

    }

    @Override
    public void update(Object entity) {

    }
}
