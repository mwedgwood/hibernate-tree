package com.github.mwedgwood.model.tree;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TreeTest {

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
        TestTree root = Tree.createRoot(new TreeElement("root", null), TestTree.class);
        TestTree child1 = root.addChild("child1");
        TestTree child12 = child1.addChild("child1.1");

        assertEquals("root.child1.child1.1", child12.getPath());
        assertEquals("root.child1", child1.getPath());
        assertEquals("root", root.getPath());
    }

    @Test
    public void testCalculateDepth() throws Exception {
        TestTree root = Tree.createRoot(new TreeElement("root", null), TestTree.class);
        TestTree child1 = root.addChild("child1");
        TestTree child12 = child1.addChild("child1.1");

        assertEquals(2, child12.getDepth().intValue());
        assertEquals(1, child1.getDepth().intValue());
        assertEquals(0, root.getDepth().intValue());
    }

    @Test
    public void testFindTree() throws Exception {
        Tree<TreeElement> child2 = createStubTestTree().findTree("child2");

        assertEquals("child2", child2.getElement().getName());
        assertEquals("root", child2.getParent().getElement().getName());
    }


    private Tree<TreeElement> createStubTestTree() {
        TestTree root = Tree.createRoot(new TreeElement("root", null), TestTree.class);
        root.addChild("child1").addChild("child1.1");
        root.addChild("child2").addChild("child2.1");
        return root;
    }

    private static class TestTree extends Tree<TreeElement> {

        TestTree() {
        }

        private TestTree addChild(String name) {
            return addChildTree(new TreeElement(name, null));
        }
    }

}
