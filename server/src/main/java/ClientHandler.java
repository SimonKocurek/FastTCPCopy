import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
