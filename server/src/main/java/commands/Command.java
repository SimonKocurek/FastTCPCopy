package commands;

import java.io.PrintWriter;

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

    public abstract void execute(PrintWriter out);

}
