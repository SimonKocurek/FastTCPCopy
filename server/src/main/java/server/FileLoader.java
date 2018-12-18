package server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

class FileLoader {

    private File file;
    private int threads;

    byte[][] loadFile(String filename, int threads) throws IOException {
        file = new File(filename);
        this.threads = threads;
        byte[] fileContent = getFileBytes();
        return splitFileIntoChunks(fileContent);
    }

    private byte[] getFileBytes() throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        System.out.println("File " + file + " read with " + fileContent.length + "B");
        return fileContent;
    }

    private byte[][] splitFileIntoChunks(byte[] fileContent) {
        byte[][] chunks = new byte[threads][];

        int chunkSize = fileContent.length / threads;

        for (int i = 0; i < threads; i++) {
            int end = i + 1 < threads ? i * chunkSize + chunkSize : fileContent.length;
            chunks[i] = Arrays.copyOfRange(fileContent, i * chunkSize, end);
        }

        System.out.println("File split into " + threads + " chunks");
        return chunks;
    }

}
