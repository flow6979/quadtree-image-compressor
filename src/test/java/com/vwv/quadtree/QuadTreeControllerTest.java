package com.vwv.quadtree;

import com.vwv.quadtree.controller.QuadTreeController;
import com.vwv.quadtree.model.QuadTreeNode;
import com.vwv.quadtree.service.QuadTreeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class QuadTreeControllerTest {

    @InjectMocks
    private QuadTreeController quadTreeController;

    @Mock
    private QuadTreeService quadTreeService;

    private final String filePath = "src/main/resources/static/output/";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCompress_Success() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile(
                "image",
                "test-image.png",
                "image/png",
                "test image content".getBytes()
        );
        QuadTreeNode mockNode = new QuadTreeNode(1);
        when(quadTreeService.compress(any(File.class))).thenReturn(mockNode);

        QuadTreeNode result = quadTreeController.compress(mockFile);

        assertNotNull(result);
        assertEquals(mockNode, result);
        verify(quadTreeService, times(1)).compress(any(File.class));
    }

    @Test
    void testCompress_IOException() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile(
                "image",
                "test-image.png",
                "image/png",
                "test image content".getBytes()
        );
        when(quadTreeService.compress(any(File.class))).thenThrow(new IOException("Compression failed"));

        assertThrows(IOException.class, () -> quadTreeController.compress(mockFile));
        verify(quadTreeService, times(1)).compress(any(File.class));
    }

    @Test
    void testDecompress_Success() throws IOException {
        QuadTreeNode root = new QuadTreeNode();
        int width = 100;
        int height = 100;
        File mockFile = new File(filePath + "decompressed-test.png");
        when(quadTreeService.decompress(eq(root), eq(width), eq(height), anyString())).thenReturn(mockFile);

        ResponseEntity<FileSystemResource> response = quadTreeController.decompress(root, width, height);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof FileSystemResource);
        assertTrue(response.getHeaders().containsKey("Content-Disposition"));
        assertTrue(response.getHeaders().get("Content-Disposition").get(0).contains("attachment"));
        verify(quadTreeService, times(1)).decompress(eq(root), eq(width), eq(height), anyString());
    }

    @Test
    void testDecompress_IOException() throws IOException {
        QuadTreeNode root = new QuadTreeNode();
        int width = 100;
        int height = 100;
        when(quadTreeService.decompress(eq(root), eq(width), eq(height), anyString()))
                .thenThrow(new IOException("Decompression failed"));

        assertThrows(IOException.class, () -> quadTreeController.decompress(root, width, height));
        verify(quadTreeService, times(1)).decompress(eq(root), eq(width), eq(height), anyString());
    }

    @Test
    void testDecompress_InvalidInput() throws IOException {
        QuadTreeNode root = null; // Invalid input
        int width = -1;   // Invalid width
        int height = 100;

        // No need to mock decompress since it won't be called due to validation

        ResponseEntity<FileSystemResource> response = quadTreeController.decompress(root, width, height);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(quadTreeService, never()).decompress(any(), anyInt(), anyInt(), anyString());
    }
}