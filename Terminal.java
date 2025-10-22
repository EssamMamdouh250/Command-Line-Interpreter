package com.mycompany.terminal;

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;

class Command {
    String name;
    String[] args;

    public Command(String name, String[] args) {
        this.name = name;
        this.args = args;
    }

    public String[] execute() {
        System.out.println("Command not implemented: " + name);
        return null;
    }
}


class Parser {
    private String commandName;
    private String[] args;

    public boolean parse(String s) {
        if (s == null || s.trim().isEmpty())
            return false;

        String[] arr = s.trim().split("\\s+");
        commandName = arr[0];
        args = Arrays.copyOfRange(arr, 1, arr.length);
        return true;
    }

    public String getCommandName() { return commandName; }
    public String[] getArgs() { return args; }
}


public class Terminal {
    Parser p = new Parser();
    boolean redirect = false;
    // Helper function to check if it is a file or no
    private static boolean checkFile(Path path, String label) {
        if (!Files.exists(path)) {
            System.out.println(label + " does not exist: " + path);
            return false;
        }
        if (!Files.isRegularFile(path)) {
            System.out.println(label + " is not a file: " + path);
            return false;
        }
        return true;
    }

    //Helper function to check if it is Directory or no
    private static boolean checkDirectory(Path path, String label) {
        if (!Files.exists(path)) {
            System.out.println(label + " does not exist: " + path);
            return false;
        }
        if (!Files.isDirectory(path)) {
            System.out.println(label + " is not a directory: " + path);
            return false;
        }
        return true;
    }


public class PwdCommand extends Command {
    public PwdCommand(String[] args) {
        super("pwd", args);
    }

    @Override
    public String[] execute() {
        
        if (args != null && args.length > 0) {
            System.out.println("pwd takes no arguments");
            return null;
        }

        
        String currentDir = System.getProperty("user.dir");
        System.out.println(currentDir);

        String[] redirectOutput = {currentDir};
        return redirectOutput;
    }
}


    public class CpCommand extends Command {
        public CpCommand(String[] args) {
            super("cp", args);
        }

        @Override
        public String[] execute() {
            if (args == null || args.length != 2) {
                System.out.println("cp <source> <destination>");
                return null;
            }

            Path src = Path.of(args[0]);
            Path dest = Path.of(args[1]);
            if (!checkFile(src, "Source") ) {
                return null;
            }


            try {
                try {
                    if (Files.exists(dest) && Files.isSameFile(src, dest)) {
                        System.out.println("source and destination are the same file.");
                        return null;
                    }
                } catch (IOException ignored) {
                }

                Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                System.out.println("Copied " + src + " -> " + dest);
            } catch (IOException e) {
                System.out.println("I/O error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("error: " + e.getMessage());
            }
            return null;
        }
    }

    public class CpRCommand extends Command {
        public CpRCommand(String[] args) {
            super("cp -r", args);
        }

        @Override
        public String[] execute() {
            if (args == null || args.length != 3) {
                System.out.println("cp -r <sourceDir> <destinationDir>");
                return null;
            }

            Path src = Path.of(args[1]);
            Path dest = Path.of(args[2]);

            if (!checkDirectory(src, "Source") || !checkDirectory(dest, "Destination")) {
                return null;
            }
            // Check if they r inside each other  and stop that
            try {
                Path Src_Real = src.toRealPath();
                Path Dest_Real = dest.toRealPath();
                if (Dest_Real.startsWith(Src_Real)) {
                    System.out.println("Destination is inside source ");
                    return null;
                }
            } catch (IOException e) {

                Path Src_Abs = src.toAbsolutePath().normalize();
                Path Dest_Abs = dest.toAbsolutePath().normalize();
                if (Dest_Abs.startsWith(Src_Abs)) {
                    System.out.println("Destination is inside source ");
                    return null;
                }
            }

            try (Stream<Path> stream = Files.walk(src)) {
                stream.forEach(sourcePath -> {
                    try {
                        Path relative = src.relativize(sourcePath);
                        Path targetPath = dest.resolve(relative);
                        if (Files.isDirectory(sourcePath)) {
                            Files.createDirectories(targetPath);
                        } else {
                            if (targetPath.getParent() != null) {
                                Files.createDirectories(targetPath.getParent());
                            }
                            Files.copy(sourcePath, targetPath,
                                    StandardCopyOption.REPLACE_EXISTING,
                                    StandardCopyOption.COPY_ATTRIBUTES);
                        }
                    } catch (IOException ioe) {
                        System.out.println("Error copying " + sourcePath + ": " + ioe.getMessage());
                    }
                });

                System.out.println("copy completed: " + src + " -> " + dest);
            } catch (IOException e) {
                System.out.println("I/O error while walking source: " + e.getMessage());
            }
            return null;
        }
    }

    public class RmCommand extends Command {
        public RmCommand(String[] args) {
            super("rm", args);
        }

