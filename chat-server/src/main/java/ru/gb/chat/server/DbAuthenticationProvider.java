package ru.gb.chat.server;


import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbAuthenticationProvider implements AuthenticationProvider {//позволит черед БД(проверять и в пускать в наше приложение)
    // аутентифицировать пользователей
    private DbConnection dbConnection;

    @Override
    public void init() {
        dbConnection = new DbConnection();// при init
        // dbConnection открывается или бросает Exeption
    }


    @Override//подписываемся на контракт, что умеем аутентифицировать
    //пользователей
    public String getNicknameByLoginAndPassword(String login, String password) {
        //думаем как сделать поиск никнейма по логину и паролю

        //для этого надо отправить запрос в базу
        String query = String.format("select nickname from users where login = '%s' and password = '%s';", login, password);
        try (ResultSet rs = dbConnection.getStmt().executeQuery(query)) {
            if (rs.next()) {
                return rs.getString("nickname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override//изменить никнейм
    public void changeNickname(String oldNickname, String newNickname) {
        //обновить таблицу user, установить никнейм '%s'-такойто,где nickname -'%s'-такойто,)
        String query = String.format("update user set nickname = '%s' where nickname - '%s';", oldNickname, newNickname);

        try {
            //todo есть опастность нарваться на неуникальный ник
            dbConnection.getStmt().executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //обновить таблицу user, установить никнейм =
    }

    @Override
    public boolean isNickBusy(String nickname) {
        String query = String.format("select id from users where login = '%s' and password = '%s';", nickname);
        try (ResultSet rs = dbConnection.getStmt().executeQuery(query)) {
            if (rs.next()) {//здесь id запросим id из таблицы юзеров
                //у которых nickname = тому-то
                //если что-то вернулось --->rs.next())
                return true;//значит ник занят
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;//иначе не занят
    }

    @Override
    public void shutdown() {
        dbConnection.close();
    }
}



