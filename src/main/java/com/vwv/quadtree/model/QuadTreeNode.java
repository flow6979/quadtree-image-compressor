package com.vwv.quadtree.model;

public class QuadTreeNode {

    private boolean isLeaf;
    private int value;

    private QuadTreeNode topLeft;
    private QuadTreeNode topRight;
    private QuadTreeNode bottomLeft;
    private QuadTreeNode bottomRight;

    public QuadTreeNode() {
        this.isLeaf = false;
        this.value = 0;
        this.topLeft = null;
        this.topRight = null;
        this.bottomLeft = null;
        this.bottomRight = null;
    }

    public QuadTreeNode(int value) {
        this.isLeaf = true;
        this.value = value;
        this.topLeft = null;
        this.topRight = null;
        this.bottomLeft = null;
        this.bottomRight = null;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public QuadTreeNode getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(QuadTreeNode topLeft) {
        this.topLeft = topLeft;
    }

    public QuadTreeNode getTopRight() {
        return topRight;
    }

    public void setTopRight(QuadTreeNode topRight) {
        this.topRight = topRight;
    }

    public QuadTreeNode getBottomLeft() {
        return bottomLeft;
    }

    public void setBottomLeft(QuadTreeNode bottomLeft) {
        this.bottomLeft = bottomLeft;
    }

    public QuadTreeNode getBottomRight() {
        return bottomRight;
    }

    public void setBottomRight(QuadTreeNode bottomRight) {
        this.bottomRight = bottomRight;
    }
}