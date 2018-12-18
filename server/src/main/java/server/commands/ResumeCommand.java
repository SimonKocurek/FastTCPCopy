package server.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

class ResumeCommand extends Command {

    @Override
    Map<String, Long> getAdditionalConfig(PrintWriter out, BufferedReader in, byte[][] fileChunks) {
        Map<String, Long> result = new HashMap<>();

        long downloaded = 0;
        for (int i = 0; i < fileChunks.length; i++) {
            try {
                Long pausedAt = Long.valueOf(in.readLine());
                ChunkData pausedChunk = pausedAtChunk(fileChunks, pausedAt);
                result.put("chunk_" + pausedChunk.getId(), pausedChunk.getPausedAt());
                downloaded += pausedChunk.getPausedAt();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        result.put("downloaded", downloaded);

        return result;
    }

    private ChunkData pausedAtChunk(byte[][] fileChunks, Long pausedAt) {
        long wentThrough = 0;

        for (int i = 0; i < fileChunks.length; i++) {
            if (pausedAt >= wentThrough && pausedAt < wentThrough + fileChunks[i].length) {
                return new ChunkData(i, pausedAt - wentThrough);
            }
            wentThrough += fileChunks[i].length;
        }

        throw new RuntimeException("Paused at a non-recognized file chunk");
    }

}
