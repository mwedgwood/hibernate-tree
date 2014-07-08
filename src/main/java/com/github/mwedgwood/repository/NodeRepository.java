package com.github.mwedgwood.repository;

import com.github.mwedgwood.model.tree.Node;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class NodeRepository extends AbstractRepository<Node> {

    private final Session session;

    public NodeRepository(Session session) {
        this.session = session;
    }

    @Override
    protected Session getCurrentSession() {
        return session;
    }

    @Override
    protected Class<Node> getPersistentClass() {
        return Node.class;
    }

    @SuppressWarnings("unchecked")
    public List<Node> findByGroupId(Integer groupId) {
        return createCriteria(Restrictions.eq("groupId", groupId))
                .addOrder(Order.asc("parentId"))
                .addOrder(Order.asc("order"))
                .list();
    }
}
