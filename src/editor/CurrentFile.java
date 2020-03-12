package editor;

public class CurrentFile {
    private static CurrentFile file;
    private static String fileName;

    private CurrentFile(String file){
        this.fileName = file;
    }

    private CurrentFile(){
    }

    public static CurrentFile getCurrentFile(){
        return file;
    }

    public static void setFileName(String fileName) {
        CurrentFile.fileName = fileName;
    }

    public static String getFileName() {
        return fileName;
    }
}
