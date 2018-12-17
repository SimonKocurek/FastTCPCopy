package commands;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class GetCommand extends Command {

    @Override
    Map<String, Long> getAdditionalConfig(PrintWriter out, byte[][] fileChunks) {
        Map<String, Long> result = new HashMap<>();

        return result;

    }

}
