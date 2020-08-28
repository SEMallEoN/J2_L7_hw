package service;

import handler.ClientHandler;
import inter.AuthService;
import inter.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ServerImpl implements Server {

    private List<ClientHandler> clients;
    private AuthService authService;

    public ServerImpl() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT); // Создаем сокет на сервере
            authService = new AuthServiceImpl(); // Создаем список авторизованных клиентов
            authService.start(); // Сообщение о запуске службы авторизации клиентов
            clients = new LinkedList<>(); // Создаем список клиентов
            // Цикл подключения клиентов
            while (true) { // Подключение клиентов
                System.out.println("Ожидаем подключения клиентов");
                Socket socket = serverSocket.accept(); // Ожидание подключения клиента
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket); // Создаем для каждого клиент свой обработчик
            }
        } catch (IOException e) {
            System.out.println("Проблема на сервере");
        } finally {
            if (authService != null) {
                authService.stop(); // Сообщение об остановке сервера аутентификации
            }
        }
    }

    // Метод отсылки приватного сообщения
    public void sendPrivateMsg(ClientHandler fromClient, String toClient, String msg) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getNick().equals(toClient)) {
                clientHandler.sendMsg("От " + fromClient.getNick() + ": " + msg);
                fromClient.sendMsg("Кому " + toClient + ": " + msg);
                return;
            } else {
                fromClient.sendMsg(toClient + " не подключен к чату!");
            }
        }
    }

    // Метод проверки клиента на задвоение
    @Override
    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler c : clients) {
            if (c.getNick() != null && c.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    // Метод рассылки сообщений списку
    @Override
    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler c : clients) {
            c.sendMsg(msg);
        }
    }

    // Метод добавления клиента в список рассылки сообщений
    @Override
    public synchronized void subscribe(ClientHandler client) {
        clients.add(client);
    }

    // Метод удаления клиента из рассылки
    @Override
    public synchronized void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }

    // Метод возвращающий список авторизованных пользователей
    @Override
    public AuthService getAuthService() {
        return authService;
    }

    @Override
    public synchronized void sendMsgToClient(ClientHandler from, String to, String msg) {
        for (ClientHandler c : clients) {
            if (c.getNick().equals(to)) {
                c.sendMsg("from " + from.getNick() + ": " + msg);
                from.sendMsg("client " + to + ": " + msg);
                return;
            }
        }
    }

    @Override
    public synchronized void broadcastClientList() {
        StringBuilder builder = new StringBuilder("/clients");
        for (ClientHandler c : clients) {
            builder.append(c.getNick() + " ");
        }
        broadcastMsg(builder.toString());
    }
}