        @Override
        public String[] execute() {
            if (args == null || args.length != 1) {
                System.out.println("rm <file>");
                return null;
            }
            Path target = Path.of(args[0]);
            try {
                Path targetAbs = target.toAbsolutePath().normalize();


                if (!checkFile(target, "Target")) {
                    return null;
                }
                Files.delete(targetAbs);
                System.out.println("Removed " + target.getFileName());
            } catch (IOException e) {
                System.out.println("rm: I/O error: " + e.getMessage());
            }
            return null;
        }
    }

    public class CdCommand extends Command {
        public CdCommand(String[] args) {
            super("cd", args);
        }

        @Override
        public String[] execute() {
            try {
                if (args.length == 0) {
                    System.setProperty("user.dir", System.getProperty("user.home"));
                } else if (args.length == 1 && args[0].equals("..")) {
                    Path current = Path.of(System.getProperty("user.dir"));
                    Path parent = current.getParent();
                    if (parent != null)
                        System.setProperty("user.dir", parent.toString());
                    else
                        System.out.println("Already at root directory");
                } else if (args.length == 1) {
                    Path newPath = Path.of(args[0]);
                    if (!newPath.isAbsolute()) {
                        newPath = Path.of(System.getProperty("user.dir")).resolve(newPath);
                    }
                    if (Files.exists(newPath) && Files.isDirectory(newPath)) {
                        System.setProperty("user.dir", newPath.normalize().toString());
                    } else {
                        System.out.println("Invalid directory: " + newPath);
                    }
                } else {
                    System.out.println("cd: too many arguments");
                }
            } catch (Exception e) {
                System.out.println("cd error: " + e.getMessage());
            }
            return null;
        }
    }
    public class LsCommand extends Command {
        public LsCommand(String[] args) {
            super("ls", args);
        }

        @Override
        public String[] execute() {
            List<String> redirectOutput = new ArrayList<>();
            try {
                int len = (args == null) ? 0 : args.length;

                if (redirect){
                    if (args.length == 1){
                        System.out.println("no file provided to redirect to");
                        redirect = false;
                        return null;
                    }
                }
                else if (len > 0){
                    System.out.println("ls takes no arguments");
                    return null;
                }

                File currentDir = new File(System.getProperty("user.dir"));
                File[] files = currentDir.listFiles();

                if (files == null) {
                    System.out.println("Error reading directory contents");
                    return null;
                }

                if (files.length == 0) {
                    System.out.println("(empty directory)");
                    String[] redirectOutputempty = {""};
                    return redirectOutputempty;
                }


                 Arrays.sort(files, Comparator.comparing(File::getName));

                for (File f : files) {
                    System.out.println(f.getName());
                    redirectOutput.add(f.getName());
                }
                return redirectOutput.toArray(new String[redirectOutput.size()]);

            } catch (Exception e) {
                System.out.println("ls error: " + e.getMessage());
            }
            return null;
        }
    }

    public class CatCommand extends Command {
        public CatCommand(String[] args) {
            super("cat", args);
        }

        List<String> redirectOutput = new ArrayList<>();

