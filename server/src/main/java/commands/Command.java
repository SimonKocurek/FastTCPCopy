package commands;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class Command {

    public static Command forRequest(String name) {
        switch (name) {
            case "GET": return new GetCommand();
            case "RESUME": return new ResumeCommand();
            default:
                throw new RuntimeException("Got invalid command name. " +
                        "Expected GET|RESUME, but got " + name);
        }
    }

    public Map<String, Long> getConfiguration(PrintWriter out, byte[][] fileChunks) {
        Map<String, Long> result = new HashMap<>();

        result.put("fileSize", fileSize(fileChunks));
        getAdditionalConfig(out, fileChunks).forEach(result::put);

        return result;
    }

    private long fileSize(byte[][] fileChunks) {
        return Arrays.stream(fileChunks)
                .mapToLong(chunk -> chunk.length)
                .sum();
    }

    abstract Map<String, Long> getAdditionalConfig(PrintWriter out, byte[][] fileChunks);

}
