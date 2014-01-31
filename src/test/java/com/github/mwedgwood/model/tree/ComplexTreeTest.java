package com.github.mwedgwood.model.tree;

import com.github.mwedgwood.repository.AbstractRepository;
import com.github.mwedgwood.service.PersistenceService;
import com.github.mwedgwood.service.TestPersistenceServiceImpl;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ComplexTreeTest {

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
        ComplexTree root = Tree.createRoot(new ComplexTreeElement("root", null), ComplexTree.class);
        root.addChildTree(new ComplexTreeElement("first child", "first child"));
        session.save(root);

        session.flush();

        ComplexTree treeFromDb = (ComplexTree) session.get(ComplexTree.class, root.getId());

        assertEquals(1, treeFromDb.getChildren().size());
    }

    @Test
    public void testListWithMixedType() throws Exception {
        ComplexTree complexTree = Tree.createRoot(new ComplexTreeElement("complex tree root", null), ComplexTree.class);
        SimpleTree simpleTree = Tree.createRoot(new TreeElement("simple tree root", null), SimpleTree.class);

        session.save(complexTree);
        session.save(simpleTree);

        session.flush();

        List<Tree> results = createTreeRepository().findAll()
                .orderBy(Order.asc("id"))
                .list();

        assertFalse(results.isEmpty());
        assertTrue(results.get(0) instanceof ComplexTree);
        assertTrue(results.get(1) instanceof SimpleTree);
    }

    private AbstractRepository<Tree> createTreeRepository() {
        return new AbstractRepository<Tree>() {
            @Override
            protected Session getCurrentSession() {
                return session;
            }

            @Override
            protected Class<Tree> getPersistentClass() {
                return Tree.class;
            }
        };
    }
}