        //Helper function to read a file
        public void readFile(String file){
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while((line = reader.readLine()) != null){
                    System.out.println(line);
                    redirectOutput.add(line);
                }
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String[] execute() {
            int argsNum = 0;
            if (redirect){
                for (int i = 0; i < args.length; i++){
                    if (args[i].equals(">")){
                        break;
                    }
                    argsNum++;
                }
            }
            else {argsNum = args.length;}
            if (argsNum > 1){
                if (checkFile(Path.of(args[0]), "source 1")){
                    readFile(args[0]);
                }
                if (checkFile(Path.of(args[1]), "source 2")){
                    readFile(args[1]);
                }
                return redirectOutput.toArray(new String[redirectOutput.size()]);
            } else if (argsNum == 1) {
                if (checkFile(Path.of(args[0]), "source 1")){
                    readFile(args[0]);
                    return redirectOutput.toArray(new String[redirectOutput.size()]);
                }
            } else {
                System.out.println("No file path provided.");
                redirect = false;
                return null;
            }

            return null;
        }
    }
    public class WcCommand extends Command {
        public WcCommand(String[] args) {
            super("wc", args);
        }
        @Override
        public String[] execute() {
            if (args.length == 0 || args == null) {
                System.out.println("no file given.");
                redirect = false;
                return null;
            }
            if (!checkFile(Path.of(args[0]), "source 1")){
                redirect = false;
                return null;
            }
            int lines = 0, letters = 0, words = 0;
            try {
                BufferedReader reader = new BufferedReader(new FileReader(args[0]));
                String line;
                while((line = reader.readLine()) != null){
                    lines++;
                    letters += line.length();
                    words++;
                    for (int i = 0; i < line.length(); i++){
                        if (line.charAt(i) == ' '){words++;}
                    }
                }
                reader.close();
                System.out.printf("%d %d %d %s\n", lines, words, letters, args[0]);
                String[] redirectOutput = {String.format("%d %d %d %s\n", lines, words, letters, args[0])};
                return redirectOutput;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public class ZipCommand extends Command {
        public ZipCommand(String[] args) {
            super("zip", args);
        }

        @Override
        public String[] execute() {
            try {
                if (args.length < 2) {
                    System.out.println("zip <archive.zip> <files...>");
                    return null;
                }

                boolean recursive = args[0].equals("-r");
                int startIndex = recursive ? 2 : 1;
                String zipFileName = args[recursive ? 1 : 0];

                try (FileOutputStream fos = new FileOutputStream(zipFileName);
                     ZipOutputStream zos = new ZipOutputStream(fos)) {

                    for (int i = startIndex; i < args.length; i++) {
                        File file = new File(args[i]);
                        if (file.exists()) {
                            if (file.isDirectory() && recursive)
                                addDirToZip(file, file.getName(), zos);
                            else if (file.isFile())
                                addFileToZip(file, zos);
                        } else {
                            System.out.println("File not found: " + args[i]);
                        }
                    }
                }
                System.out.println("Created " + zipFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void addFileToZip(File file, ZipOutputStream zos) throws IOException {
            try (FileInputStream fis = new FileInputStream(file)) {
                ZipEntry entry = new ZipEntry(file.getName());
                zos.putNextEntry(entry);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0)
                    zos.write(buffer, 0, len);
                zos.closeEntry();
            }
        }

        private void addDirToZip(File folder, String parent, ZipOutputStream zos) throws IOException {
            for (File file : folder.listFiles()) {
                if (file.isDirectory()) {
                    addDirToZip(file, parent + "/" + file.getName(), zos);
                } else {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        ZipEntry entry = new ZipEntry(parent + "/" + file.getName());
                        zos.putNextEntry(entry);
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = fis.read(buffer)) > 0)
                            zos.write(buffer, 0, len);
                        zos.closeEntry();
                    }
                }
            }
        }
    }

    public class UnzipCommand extends Command {

        public UnzipCommand(String[] args) {
            super("unzip", args);
        }

        @Override
        public String[] execute() {
            if (args.length < 1) {
                System.out.println("Usage: unzip <archive.zip> [-d destination]");
                return null;
            }

            String zipFile = args[0];
            String destDir = ".";

            if (args.length == 3 && args[1].equals("-d"))
                destDir = args[2];

            File dir = new File(destDir);
            if (!dir.exists())
                dir.mkdirs();

            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    File newFile = new File(dir, entry.getName());

                    if (entry.isDirectory()) {
                        newFile.mkdirs();
                    } else {
                        new File(newFile.getParent()).mkdirs();
                        try (FileOutputStream fos = new FileOutputStream(newFile)) {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = zis.read(buffer)) > 0)
                                fos.write(buffer, 0, len);
                        }
                    }
                    zis.closeEntry();
                }
                System.out.println("Unzipped successfully to " + destDir);
            } catch (IOException e) {
                System.out.println("Error while unzipping: " + e.getMessage());
            }
            return null;
        }
    }


    public void chooseCommandAction() {
        String cmdName = p.getCommandName();
        String[] args = p.getArgs();
        String[] redirectOutput;
        int redirectIndex = 0;
        redirect = false;

        // check if there is a redirect command
        for (int i = 0; i < args.length; i++){
            if (args[i].equals(">")){
                redirect = true;
                redirectIndex = i;
            }
        }

        Command cmd;

        switch (cmdName) {
            case "pwd":
                cmd = new PwdCommand(args);
                break;
            case "cp":
                if (args != null && args.length > 0 && "-r".equals(args[0])) {
                    cmd = new CpRCommand(args);
                } else {
                    cmd = new CpCommand(args);
                }
                break;
            case "rm":
                cmd = new RmCommand(args);
                break;
            case "cd":
                cmd = new CdCommand(args);
                break;
            case "ls":
                cmd = new LsCommand(args);
                break;
                    
            case "zip":
                cmd = new ZipCommand(args);
                break;
            case "unzip":
                cmd = new UnzipCommand(args);
                break;
            case "cat":
                cmd = new CatCommand(args);
                break;
            case "wc":
                cmd = new WcCommand(args);
                break;
            default:
                cmd = new Command(cmdName, args);
                break;
        }

        // store the redirect output
        redirectOutput = cmd.execute();
        // check if there is a redirect and if so redirect to file
        if (redirect){
            if (redirectOutput.length == 0 || redirectOutput == null){
                System.out.println("no output to redirect");
            }
            else if (args.length == redirectIndex + 1){
                System.out.println("no file provided to redirect to");
                return;
            }
            else {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(args[redirectIndex + 1]));
                    if (redirectOutput.length == 1){
                        writer.write(redirectOutput[0]);
                    }
                    else {
                        writer.write(redirectOutput[0]);
                        for (int i = 1; i < redirectOutput.length; i++){
                            writer.write("\n" + redirectOutput[i]);
                        }
                    }
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    public static void main(String[] args) {
        Terminal t = new Terminal();
        Scanner sc = new Scanner(System.in);

        System.out.println("Simple Java Terminal. Type 'exit' to quit.");

        while (true) {
            System.out.print(">> ");
            String input = sc.nextLine();

            if (input.equalsIgnoreCase("exit"))
                break;

            if (t.p.parse(input))
                t.chooseCommandAction();
            else
                System.out.println("Invalid command!");
        }

        sc.close();
    }
}
