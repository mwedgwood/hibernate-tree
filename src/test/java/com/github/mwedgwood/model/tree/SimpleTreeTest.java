package com.github.mwedgwood.model.tree;

import com.github.mwedgwood.repository.SimpleTreeRepository;
import com.github.mwedgwood.service.PersistenceService;
import com.github.mwedgwood.service.TestPersistenceServiceImpl;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleTreeTest {

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
        SimpleTreeRepository repository = new SimpleTreeRepository(session);

        SimpleTree root = Tree.createRoot(new TreeElement("root", "root of tree"), SimpleTree.class);
        root.addChildTree(new TreeElement("first child", "first child"));
        repository.save(root);

        session.flush();

        SimpleTree treeFromDb = repository.findById(root.getId());

        assertEquals(1, treeFromDb.getChildren().size());
    }
}
