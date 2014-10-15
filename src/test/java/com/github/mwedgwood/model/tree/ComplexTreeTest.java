package com.github.mwedgwood.model.tree;

import com.github.mwedgwood.repository.AbstractTreeRepository;
import com.github.mwedgwood.repository.TreeRepository;
import com.github.mwedgwood.service.PersistenceService;
import com.github.mwedgwood.service.TestPersistenceServiceImpl;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
        Tree root = Tree.createRoot(new ComplexTreeNode("root"), Tree.class);
        root.addChildTree(new ComplexTreeNode("first child"));
        session.save(root);

        session.flush();

        Tree treeFromDb = (Tree) session.get(Tree.class, root.getId());

        assertEquals(1, treeFromDb.getChildren().size());
    }

    @Test
    public void testListWithMixedType() throws Exception {
        Tree complexTree = Tree.createRoot(new ComplexTreeNode("complex tree root"), Tree.class);
        complexTree.addChildTree(new SimpleTreeNode("first child base element"));
        complexTree.addChildTree(new ComplexTreeNode("first Child complex element"));

        session.save(complexTree);
        session.flush();

        Tree root = createTreeRepository().findRoot();

        session.flush();

        assertEquals(ComplexTreeNode.class, root.getElement().getClass());

        List<Tree> children = root.getChildren();
        assertFalse(children.isEmpty());
        assertEquals(2, children.size());
        assertEquals(SimpleTreeNode.class, children.get(0).getElement().getClass());
        assertEquals(ComplexTreeNode.class, children.get(1).getElement().getClass());
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
