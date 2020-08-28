package inter;

import handler.ClientHandler;

public interface Server {
    int PORT = 8189;

    boolean isNickBusy(String nick);

    void broadcastMsg(String msg);

    void subscribe(ClientHandler client);

    void unsubscribe(ClientHandler client);

    AuthService getAuthService();

    void sendPrivateMsg(ClientHandler clientHandler, String nick, String clientStr);

    void sendMsgToClient(ClientHandler from, String to, String msg);

    void broadcastClientList();
}
