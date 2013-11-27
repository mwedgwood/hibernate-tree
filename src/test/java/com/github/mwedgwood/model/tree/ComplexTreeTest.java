package com.github.mwedgwood.model.tree;

import com.github.mwedgwood.service.PersistenceService;
import com.github.mwedgwood.service.TestPersistenceServiceImpl;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
        session.getTransaction().commit();
        session.close();
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

}
