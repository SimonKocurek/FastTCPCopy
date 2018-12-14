class Util {

    static String basenameFromFilename(String filename) {
        String[] filePath = filename.split("/");
        return filePath[filePath.length - 1];
    }

}
