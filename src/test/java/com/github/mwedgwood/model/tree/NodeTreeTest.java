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
        Node root = makeNodes(5, 4, true, session);
        NodeTree tree = new NodeTreeRepository(session).findById(root.getId());
        assertNotNull(tree);
        assertFalse(tree.getChildren().isEmpty());
        assertEquals(4, tree.getChildren().size());
    }

    @Test
    public void testFindByGroupId() throws Exception {
        Node root = makeNodes(5, 4, true, session);
        NodeTree tree = new NodeTreeRepository(session).findByGroupId(root.getGroupId());
        assertNotNull(tree);
        assertFalse(tree.getChildren().isEmpty());
        assertEquals(4, tree.getChildren().size());
        System.out.println(tree.prettyPrint());
    }

    public static Node makeNodes(int maxDepth, int maxWidth, boolean save, Session session) {
        Node root = new Node("root", null);
        root.setGroupId(0);
        if (save) session.save(root);
        makeNodes(maxDepth, maxWidth, root.getId(), 1, save, session);
        if (save) session.flush();
        return root;
    }

    private static void makeNodes(int maxDepth, int maxWidth, int parentId, int parentDepth, boolean save, Session session) {
        for (int i = 0; i < maxWidth && parentDepth <= maxDepth; i++) {
            Node child = new Node(parentDepth + "." + (i + 1), null);
            child.setGroupId(0);
            child.setOrder(i);
            child.setParentId(parentId);
            if (save) session.save(child);
            makeNodes(maxDepth, maxWidth, child.getId(), parentDepth + 1, save, session);
        }
    }

}
