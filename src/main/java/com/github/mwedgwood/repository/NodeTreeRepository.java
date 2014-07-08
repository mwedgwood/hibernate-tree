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

    @Override
    public NodeTree findById(Integer id) {
        return null;
    }

    public NodeTree findByGroupId(Integer groupId) {
        return NodeTree.fromList(nodeRepository.findByGroupId(groupId));
    }

    @Override
    public RepositoryResult findAll() {
        return null;
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
