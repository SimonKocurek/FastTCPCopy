package commands;

import java.io.IOException;

public abstract class Command {

    private void executeClientCommand() {
        try {
            String command = in.readLine();

            switch (command) {
                case "GET":
                    handleGet();
                    break;
                case "RESUME":
                    handleResume();
                    break;
                default:
                    throw new RuntimeException("Got invalid command from client. " +
                            "Expected GET|RESUME, but got " + command);
            }

        } catch (IOException e) {
            System.err.println("Executing command failed for client " + clientNumber + ". " + e.getMessage());
        }
    }

}
