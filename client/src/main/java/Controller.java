import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Controller {

    @FXML
    public TextField filename;

    @FXML
    public TextField connectionUrl;

    @FXML
    public TextField connections;

    @FXML
    public ProgressBar progressbar;

    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    @FXML
    public void downloadClicked(ActionEvent actionEvent) {

        String filename = this.filename.getText();
        String connectionUrl = this.connectionUrl.getText();
        String hostname = connectionUrl.split(":")[0];
        int port = Integer.valueOf(connectionUrl.split(":")[1]);
        int threads = Integer.valueOf(connections.getText());

        progressbar.setProgress(50);

        CopyClient copyClient = new CopyClient(hostname, port, filename, threads);
        executor.submit(copyClient);
    }

    @FXML
    public void pauseClicked(ActionEvent actionEvent) {
        executor.shutdownNow();
        System.out.println("Shutting down client");

        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
