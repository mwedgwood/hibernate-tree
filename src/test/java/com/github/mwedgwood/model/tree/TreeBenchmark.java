package com.github.mwedgwood.model.tree;

import com.github.mwedgwood.repository.AbstractTreeRepository;
import com.github.mwedgwood.repository.TreeRepository;
import com.github.mwedgwood.service.PersistenceService;
import com.github.mwedgwood.service.TestPersistenceServiceImpl;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.junit.Test;

public class TreeBenchmark {

    private static final PersistenceService PERSISTENCE_SERVICE = TestPersistenceServiceImpl.getInstance();


    @Test
    public void testFindTreeForDepth() throws Exception {
        final Tree testTree = buildAndSaveTree();

        long start = System.currentTimeMillis();
        final Tree tree = findEntireTree(testTree);
        System.out.println("Time to find tree: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        findEntireTree(testTree);
        System.out.println("Time to find tree CACHED: " + (System.currentTimeMillis() - start) + " ms");

        System.out.println("Number of nodes: " + tree.toList().size());
    }

    private Tree buildAndSaveTree() {
        return new TestUnitOfWork<Tree>() {
            Tree unitOfWork(Session session) {
                Tree testTree = makeTree(5, 5);
                createTreeRepository(session).save(testTree);
                return testTree;
            }
        }.execute(false);
    }

    private Tree findEntireTree(final Tree testTree) {
        return new TestUnitOfWork<Tree>() {
            Tree unitOfWork(Session session) {
                //                return createTreeRepository(session).findByIdForDepth(testTree.getId(), 10);
                return createTreeRepository(session).findEntireTree(testTree.getId());
            }
        }.execute(true);
    }

    private Tree makeTree(int depth, int maxWidth) {
        Tree root = Tree.createRoot(new ComplexTreeNode("complex-root"), Tree.class);
        makeTree(depth, maxWidth, root);
        return root;
    }

    private Tree makeTree(int maxDepth, int maxWidth, Tree tree) {
        Integer depth = tree.getDepth();
        for (int i = 0; i < maxWidth && depth < maxDepth; i++) {
            TreeNode treeNode = i % 2 == 0 ?
                    new SimpleTreeNode("simple-" + (depth + 1) + "." + (i + 1)) :
                    new ComplexTreeNode("coplex-" + (depth + 1) + "." + (i + 1));

            tree.addChildTree(treeNode);
            makeTree(maxDepth, maxWidth, tree.getChildren().get(i));
        }
        return tree;
    }

    private static abstract class TestUnitOfWork<T> {

        abstract T unitOfWork(Session session);

        T execute(boolean readOnly) {
            Session session = PERSISTENCE_SERVICE.getSessionFactory().openSession();
            session.setDefaultReadOnly(readOnly);

            T result = null;
            try {
                session.beginTransaction();
                result = unitOfWork(session);
                session.getTransaction().commit();
            } catch (HibernateException e) {
                session.getTransaction().rollback();
                throw e;
            } finally {
                session.close();
            }

            return result;
        }

    }

    private TreeRepository<Tree> createTreeRepository(final Session session) {
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
