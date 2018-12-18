package client.commands;

import java.io.IOException;
import java.io.PrintWriter;

public abstract class Command {

    final String filename;
    final int threads;

    Command(String filename, int threads) {
        this.filename = filename;
        this.threads = threads;
    }

    public final void execute(PrintWriter out) throws IOException {
        out.println(filename);
        out.println(threads);
        out.println(getName());
        sendAdditionalData(out);
        out.flush();

        System.out.println("Sent " + getName() + " command");
    }

    abstract void sendAdditionalData(PrintWriter out) throws IOException;

    abstract String getName();

}
