let decompressedImageUrl = null; // Store the image URL for downloading

function formatBytes(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function updateProgress(percentage, stage) {
    const progressBar = document.getElementById('progressBar');
    const progressText = document.getElementById('progressText');
    const stageText = document.getElementById('stageText');
    progressBar.style.width = `${percentage}%`;
    progressText.textContent = `Processing... ${percentage}%`;
    stageText.textContent = stage;
}

async function compressImage() {
    const imageInput = document.getElementById('imageInput');
    const decompressedImage = document.getElementById('decompressedImage');
    const resultSection = document.getElementById('resultSection');
    const compressButton = document.getElementById('compressButton');
    const loadingIndicator = document.getElementById('loadingIndicator');
    const originalSizeElement = document.getElementById('originalSize');
    const decompressedSizeElement = document.getElementById('decompressedSize');

    if (!imageInput.files || imageInput.files.length === 0) {
        alert('Please select an image to compress.');
        return;
    }

    const file = imageInput.files[0];
    const formData = new FormData();
    formData.append('image', file);

    try {
        // Show loading indicator and disable button
        compressButton.disabled = true;
        loadingIndicator.style.display = 'block';
        updateProgress(0, 'Starting...');

        const originalSize = file.size;
        originalSizeElement.textContent = formatBytes(originalSize);

        // Stage 1: Uploading the image (0% to 20%)
        updateProgress(10, 'Uploading');
        const compressResponse = await fetch('/api/compress', {
            method: 'POST',
            body: formData
        });
        updateProgress(20, 'Uploading');

        if (!compressResponse.ok) {
            throw new Error('Compression failed: ' + compressResponse.statusText);
        }

        // Stage 2: Backend compression (20% to 50%)
        updateProgress(30, 'Compression');
        const quadTreeData = await compressResponse.json(); // Either a single quadtree or an array of quadtrees
        updateProgress(50, 'Compression');

        // Stage 3: Get the original image dimensions and send for decompression (50% to 60%)
        updateProgress(55, 'Sending for decompression');
        const img = new Image();
        img.src = URL.createObjectURL(file);
        await new Promise((resolve) => {
            img.onload = resolve;
        });
        const width = img.width;
        const height = img.height;
        updateProgress(60, 'Sending for decompression');

        // Stage 4: Decompress the quadtree(s) (60% to 90%)
        updateProgress(70, 'Decompression');
        const decompressResponse = await fetch(`/api/decompress?width=${width}&height=${height}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(quadTreeData)
        });
        updateProgress(90, 'Decompression');

        if (!decompressResponse.ok) {
            throw new Error('Decompression failed: ' + decompressResponse.statusText);
        }

        const blob = await decompressResponse.blob();
        decompressedImageUrl = URL.createObjectURL(blob);
        decompressedImage.src = decompressedImageUrl;

        // Stage 5: Rendering the decompressed image (90% to 100%)
        updateProgress(95, 'Rendering');
        const decompressedSize = blob.size;
        decompressedSizeElement.textContent = formatBytes(decompressedSize);
        updateProgress(100, 'Rendering');

        // Show the results section with animation
        resultSection.style.display = 'block';
        resultSection.classList.add('show');

    } catch (error) {
        alert('Error: ' + error.message);
        console.error(error);
    } finally {
        // Hide loading indicator and re-enable button
        compressButton.disabled = false;
        loadingIndicator.style.display = 'none';
    }
}

function downloadImage() {
    if (!decompressedImageUrl) {
        alert('No image available to download.');
        return;
    }

    const link = document.createElement('a');
    link.href = decompressedImageUrl;
    link.download = 'decompressed-image.png';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}