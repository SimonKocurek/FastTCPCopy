import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

class CopyClient {

    private final String serverAddress;
    private final int serverPort;
    private final String filename;
    private final int threads;

    private RandomAccessFile file;
    private CountDownLatch downloadingThreads;

    private PrintWriter out;
    private BufferedReader in;
    private long filesize;

    CopyClient(String serverAddress, int serverPort, String filename, int threads) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.filename = filename;
        this.threads = threads;
    }

    void start() {
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            System.out.println("Connected to server " + socket);
            handleConnection(socket);

        } catch (UnknownHostException e) {
            System.err.println("Invalid server address");
            e.printStackTrace();

        } catch (IOException e) {
            System.err.println("Failed connecting to server");
            e.printStackTrace();
        }
    }

    private void handleConnection(Socket socket) throws IOException {
        initializeStreams(socket);
        sendCommand(out, "GET");
        initializeLocalData();
        connectToUploaders();
        finalizeDownload();
    }

    private void finalizeDownload() {
        try {
            downloadingThreads.await();
            file.close();
            System.out.println("File downloaded sucessfully");

        } catch (InterruptedException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initializeStreams(Socket socket) throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void sendCommand(PrintWriter out, String name) {
        out.println(filename);
        out.println(threads);
        out.println(name);
        System.out.println("Sent " + name + " command");
    }

    private void initializeLocalData() {
        try {
            filesize = Long.parseLong(in.readLine());

            String[] filePath = filename.split("/");
            file = new RandomAccessFile(filePath[filePath.length - 1], "rw");
            file.setLength(filesize);

        } catch (IOException e) {
            System.err.println("Failed creating donwloaded file");
            e.printStackTrace();
        }
    }

    private void connectToUploaders() {
        try {
            int uploaderPort = Integer.parseInt(in.readLine());
            downloadingThreads = new CountDownLatch(threads);

            for (int i = 0; i < threads; i++) {
                long chunkSize = filesize / threads;
                long fileStart = i * chunkSize;

                new Downloader(i, serverAddress, uploaderPort, file, fileStart, downloadingThreads).start();
            }

        } catch (IOException e) {
            System.err.println("Failed connecting to uploader");
            e.printStackTrace();
        }
    }

}
