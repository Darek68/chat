package ru.darek;

public interface UserService {
    String getUsernameByLoginAndPassword(String login, String password);
    boolean getIsAdminByUsername(String username);
    void createNewUser(String login, String password, String username);
    boolean isLoginAlreadyExist(String login);
    boolean isUsernameAlreadyExist(String username);

}