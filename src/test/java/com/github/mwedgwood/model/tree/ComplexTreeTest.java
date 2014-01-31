package com.github.mwedgwood.model.tree;

import com.github.mwedgwood.repository.AbstractRepository;
import com.github.mwedgwood.repository.AbstractTreeRepository;
import com.github.mwedgwood.repository.TreeRepository;
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
        ComplexTree root = Tree.createRoot("root", ComplexTree.class);
        root.addChildTree("first child");
        session.save(root);

        session.flush();

        ComplexTree treeFromDb = (ComplexTree) session.get(ComplexTree.class, root.getId());

        assertEquals(1, treeFromDb.getChildren().size());
    }

    @Test
    public void testListWithMixedType() throws Exception {
        ComplexTree rootTree = Tree.createRoot("complex root", ComplexTree.class);
        rootTree.addChildTree(new ComplexTree().setName("complex child"));
        rootTree.addChildTree(new SimpleTree().setName("simple child"));

        session.save(rootTree);
        session.flush();

        Tree root = createTreeRepository().findRoot();
        List<Tree> children = root.getChildren();

        session.flush();

        assertFalse(children.isEmpty());
        assertTrue(children.get(0) instanceof ComplexTree);
        assertTrue(children.get(1) instanceof SimpleTree);
    }

    private TreeRepository<Tree> createTreeRepository() {
        return new AbstractTreeRepository<Tree>() {
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
