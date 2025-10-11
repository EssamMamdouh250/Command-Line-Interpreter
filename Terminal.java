import java.nio.file.*;
import java.util.*;

public class Terminal {

    public void execute(String command, String[] args) {
        try {
            switch (command) {
                case "pwd":
                    pwd();
                    break;

                case "cd":
                    cd(args);
                    break;

                case "ls":
                    ls();
                    break;

                case "mkdir":
                    mkdir(args);
                    break;

                case "rmdir":
                    rmdir(args);
                    break;

                case "touch":
                    touch(args);
                    break;

                case "rm":
                    rm(args);
                    break;

                case "cp":
                    cp(args);
                    break;

                case "cp-r":
                case "cp -r":
                    cp_r(args);
                    break;

                case "cat":
                    cat(args);
                    break;

                case "wc":
                    wc(args);
                    break;

                case ">":
                    // TODO
                    break;

                case ">>":
                    // TODO
                    break;

                case "zip":
                    zip(args);
                    break;

                case "unzip":
                    unzip(args);
                    break;

                case "exit":
                    System.out.println("Exiting Program CLI, Goodbye...");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Error: Unknown command '" + command + "'");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error while executing command: " + e.getMessage());
        }
    }

    // ========== Commands skeleton ==========

    public String pwd() {
        // TODO
        return "pwd";
    }

    public void cd(String[] args) {
        // TODO
    }

    public void ls() {
        // TODO
    }

    public void mkdir(String[] args) {
        // TODO
    }

    public void rmdir(String[] args) {
        // TODO
    }

    public void touch(String[] args) {
        // TODO
    }

    public void rm(String[] args) {
        // TODO
    }

    public void cp(String[] args) {
        // TODO
    }

    public void cp_r(String[] args) {
        // TODO
    }

    public void cat(String[] args) {
        // TODO
    }

    public void wc(String[] args) {
        // TODO
    }

    public void redirectOutput(String command, String fileName) {
        // TODO (for > redirection)
    }

    public void appendOutput(String command, String fileName) {
        // TODO (for >> redirection)
    }

    public void zip(String[] args) {
        // TODO
    }

    public void unzip(String[] args) {
        // TODO
    }
}
