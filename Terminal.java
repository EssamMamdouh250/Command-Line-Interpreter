/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

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


class Parser {
    String commandName;
    String[] args;

    public boolean parse(String s) {
        if (s == null || s.trim().isEmpty())
            return false;

        String[] arr = s.trim().split("\\s+");
        commandName =arr[0];
        args = Arrays.copyOfRange(arr, 1, arr.length);
        return true;
    }

    public String getCommandName() { return commandName; }
    public String[] getArgs() { return args; }
}


public class Terminal {
    Parser p = new Parser();

    public String pwd() {
        return System.getProperty("user.dir");
    }

    public void chooseCommandAction() {
        String cmd = p.getCommandName();
        String[] args = p.getArgs();

        switch (cmd) {
            case "pwd":
                System.out.println(pwd());
                break;
            default:
                System.out.println("Unknown command!");
        }
    }

    public static void main(String[] args) {
        Terminal t = new Terminal();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print(">> ");
            String input = sc.nextLine();

            if (input.equals("exit"))
                break;

            if (t.p.parse(input))
                t.chooseCommandAction();
            else
                System.out.println("Invalid command!");
        }

        sc.close();
    }
}
