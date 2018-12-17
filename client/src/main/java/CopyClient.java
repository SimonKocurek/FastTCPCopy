import commands.Command;
import commands.GetCommand;
import commands.ResumeCommand;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class CopyClient implements Runnable {

    private final String serverAddress;
    private final int serverPort;
    private final String filename;
    private final int threads;
    private final ProgressWatcher progressWatcher;

    private RandomAccessFile file;
    private CountDownLatch downloadingThreads;

    private PrintWriter out;
    private BufferedReader in;

    private ExecutorService executor;

    CopyClient(String serverAddress, int serverPort, String filename, int threads, ProgressWatcher progressWatcher) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.filename = filename;
        this.threads = threads;
        this.progressWatcher = progressWatcher;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            System.out.println("Connected to server " + socket);
            handleConnection(socket);

        } catch (UnknownHostException e) {
            System.err.println("Invalid server address");
            e.printStackTrace();

        } catch (IOException e) {
            System.err.println("Failed connecting to server");
            e.printStackTrace();
        }
    }

    private void handleConnection(Socket socket) throws IOException {
        initializeStreams(socket);
        sendCommand();
        initializeLocalData();
        connectToUploaders();
        finalizeDownload();
    }

    private void initializeStreams(Socket socket) throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void sendCommand() {
        try {
            getDownloadCommand().execute(out);

        } catch (IOException e) {
            System.err.println("Sending command failed");
            e.printStackTrace();
        }
    }

    private Command getDownloadCommand() {
        File stateFile = new File(Util.basenameFromFilename(filename + ".download"));
        if (stateFile.exists()) {
            return new ResumeCommand(filename, threads);
        } else {
            return new GetCommand(filename, threads);
        }
    }

    private void initializeLocalData() {
        try {
            long fileSize = Long.parseLong(in.readLine());
            long downloaded = Long.parseLong(in.readLine());

            file = new RandomAccessFile(Util.basenameFromFilename(filename), "rw");
            file.setLength(fileSize);

            progressWatcher.setEnd(fileSize);
            progressWatcher.add(downloaded);

        } catch (IOException e) {
            System.err.println("Failed creating downloaded file");
            e.printStackTrace();
        }
    }

    private void connectToUploaders() {
        try {
            int uploaderPort = Integer.parseInt(in.readLine());
            downloadingThreads = new CountDownLatch(threads);

            executor = Executors.newFixedThreadPool(threads);

            for (int i = 0; i < threads; i++) {
                Downloader downloader = new Downloader(i, serverAddress, uploaderPort,
                        Util.basenameFromFilename(filename), file, progressWatcher, downloadingThreads);
                executor.submit(downloader);
            }

        } catch (IOException e) {
            System.err.println("Failed connecting to uploader");
            e.printStackTrace();
        }
    }

    private void finalizeDownload() {
        waitToFinishDownloading();
        closeFile();
    }

    private void waitToFinishDownloading() {
        try {
            downloadingThreads.await();
            deleteStateFile();
            System.out.println("Download finished");

        } catch (InterruptedException e) {
            deleteStateFile();
            executor.shutdownNow();

            System.out.println("Paused");
            waitForPause();
        }
    }

    private void deleteStateFile() {
        if (new File(Util.basenameFromFilename(filename + ".download")).delete()) {
            System.out.println("Previous state file deleted");
        }
    }

    private void waitForPause() {
        try {
            executor.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closeFile() {
        try {
            file.close();
            System.out.println("File closed successfully");

        } catch (IOException e) {
            System.err.println("Failed closing file");
            e.printStackTrace();
        }
    }

}
