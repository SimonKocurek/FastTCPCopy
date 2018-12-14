package commands;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.out;

public class GetCommand extends Command {

    @Override
    public void execute(PrintWriter out) {
        out.println(fileSize());

        try (ServerSocket listener = new ServerSocket(socket.getLocalPort() + clientNumber)) {
            out.println(listener.getLocalPort());
            out.println("Waiting for client forRequest connect forRequest uploader streams");

            ExecutorService executor = Executors.newFixedThreadPool(threads);

            long offset = 0;
            for (int i = 0; i < threads; i++) {
                Uploader uploader = new Uploader(offset, fileChunks[i], listener.accept());
                executor.submit(uploader);

                offset += fileChunks[i].length;
            }

        } catch (IOException e) {
            out.println("Uploader socket closed. " + e.getMessage());
        }
    }

    private long fileSize() {
        return Arrays.stream(fileChunks)
                .mapToLong(chunk -> chunk.length)
                .sum();
    }

}
