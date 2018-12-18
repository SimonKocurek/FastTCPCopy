package client;

public class Util {

    public static String basenameFromFilename(String filename) {
        String[] filePath = filename.split("/");
        return filePath[filePath.length - 1];
    }

    public static String stateFileFor(String filename) {
        return basenameFromFilename(filename) + ".download";
    }

}
