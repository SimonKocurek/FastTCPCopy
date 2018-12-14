import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler extends Thread {

    private final Socket socket;
    private final int clientNumber;

    private BufferedReader in;
    private PrintWriter out;

    private int threads;
    private byte[][] fileChunks;

    ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;

        System.out.println("New connection " + socket + " for client " + clientNumber);
    }

    @Override
    public void run() {
        try {
            initializeStreams();
            loadFileChunks();
            executeClientCommand();
            closeSocket();

        } catch (IOException e) {
            System.err.println("Connection for client " + clientNumber + " failed");
            e.printStackTrace();
        }
    }

    private void loadFileChunks() {
        try {
            String filename = in.readLine();
            threads = Integer.valueOf(in.readLine());
            fileChunks = new FileLoader().loadFile(filename, threads);
        } catch (IOException e) {
            System.err.println("Reading file failed");
            e.printStackTrace();
        }
    }

    private void initializeStreams() throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    private void executeClientCommand() {
        try {
            String command = in.readLine();

            switch (command) {
                case "GET":
                    handleGet();
                    break;
                case "RESUME":
                    handleResume();
                    break;
                default:
                    throw new RuntimeException("Got invalid command from client. " +
                            "Expected GET|RESUME, but got " + command);
            }

        } catch (IOException e) {
            System.err.println("Executing command failed for client " + clientNumber + ". " + e.getMessage());
        }
    }

    private void handleGet() {
        out.println(fileSize());

        try (ServerSocket listener = new ServerSocket(socket.getLocalPort() + clientNumber)) {
            out.println(listener.getLocalPort());
            System.out.println("Waiting for client to connect to uploader streams");

            ExecutorService executor = Executors.newFixedThreadPool(threads);

            long offset = 0;
            for (int i = 0; i < threads; i++) {
                Uploader uploader = new Uploader(offset, fileChunks[i], listener.accept());
                executor.submit(uploader);

                offset += fileChunks[i].length;
            }

        } catch (IOException e) {
            System.out.println("Uploader socket closed. " + e.getMessage());
        }
    }

    private void handleResume() {

    }

    private void closeSocket() {
        try {
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {
            System.err.println("Failed closing socket for client" + clientNumber);
            e.printStackTrace();
        }
    }

    private long fileSize() {
        return Arrays.stream(fileChunks)
                .mapToLong(chunk -> chunk.length)
                .sum();
    }

}
