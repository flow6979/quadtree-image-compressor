package com.vwv.quadtree.service;

import com.vwv.quadtree.model.QuadTreeNode;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class QuadTreeService {

    public QuadTreeNode buildQuadTree(int[][] image, int x, int y, int width, int height) {
        // Base case
        if (width == 1 && height == 1) {
            return new QuadTreeNode(image[x][y]);
        }

        // Checking uniformity of the region
        int firstValue = image[x][y];
        boolean isUniform = true;
        for (int i = x; i < x + height && isUniform; i++) {
            for (int j = y; j < y + width; j++) {
                if (image[i][j] != firstValue) {
                    isUniform = false;
                    break;
                }
            }
        }

        if (isUniform) {
            return new QuadTreeNode(firstValue);
        }

        // Divide the region into four quadrants
        int halfWidth = (width + 1) / 2;  // Ceiling division for odd width & height
        int halfHeight = (height + 1) / 2;

        QuadTreeNode topLeft = buildQuadTree(image, x, y, halfWidth, halfHeight);
        QuadTreeNode topRight = (halfWidth < width) ? buildQuadTree(image, x, y + halfWidth, width - halfWidth, halfHeight) : null;
        QuadTreeNode bottomLeft = (halfHeight < height) ? buildQuadTree(image, x + halfHeight, y, halfWidth, height - halfHeight) : null;
        QuadTreeNode bottomRight = (halfWidth < width && halfHeight < height) ? buildQuadTree(image, x + halfHeight, y + halfWidth, width - halfWidth, height - halfHeight) : null;

        QuadTreeNode node = new QuadTreeNode();
        node.setLeaf(false);
        node.setTopLeft(topLeft);
        node.setTopRight(topRight);
        node.setBottomLeft(bottomLeft);
        node.setBottomRight(bottomRight);
        return node;
    }

    public void quadTreeToImage(QuadTreeNode node, int[][] image, int x, int y, int width, int height) {
        if (node.isLeaf()) {
            for (int i = x; i < x + height; i++) {
                for (int j = y; j < y + width; j++) {
                    image[i][j] = node.getValue();
                }
            }
        } else {
            int halfWidth = (width + 1) / 2;
            int halfHeight = (height + 1) / 2;

            quadTreeToImage(node.getTopLeft(), image, x, y, halfWidth, halfHeight);
            if (node.getTopRight() != null) {
                quadTreeToImage(node.getTopRight(), image, x, y + halfWidth, width - halfWidth, halfHeight);
            }
            if (node.getBottomLeft() != null) {
                quadTreeToImage(node.getBottomLeft(), image, x + halfHeight, y, halfWidth, height - halfHeight);
            }
            if (node.getBottomRight() != null) {
                quadTreeToImage(node.getBottomRight(), image, x + halfHeight, y + halfWidth, width - halfWidth, height - halfHeight);
            }
        }
    }

    // Convert image file to binary grid (no resizing)
    public int[][] imageToBinaryGrid(File imageFile) throws IOException {
        BufferedImage image = ImageIO.read(imageFile);
        int width = image.getWidth();
        int height = image.getHeight();

        int[][] binaryGrid = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                int rgb = image.getRGB(j, i);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                int gray = (red + green + blue) / 3;

                binaryGrid[i][j] = gray < 128 ? 0 : 1;
            }
        }
        return binaryGrid;
    }

    public File binaryGridToImage(int[][] binaryGrid, String outputPath) throws IOException {
        int height = binaryGrid.length;
        int width = binaryGrid[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int value = binaryGrid[i][j] == 1 ? 0xFFFFFFFF : 0xFF000000; // White or black
                image.setRGB(j, i, value);
            }
        }
        File outputFile = new File(outputPath);
        ImageIO.write(image, "png", outputFile);
        return outputFile;
    }

    public QuadTreeNode compress(File imageFile) throws IOException {
        int[][] binaryGrid = imageToBinaryGrid(imageFile);
        return buildQuadTree(binaryGrid, 0, 0, binaryGrid[0].length, binaryGrid.length);
    }

    public File decompress(QuadTreeNode root, int width, int height, String outputPath) throws IOException {
        int[][] binaryGrid = new int[height][width];
        quadTreeToImage(root, binaryGrid, 0, 0, width, height);
        return binaryGridToImage(binaryGrid, outputPath);
    }

}