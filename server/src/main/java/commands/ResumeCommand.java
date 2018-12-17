package commands;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

class ResumeCommand extends Command {

    @Override
    Map<String, Long> getAdditionalConfig(PrintWriter out, byte[][] fileChunks) {
        Map<String, Long> result = new HashMap<>();

        return result;
    }

}
