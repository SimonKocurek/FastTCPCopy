package server.commands;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

class GetCommand extends Command {

    @Override
    Map<String, Long> getAdditionalConfig(PrintWriter out, BufferedReader in, byte[][] fileChunks) {
        return new HashMap<>();
    }

}
