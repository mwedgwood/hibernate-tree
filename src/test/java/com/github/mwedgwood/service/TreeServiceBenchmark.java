package com.github.mwedgwood.service;

import com.github.mwedgwood.model.tree.*;
import com.github.mwedgwood.repository.AbstractTreeRepository;
import com.github.mwedgwood.repository.NodeRepository;
import com.github.mwedgwood.repository.NodeTreeRepository;
import com.github.mwedgwood.repository.TreeRepository;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Random;

public class TreeServiceBenchmark {

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
    public void testMakeLargeComplexTree() throws Exception {
        long start = System.currentTimeMillis();
        makeComplexTree(10, 5);
        System.out.println("Time to make tree: " + (System.currentTimeMillis() - start) + " ms");
    }

    @Test
    public void testGetEntireTree() throws Exception {
        int maxDepth = 8;

        ComplexTree tree = makeComplexTree(maxDepth, 4);
        session.save(tree);
        session.flush();

        AbstractTreeRepository<ComplexTree> repository = new AbstractTreeRepository<ComplexTree>() {
            @Override
            protected Session getCurrentSession() {
                return session;
            }

            @Override
            protected Class<ComplexTree> getPersistentClass() {
                return ComplexTree.class;
            }
        };

        long start = System.currentTimeMillis();
        ComplexTree treeForDepth = repository.findEntireTree();
        System.out.println("Time to find tree: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        repository.findEntireTree();
        System.out.println("Time to find tree CACHED: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        repository.findEntireTree();
        System.out.println("Time to find tree CACHED TWO: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        treeForDepth.prettyPrint();
        System.out.println("Time to recurse tree: " + (System.currentTimeMillis() - start) + " ms");
    }

    @Test
    public void testGetComplexTreeForDepth() throws Exception {
        int maxDepth = 8;

        ComplexTree tree = makeComplexTree(maxDepth, 4);
        session.save(tree);
        session.flush();

        TreeRepository<ComplexTree> repository = new AbstractTreeRepository<ComplexTree>() {
            @Override
            protected Session getCurrentSession() {
                return session;
            }

            @Override
            protected Class<ComplexTree> getPersistentClass() {
                return ComplexTree.class;
            }
        };

        long start = System.currentTimeMillis();
        ComplexTree treeForDepth = repository.findByIdForDepth(tree.getId(), maxDepth);
        System.out.println("Time to find tree: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        repository.findByIdForDepth(tree.getId(), maxDepth);
        System.out.println("Time to find tree CACHED: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        repository.findByIdForDepth(tree.getId(), maxDepth);
        System.out.println("Time to find tree CACHED TWO: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        treeForDepth.prettyPrint();
        System.out.println("Time to recurse tree: " + (System.currentTimeMillis() - start) + " ms");
    }

    @Test
    public void testGetComplexTreeByRootId() throws Exception {
        int maxDepth = 8;

        ComplexTree tree = makeComplexTree(maxDepth, 4);
        session.save(tree);
        session.flush();

        TreeRepository<ComplexTree> repository = new AbstractTreeRepository<ComplexTree>() {
            @Override
            protected Session getCurrentSession() {
                return session;
            }

            @Override
            protected Class<ComplexTree> getPersistentClass() {
                return ComplexTree.class;
            }
        };

        long start = System.currentTimeMillis();
        ComplexTree treeForDepth = repository.findRoot();
        System.out.println("Time to find tree: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        repository.findRoot();
        System.out.println("Time to find tree CACHED: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        repository.findRoot();
        System.out.println("Time to find tree CACHED TWO: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        treeForDepth.prettyPrint();
        System.out.println("Time to recurse tree: " + (System.currentTimeMillis() - start) + " ms with " + treeForDepth.toList().size() + " nodes");

    }

    @Test
    public void testGetNodeTreeByGroupId() throws Exception {
        int maxDepth = 8;
        Node rootNode = NodeTreeTest.makeNodes(maxDepth, 4, true, session);

        NodeTreeRepository repository = new NodeTreeRepository(session);
        NodeRepository nodeRepository = new NodeRepository(session);

        long start = System.currentTimeMillis();
        List<Node> nodes = nodeRepository.findByGroupId(rootNode.getGroupId());
        System.out.println("Time to find " + nodes.size() + " nodes: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        NodeTree.fromList(nodes);
        System.out.println("Time to build tree: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        NodeTree treeForDepth = repository.findByGroupId(rootNode.getGroupId());
        System.out.println("Time to find tree: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        repository.findByGroupId(rootNode.getGroupId());
        System.out.println("Time to find tree CACHED: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        repository.findByGroupId(rootNode.getGroupId());
        System.out.println("Time to find tree CACHED TWO: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        treeForDepth.prettyPrint();
        System.out.println("Time to recurse tree: " + (System.currentTimeMillis() - start) + " ms with " + treeForDepth.toList().size() + " nodes");

    }

    private ComplexTree makeComplexTree(int depth, int maxWidth) {
        ComplexTree root = Tree.createRoot(new ComplexTreeElement("root", null), ComplexTree.class);
        makeComplexTree(depth, maxWidth, root);
        return root;
    }

    private ComplexTree makeComplexTree(int maxDepth, int maxWidth, ComplexTree tree) {
        Integer depth = tree.getDepth();
        for (int i = 0; i < maxWidth && depth < maxDepth; i++) {
            tree.addChildTree(new ComplexTreeElement(String.valueOf(new Random().nextLong()), null));
            makeComplexTree(maxDepth, maxWidth, (ComplexTree) tree.getChildren().get(i));
        }
        return tree;
    }


}
