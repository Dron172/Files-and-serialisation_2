import javax.imageio.IIOException;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {

    public static StringBuilder tempLog = new StringBuilder();
    public static DateTimeFormatter Formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy ");
    public static LocalDateTime logWriteTime = LocalDateTime.now();

    public static void main(String[] args) {
        /*Создание каталогов файлов*/
        File src = dirCreator("src", "C:\\Games");
        File res = dirCreator("res", "C:\\Games");
        File savegames = dirCreator("savegames", "C:\\Games");
        File temp = dirCreator("temp", "C:\\Games");
        File mainDirectory = dirCreator("main", "C:\\Games\\src");
        File test = dirCreator("test", "C:\\Games\\src");
        File drawables = dirCreator("drawables", "C:\\Games\\res");
        File vectors = dirCreator("vectors", "C:\\Games\\res");
        File icons = dirCreator("icons", "C:\\Games\\res");

        /*Создание файлов*/
        File mainFile = fileCreator(mainDirectory, "Main.java");//Создание файлов
        File utils = fileCreator(mainDirectory, "Utils.java");
        File log = fileCreator(temp, "temp.txt");

        /*Логирование*/
        try (FileWriter logWriter = new FileWriter(log, true)) {
            logWriter.write(tempLog.toString());
            logWriter.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        GameProgress saveFirst = new GameProgress(85, 1, 6, 13.88);
        GameProgress saveSecond = new GameProgress(41, 11, 18, 2.25);
        GameProgress saveThird = new GameProgress(22, 3, 7, 10.25);

        saveGame("C:\\Games\\savegames\\saveFirst.dat", saveFirst);
        saveGame("C:\\Games\\savegames\\saveSecond.dat", saveSecond);
        saveGame("C:\\Games\\savegames\\saveThird.dat", saveThird);

        zipFiles("C:\\Games\\savegames\\zip_save.zip", savegames.listFiles());

        File[] files = savegames.listFiles((File pathname) -> pathname.getName().endsWith(".dat"));
        for(File file:files){
            file.delete();
        }

        openZip("C:\\Games\\savegames\\zip_save.zip", "C:\\Games\\savegames\\");

        GameProgress openProgress=openProgress("C:\\Games\\savegames\\saveSecond.dat");
        System.out.println(openProgress);




    }


    static File dirCreator(String dirName, String pathName) {
        File newDir = new File(pathName + "\\" + dirName);
        if (newDir.mkdir()) {
            tempLog.append(logWriteTime.format(Formatter) + " Каталог " + newDir.getName() + " создан" + "\n");
        }
        return newDir;

    }

    static File fileCreator(File dirPath, String fileName) {
        File newFile = new File(dirPath, fileName);
        try {
            if (newFile.createNewFile()) {
                tempLog.append(logWriteTime.format(Formatter) + " Файл " + newFile.getName() + " создан" + "\n");
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return newFile;
    }

    static void saveGame(String pathName, GameProgress save) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(pathName)) {
            ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
            oos.writeObject(save);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    static void zipFiles(String pathName, File[] files) {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(pathName))) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file.getPath())) {
                    ZipEntry entry = new ZipEntry(file.getName());
                    zout.putNextEntry(entry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zout.write(buffer);
                    zout.closeEntry();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    static void openZip(String zipPath, String openZipPath){
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipPath))) {
            ZipEntry entry;
            String name;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                name = entry.getName();
                FileOutputStream fileOutputStream = new FileOutputStream(openZipPath+ "\\"+ name);
                for (int c = zipInputStream.read(); c != -1; c = zipInputStream.read()) {
                    fileOutputStream.write(c);
                }
                fileOutputStream.flush();
                zipInputStream.closeEntry();
                fileOutputStream.close();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    static GameProgress openProgress(String save){
        GameProgress openProgress = null;
        try (FileInputStream fileInputStream = new FileInputStream(save);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            openProgress = (GameProgress) objectInputStream.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return openProgress;
    }


}