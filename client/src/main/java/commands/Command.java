package commands;

import java.io.IOException;
import java.io.PrintWriter;

public abstract class Command {

    final String filename;
    final int threads;

    protected String name;

    Command(String filename, int threads) {
        this.filename = filename;
        this.threads = threads;
    }

    public final void execute(PrintWriter out) throws IOException {
        out.println(filename);
        out.println(threads);
        out.println(name);
        sendAdditionalData(out);

        System.out.println("Sent " + name + " command");
    }

    abstract void sendAdditionalData(PrintWriter out) throws IOException;

}
