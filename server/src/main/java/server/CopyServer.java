package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

class CopyServer implements Runnable {

    private final int serverPort;

    private int clientNumber = 1;

    CopyServer(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        try (ServerSocket listener = new ServerSocket(serverPort)) {
            while (!Thread.interrupted()) {
                new ClientHandler(listener.accept(), clientNumber++).start();
            }

        } catch (SocketException e) {
            System.out.println("server.Server shutting down");

        } catch (IOException e) {
            System.err.println("server.Server socket error");
            e.printStackTrace();
        }
    }

}
