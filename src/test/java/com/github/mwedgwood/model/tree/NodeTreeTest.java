package com.github.mwedgwood.model.tree;

import com.github.mwedgwood.repository.NodeTreeRepository;
import com.github.mwedgwood.service.PersistenceService;
import com.github.mwedgwood.service.TestPersistenceServiceImpl;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NodeTreeTest {

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
    public void testFindById() throws Exception {
        Node root = buildSmallTree();

        NodeTree tree = new NodeTreeRepository(session).findByGroupId(root.getGroupId());
        assertNotNull(tree);
        assertFalse(tree.getChildren().isEmpty());
        assertEquals(2, tree.getChildren().size());
        System.out.println(tree.prettyPrint());
    }

    private Node buildSmallTree() {
        Node root = new Node("root", null);
        root.setGroupId(0);
        session.save(root);

        Node child1 = new Node("1", null);
        child1.setParentId(root.getId());
        child1.setOrder(0);
        child1.setGroupId(root.getGroupId());
        session.save(child1);

        Node child11 = new Node("1.1", null);
        child11.setParentId(child1.getId());
        child11.setOrder(0);
        child11.setGroupId(root.getGroupId());
        session.save(child11);

        Node child2 = new Node("2", null);
        child2.setParentId(root.getId());
        child2.setOrder(1);
        child2.setGroupId(root.getGroupId());
        session.save(child2);

        Node child21 = new Node("2.1", null);
        child21.setParentId(child2.getId());
        child21.setOrder(0);
        child21.setGroupId(root.getGroupId());
        session.save(child21);

        session.flush();
        assertNotNull(root.getId());
        return root;
    }

}
