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
        Tree root = Tree.createRoot(new ComplexTreeElement("root", null), Tree.class);
        root.addChildTree(new ComplexTreeElement("first child", "first child"));
        session.save(root);

        session.flush();

        Tree treeFromDb = (Tree) session.get(Tree.class, root.getId());

        assertEquals(1, treeFromDb.getChildren().size());
    }

    @Test
    public void testListWithMixedType() throws Exception {
        Tree complexTree = Tree.createRoot(new ComplexTreeElement("complex tree root", null), Tree.class);
        complexTree.addChildTree(new TreeElement("first child base element", "base tree element"));
        complexTree.addChildTree(new ComplexTreeElement("first Child complex element", "complex tree element"));

        session.save(complexTree);
        session.flush();

        Tree root = createTreeRepository().findRoot();

        session.flush();

        assertEquals(ComplexTreeElement.class, root.getElement().getClass());

        List<Tree> children = root.getChildren();
        assertFalse(children.isEmpty());
        assertEquals(2, children.size());
        assertEquals(TreeElement.class, children.get(0).getElement().getClass());
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
