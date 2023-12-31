package ru.darek;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;
    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("Сервер запущен на порту %d. Ожидание подключения клиентов\n", port);
            userService = new InMemoryUserService();
          //  userService = new InPostgresUserService();
            System.out.println("Запущен сервис для работы с пользователями");
            while (true) {
                Socket socket = serverSocket.accept();
                try {
                    new ClientHandler(this, socket);
                } catch (IOException e) {
                    System.out.println("Не удалось подключить клиента");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(message);
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        broadcastMessage("Подключился новый клиент " + clientHandler.getUsername());
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastMessage("Отключился клиент " + clientHandler.getUsername());
    }

    public synchronized boolean isUserBusy(String username) {
        for (ClientHandler c : clients) {
            if (c.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void sendPrivateMessage(ClientHandler sender, String receiverUsername, String message) {
        boolean findReciver = false;
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equals(receiverUsername)) {
                clientHandler.sendMessage("<private> " + sender.getUsername() + ": " + message);
                findReciver = true;
            }
        }
        if (findReciver) {
            sender.sendMessage("<private> " + sender.getUsername() + ": " + message);
        } else {
            sender.sendMessage("<private> Не найден пользователь: " + receiverUsername);
        }
    }

    public void kick(String kickUsername, ClientHandler admin) {
        // System.out.println( "kick " + kickUsername);
        ClientHandler kickHandler = null;
        for (ClientHandler clientHandler : clients) {
            //  System.out.println(kickUsername + " " + clientHandler.getUsername());
            if (clientHandler.getUsername().equals(kickUsername)) {
                kickHandler = clientHandler;
                break;
            }
        }
        if (kickHandler != null) {
            kickHandler.sendMessage("/exit_confirmed");
            kickHandler.disconnect();
        } else {
            admin.sendMessage("<private> Не найден пользователь: " + kickUsername);
        }
    }
}