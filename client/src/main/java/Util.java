public class Util {

    public static String baseNameFromFilename(String filename) {
        String[] filePath = filename.split("/");
        return filePath[filePath.length - 1];
    }

}
