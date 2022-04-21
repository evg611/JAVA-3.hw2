package ru.gb.chat.server;



public interface AuthenticationProvider {
    void init();//говорим что провайдер подготовся

    String getNicknameByLoginAndPassword(String login, String password);

    void changeNickname(String oldNickname, String newNickname);

    boolean isNickBusy(String nickname);//ник занят

    void shutdown();//провайдер, заверши работу

}


