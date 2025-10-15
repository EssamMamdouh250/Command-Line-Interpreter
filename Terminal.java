package com.mycompany.terminal;

import java.util.Scanner;
import java.util.Arrays;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

class Command {
    String name;
    String[] args;

    public Command(String name, String[] args) {
        this.name = name;
        this.args = args;
    }

    public void execute() {
        System.out.println("Command not implemented: " + name);
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

    public class PwdCommand extends Command {
        public PwdCommand(String[] args) {
            super("pwd", args);
        }

        @Override
        public void execute() {
            System.out.println(System.getProperty("user.dir"));
        }
    }


    public void chooseCommandAction() {
        String cmdName = p.getCommandName();
        String[] args = p.getArgs();

        Command cmd;

        switch (cmdName) {
            case "pwd":
                cmd = new PwdCommand(args);
                break;
            default:
                cmd = new Command(cmdName, args);
                break;
        }

        cmd.execute();
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
