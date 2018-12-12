public class Server {

    private static int port = 9090;

    public static void main(String[] args) {
        new CopyServer(Server.port).run();
    }

}
