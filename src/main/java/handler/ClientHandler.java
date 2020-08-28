package handler;

import inter.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private String nick;

    public String getNick() {
        return nick;
    }

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
            this.nick = "";
            new Thread(() -> {
                try {
                    long a = System.currentTimeMillis();
                    while ((a - System.currentTimeMillis()) > -120000) {
                        if (authentication()) {
                            readMessage();
                        } else {
                            System.out.println("The client is not logged in within 120 seconds");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Problems creating a client handler");
        }
    }

    private boolean authentication() throws IOException {
        while (true) {
            String str = dis.readUTF();
            if (str.startsWith("/auth")) {
                String[] dataArray = str.split("\\s");
                String nick = server.getAuthService().getNick(dataArray[1], dataArray[2]);
                if (nick != null) {
                    if (!server.isNickBusy(nick)) {
                        sendMsg("/authOk " + nick);
                        this.nick = nick;
                        server.broadcastMsg(this.nick + " Join to chat");
                        server.subscribe(this);
                        return true;
                    } else {
                        sendMsg("You are logged in");
                        return false;
                    }
                } else {
                    sendMsg("Incorrect password or login");
                    return false;
                }
            }
        }
    }

    public void sendMsg(String msg) {
        try {
            dos.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readMessage() throws IOException {
        while (true) {
            String clientStr = dis.readUTF();
            if (clientStr.startsWith("/")) {
                if (clientStr.equals("/exit")) {
                    return;
                }
                if (clientStr.startsWith("/w")) {
                    String[] strArray = clientStr.split("\\s");
                    String nickName = strArray[1];
                    String msg = clientStr.substring(4 + nickName.length());
                }
                continue;
            }
            server.broadcastMsg(this.nick + ": " + clientStr);
        }
    }

    private void closeConnection() {
        server.unsubscribe(this);
        server.broadcastMsg(this.nick + ": out from chat");

        try {
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
