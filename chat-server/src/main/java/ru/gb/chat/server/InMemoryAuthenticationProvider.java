package ru.gb.chat.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryAuthenticationProvider implements AuthenticationProvider {
    private class User {
        private String login;
        private String password;
        private String nickname;

        public User(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }

    private List<User> users;

    public InMemoryAuthenticationProvider() {
        this.users = new ArrayList<>(Arrays.asList(
                new User("Bob", "100", "MegaBob"),
                new User("Jack", "100", "Mystic"),
                new User("John", "100", "Wizard")
        ));
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (User u : users) {
            if (u.login.equals(login) && u.password.equals(password)) {
                return u.nickname;
            }
        }
        return null;
    }

    @Override
    public void changeNickname(String oldNickname, String newNickname) {
        for (User u : users) {
            if (u.nickname.equals(oldNickname)) {
                u.nickname = newNickname;
                return;
            }
        }
    }
    //перебираем всел юзеров
    @Override
    public boolean isNickBusy(String nickname) {//реализуем метод
        for (User u : users) {
            if (u.nickname.equals(nickname)) {//если вид что у какого-то
                //юзера такой ник уже есть,
                return true;//то ник занят
            }
        }
        return false;//иначе не занят
    }
    @Override
    public void init() {
    }

    @Override
    public void shutdown() {
    }
}
