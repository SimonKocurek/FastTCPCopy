package commands;

import java.io.PrintWriter;

public class GetCommand extends Command {

    public GetCommand(String filename, int threads) {
        super(filename, threads);
    }

    @Override
    void sendAdditionalData(PrintWriter out) {
    }

}
