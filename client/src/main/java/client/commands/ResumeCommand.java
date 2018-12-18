package client.commands;

import client.Util;

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
        Path path = Paths.get(Util.stateFileFor(filename));
        Files.lines(path)
                .mapToLong(line -> Long.valueOf(line.split("-")[0]))
                .forEach(out::println);
    }

    @Override
    String getName() {
        return "RESUME";
    }

}
