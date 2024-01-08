package ru.darek;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientApplication {
    //static String username;
    private static String username;

    private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;

    public static void main(String[] args) {
        try /*(
                Socket socket = new Socket("localhost", 8189);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        )*/ {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Подключились к серверу");
            Scanner scanner = new Scanner(System.in);
            new Thread(() -> {
                try {
                    while (true) {
                        String message = in.readUTF();
                        if (message.startsWith("/")) {
                            if (message.startsWith("/authok ")) {
                                username = message.split(" ")[1];
                                break;
                            }
                        }
                        System.out.println(message);
                    }
                    while (true) {
                        String message = in.readUTF();
                        if (message.equals("/exit_confirmed")) {
                            break;
                        }
                        System.out.println(message);
                    }
                } catch (EOFException e) {
                    System.out.println("Поток закрылся - читать нельзя!");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("Потеряна связь с сокетом!");
                    e.printStackTrace();
                }finally {
                    disconnect();
                }
            }).start();
            while (true) {
                String message = scanner.nextLine();
                out.writeUTF(message);
                if (message.equals("/exit")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void disconnect() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}