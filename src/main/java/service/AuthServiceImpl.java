package service;

import inter.AuthService;

import java.util.LinkedList;
import java.util.List;

public class AuthServiceImpl implements AuthService {

    private List<UserEntity> userList;

    public AuthServiceImpl() {
        this.userList = new LinkedList<>();
        this.userList.add(new UserEntity("nick1", "login1", "pass1"));
        this.userList.add(new UserEntity("nick2", "login2", "pass2"));
        this.userList.add(new UserEntity("nick3", "login3", "pass3"));

    }

    @Override
    public void start() {
        System.out.println("Authentication service started");
    }

    @Override
    public String getNick(String login, String password) {
        for (UserEntity u : userList) {
            if (u.login.equals(login) && u.password.equals(password)) {
                return u.nick;
            }
        }
        return null;
    }

    @Override
    public void stop() {

    }

    private class UserEntity {
        private String nick;
        private String login;
        private String password;

        public UserEntity(String nick, String login, String password) {
            this.nick = nick;
            this.login = login;
            this.password = password;
        }
    }
}
