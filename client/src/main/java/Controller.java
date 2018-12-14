import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import java.io.File;
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

        ProgressWatcher progressWatcher = new ProgressWatcher(progressbar);

        CopyClient copyClient = new CopyClient(hostname, port, filename, threads, progressWatcher);
        executor.submit(copyClient);
    }

    @FXML
    public void pauseClicked(ActionEvent actionEvent) {
        executor.shutdownNow();
        System.out.println("Shutting down client");

        try {
            executor.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Client timeout closing connections");
            e.printStackTrace();
        }
    }

    @FXML
    public void clearClicked(ActionEvent actionEvent) {
        String filename = this.filename.getText();
        String basename = Util.basenameFromFilename(filename);

        if (new File(basename).delete()) {
            System.out.println("Downloaded file deleted");
        }
        if (new File(basename + ".download").delete()) {
            System.out.println("State file deleted");
        }
    }

}
