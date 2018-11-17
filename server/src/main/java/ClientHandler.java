import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;

public class ClientHandler extends Thread {

    private final Socket socket;
    private final int clientNumber;

    private BufferedReader in;
    private PrintWriter out;

    private File file;
    private byte[][] chunks;
    private int threads;

    ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;

        System.out.println("New connection " + socket + " for client " + clientNumber);
    }

    @Override
    public void run() {
        try {
            initializeStreams();
            loadFile();
            executeClientCommand();
            closeSocket();

        } catch (IOException e) {
            System.err.println("Connection for client " + clientNumber + " failed");
            e.printStackTrace();
        }
    }

    private void initializeStreams() throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    private void loadFile() {
        try {
            String filename = in.readLine();
            file = new File(filename);

            threads = Integer.valueOf(in.readLine());

            byte[] fileContent = getFileBytes();
            chunks = splitFileIntoChunks(fileContent);

        } catch (IOException e) {
            System.err.println("Reading file failed");
            e.printStackTrace();
        }
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
            System.err.println("Executing command failed for client " + clientNumber);
            e.printStackTrace();
        }
    }

    private void handleGet() {
        out.println(file.length());

        try (ServerSocket listener = new ServerSocket(socket.getLocalPort() + clientNumber)) {
            out.println(listener.getLocalPort());
            System.out.println("Waiting for client to connect to uploader streams");

            for (int i = 0; i < threads; i++) {
                new Uploader(chunks[i], listener.accept()).start();
            }

        } catch (IOException e) {
            System.err.println("main.java.Uploader socket failed");
            e.printStackTrace();
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

}
