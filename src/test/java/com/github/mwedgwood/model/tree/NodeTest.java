package com.github.mwedgwood.model.tree;

import com.github.mwedgwood.repository.NodeRepository;
import com.github.mwedgwood.service.PersistenceService;
import com.github.mwedgwood.service.TestPersistenceServiceImpl;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NodeTest {
    private static final PersistenceService PERSISTENCE_SERVICE = TestPersistenceServiceImpl.getInstance();

    private Session session;

    @Before
    public void setUp() {
        session = PERSISTENCE_SERVICE.getSessionFactory().openSession();
        session.setFlushMode(FlushMode.MANUAL);
        session.beginTransaction();
    }

    @After
    public void tearDown() {
        Transaction transaction = session.getTransaction();
        try {
            session.flush();
            session.clear();
        } finally {
            if ((transaction != null && transaction.isActive())) {
                transaction.rollback();
            }
            session.close();
        }
    }

    @Test
    public void testSave() throws Exception {
        Node root = new Node("root", null);

        NodeRepository repository = new NodeRepository(session);
        repository.save(root);

        session.flush();
        assertNotNull(root.getId());

        Node fromDb = repository.findById(root.getId());
        assertEquals(root.getId(), fromDb.getId());
        assertEquals(root.getName(), fromDb.getName());
    }

    @Test
    public void testFindByGroup() throws Exception {
        Node root = new Node("root", null);
        root.setGroupId(0);

        NodeRepository repository = new NodeRepository(session);
        repository.save(root);

        Node child1 = new Node("1", null);
        child1.setParentId(root.getId());
        child1.setOrder(0);
        child1.setGroupId(root.getGroupId());
        repository.save(child1);

        Node child2 = new Node("2", null);
        child2.setParentId(root.getId());
        child2.setOrder(1);
        child2.setGroupId(root.getGroupId());
        repository.save(child2);

        session.flush();

        List<Node> nodes = repository.findByGroupId(root.getGroupId());
        assertFalse(nodes.isEmpty());
        assertEquals(nodes.get(0).getName(), "root");
        assertEquals(nodes.get(1).getName(), "1");
        assertEquals(nodes.get(2).getName(), "2");
    }
}
