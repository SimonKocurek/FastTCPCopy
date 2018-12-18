package server.commands;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class Command {

    public static Command forRequest(String name) {
        System.out.println("Got command " + name);
        switch (name) {
            case "GET": return new GetCommand();
            case "RESUME": return new ResumeCommand();
            default:
                throw new RuntimeException("Got invalid command name. " +
                        "Expected GET|RESUME, but got " + name);
        }
    }

    public Map<String, Long> getConfiguration(PrintWriter out, BufferedReader in, byte[][] fileChunks) {
        Map<String, Long> result = new HashMap<>();

        result.put("fileSize", fileSize(fileChunks));
        result.put("threads", (long) fileChunks.length);
        getAdditionalConfig(out, in, fileChunks).forEach(result::put);

        return result;
    }

    private long fileSize(byte[][] fileChunks) {
        return Arrays.stream(fileChunks)
                .mapToLong(chunk -> chunk.length)
                .sum();
    }

    abstract Map<String, Long> getAdditionalConfig(PrintWriter out, BufferedReader in, byte[][] fileChunks);

}
