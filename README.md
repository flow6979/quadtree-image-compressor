# Quadtree Image Compressor

This project implements a quadtree-based image compression tool using a Spring Boot backend and a simple frontend. The application allows users to upload an image, compress it using a quadtree data structure, decompress it, and download the resulting image. The compression process converts the image to a 1-bit (black-and-white) format, which can reduce the file size when saved as a PNG, especially for images with large uniform areas.

### Features
- Upload an image (PNG, JPEG, etc.) via a web interface.
- Convert the image to a 1-bit (black-and-white) format.
- Compress the image using a quadtree data structure.
- Decompress the quadtree to reconstruct the image.
- Display the original and decompressed image sizes.
- Download the decompressed image as a PNG.

### Technologies Used
- **Backend**: Spring Boot (Java)
- **Frontend**: HTML, CSS, JavaScript
- **Dependencies**: Maven, Java ImageIO (for image processing)

## Setup Instructions

### Prerequisites
- Java 11 or higher
- Maven

### Steps to Set Up
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/flow6979/quadtree-image-compressor
   cd quadtree-image-compression
   ```
2. **Build the Project**:
   ```bash
   mvn clean install
   ```
4. **Run the Application:**:
   ```bash
   mvn spring-boot:run
   ```

<img width="1001" alt="image" src="https://github.com/user-attachments/assets/b640eaa7-da17-4a56-a519-5901fbe4547b" />

