package com.satisfactory.production.binaryTree;

public class BinaryTree {
    private Node root;

    public BinaryTree(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public void parcoursPostfixe(Node node) {
        if (node.getLeft() != null) {
            parcoursPostfixe(node.getLeft());
        }else if (node.getRecipe().getId_composant1() != 0){
            System.out.println(node.getRecipe().getId_composant1());
        }
        if (node.getRight() != null) {
            parcoursPostfixe(node.getRight());
        }else if (node.getRecipe().getId_composant2() != 0) {
            System.out.println(node.getRecipe().getId_composant2());
        }
        System.out.println(node.getRecipe().getId_article());
    }


}
