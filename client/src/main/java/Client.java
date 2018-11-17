import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application {

    private static final String filename = "C:/Users/SimonKocurek/Downloads/test.txt";
    private static final String serverAddress = "localhost";
    private static final int serverPort = 9090;
    private static final int threads = 5;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) {
//        launch(args);

        new CopyClient(serverAddress, serverPort, filename, threads).start();
    }

}
