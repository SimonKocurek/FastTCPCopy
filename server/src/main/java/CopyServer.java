import java.io.IOException;
import java.net.ServerSocket;

class CopyServer {

    private final int serverPort;

    private int clientNumber = 1;

    CopyServer(int serverPort) {
        this.serverPort = serverPort;

        System.out.println("Starting server at " + serverPort);
    }

    void start() {
        try (ServerSocket listener = new ServerSocket(serverPort)) {
            while (true) {
                new ClientHandler(listener.accept(), clientNumber++).start();
            }

        } catch (IOException e) {
            System.err.println("Server socket error");
            e.printStackTrace();
        }
    }

}
