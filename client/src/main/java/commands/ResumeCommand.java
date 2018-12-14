package commands;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResumeCommand extends Command {

    public ResumeCommand(String filename, int threads) {
        super(filename, threads);
    }

    @Override
    void sendAdditionalData(PrintWriter out) throws IOException {
        Path path = Paths.get(filename);
        Files.lines(path).forEach(line -> {
            long currentPointer = Long.valueOf(line.split("-")[0]);
            out.println(currentPointer);
        });
    }

}
