package ru.darek;

import java.sql.*;

public class InPostgresUserService implements UserService{
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/postgres";
    private  static final String SELECT_USERS_SQL = "SELECT u.login, u.username FROM homework24.users u WHERE u.login = ? AND u.password = ?;";
    private  static final String SELECT_IS_ADMIN_SQL = """
       SELECT u.login FROM homework24.users u, homework24.usertorole ur,homework24.roles r 
       WHERE u.username = ? AND u.login = ur.user_id AND r.id = ur.role_id AND r.name = 'ADMIN';
    """;
    private static final String SELECT_USER_BY_LOGIN_SQL = "SELECT * FROM homework24.users u WHERE u.login = ?;";
    private  static final String SELECT_USER_BY_USERNAME_SQL = "SELECT * FROM homework24.users u WHERE u.username = ?;";
    private static final String INSERT_USER_SQL = """   
        BEGIN TRANSACTION;
        INSERT INTO homework24.users VALUES (?,?,?);
        INSERT INTO homework24.usertorole VALUES (?,2);
        COMMIT TRANSACTION;
    """;

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        try(Connection connection = DriverManager.getConnection(DATABASE_URL, "postgres", "352800")) {
            try(PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USERS_SQL)) {
                preparedStatement.setString(1, login);
                preparedStatement.setString(2,password);
                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String username = resultSet.getString(2);
                        //System.out.println("InPostgresUserService: " + login + " " + password + " username: " + username);
                        return username;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //System.out.println("Не нашли пользователя " + login + " " + password);
        return null;
    }

    @Override
    public boolean getIsAdminByUsername(String username) {
        try(Connection connection = DriverManager.getConnection(DATABASE_URL, "postgres", "352800")) {
            try(PreparedStatement preparedStatement = connection.prepareStatement(SELECT_IS_ADMIN_SQL)) {
                preparedStatement.setString(1, username);
                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String role = resultSet.getString(1);
                        //System.out.println("InPostgresUserService: пользователь " + username + " имеет роль ADMIN");
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //System.out.println("InPostgresUserService: у пользователя " + username + " нет роли ADMIN");
        return false;
    }

    @Override
    public void createNewUser(String login, String password, String username) {
        try(Connection connection = DriverManager.getConnection(DATABASE_URL, "postgres", "352800")) {
            try(PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {
                preparedStatement.setString(1, login);
                preparedStatement.setString(2,password);
                preparedStatement.setString(3,username);
                preparedStatement.setString(4,login);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //System.out.println("Пользователь " + username + "(" + login + "/" + password + ") успешно создан c ролю manager");
    }

    @Override
    public boolean isLoginAlreadyExist(String login) {
        try(Connection connection = DriverManager.getConnection(DATABASE_URL, "postgres", "352800")) {
            try(PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_LOGIN_SQL)) {
                preparedStatement.setString(1, login);
                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String username = resultSet.getString(3);
                        //System.out.println("InPostgresUserService: логин " + login + " уже занят пользователем: " + username);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isUsernameAlreadyExist(String username) {
        try(Connection connection = DriverManager.getConnection(DATABASE_URL, "postgres", "352800")) {
            try(PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_USERNAME_SQL)) {
                preparedStatement.setString(1, username);
                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String login = resultSet.getString(1);
                        //System.out.println("InPostgresUserService: пользователь " + username + " уже занят логином: " + login);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
