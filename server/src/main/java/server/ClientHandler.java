package server;

import server.commands.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class ClientHandler extends Thread {

    private final Socket socket;
    private final int clientNumber;

    private BufferedReader in;
    private PrintWriter out;

    private byte[][] fileChunks;

    ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
    }

    @Override
    public void run() {
        try {
            initializeStreams();
            loadFileChunks();
            handleClient();
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

    private void loadFileChunks() {
        try {
            String filename = in.readLine();
            int threads = Integer.valueOf(in.readLine());
            fileChunks = new FileLoader().loadFile(filename, threads);
        } catch (IOException e) {
            System.err.println("Reading file failed");
            e.printStackTrace();
        }
    }

    private void handleClient() {
        try {
            String request = in.readLine();
            Command command = Command.forRequest(request);
            Map<String, Long> configuration = command.getConfiguration(out, in, fileChunks);
            UploaderServerSocket uploaderSocket = new UploaderServerSocket(
                    configuration, socket, clientNumber, out, fileChunks);
            uploaderSocket.start();

        } catch (IOException e) {
            System.err.println("Executing command failed for client " + clientNumber + ". " + e.getMessage());
        }
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
