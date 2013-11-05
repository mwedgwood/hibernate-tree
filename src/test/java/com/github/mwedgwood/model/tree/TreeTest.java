package com.github.mwedgwood.model.tree;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class TreeTest {


    @Test
    public void testAddChildTree() throws Exception {
        Tree<TreeElement> tree = createStubTestTree();
        Tree child3 = tree.addChildTree(new TestTree("child3", tree));

        assertEquals("child3", child3.getElement().getName());
        assertSame(tree, child3.getParent());
        assertEquals(0, child3.getChildren().size());
    }

    @Test
    public void testPrettyPrint() throws Exception {
        String output = "└── root\n" +      //
                "    ├── child1\n" +        //
                "    │   └── child1.1\n" +  //
                "    └── child2\n" +        //
                "        └── child2.1";     //

        assertEquals(output, createStubTestTree().prettyPrint());
    }

    @Test
    public void testGetPath() {
        TestTree root = new TestTree("root", null);
        TestTree child1 = root.addChild("child1");
        TestTree child12 = child1.addChild("child1.1");

        assertEquals("root.child1.child1.1", child12.getPath());
        assertEquals("root.child1", child1.getPath());
        assertEquals("root", root.getPath());
    }

    @Test
    public void testCalculateDepth() throws Exception {
        TestTree root = new TestTree("root", null);
        TestTree child1 = root.addChild("child1");
        TestTree child12 = child1.addChild("child1.1");

        assertEquals(2, child12.calculateDepth().intValue());
        assertEquals(1, child1.calculateDepth().intValue());
        assertEquals(0, root.calculateDepth().intValue());
    }

    @Test
    public void testFindTree() throws Exception {
        Tree<TreeElement> child2 = createStubTestTree().findTree("child2");

        assertEquals("child2", child2.getElement().getName());
        assertEquals("root", child2.getParent().getElement().getName());
    }


    private Tree<TreeElement> createStubTestTree() {
        TestTree root = new TestTree("root", null);
        root.addChild("child1").addChild("child1.1");
        root.addChild("child2").addChild("child2.1");
        return root;
    }

    private static class TestTree extends Tree<TreeElement> {
        private TestTree(String elementName, Tree<TreeElement> parent) {
            super(new TreeElement(elementName, "some name") {
            }, parent);
        }

        TestTree addChild(String name) {
            return (TestTree) addChildTree(new TestTree(name, this));
        }

    }

}
