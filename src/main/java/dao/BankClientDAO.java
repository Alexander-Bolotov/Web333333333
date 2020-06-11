package dao;

import com.sun.deploy.util.SessionState;
import model.BankClient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankClientDAO {

    private Connection connection;
    static int countI = 0;

    public BankClientDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean nameIsExist(String name) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM bank_client WHERE name = ?");
            preparedStatement.setString(1, name);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();

            resultSet.next();
            return resultSet.getInt(1) == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<BankClient> getAllBankClient() {
        String queryAllClient = "SELECT * FROM bank_client";
        List<BankClient> clients = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(queryAllClient)) {
            stmt.execute();
            ResultSet resultSet = stmt.getResultSet();
            while (resultSet.next()) {
                clients.add(new BankClient(resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("password"),
                        resultSet.getLong("money")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    public boolean validateClient(String name, String password) {
        String selectCount = "SELECT COUNT(*) FROM bank_client WHERE name = ? and password = ?";

        try (PreparedStatement stmt = connection.prepareStatement(selectCount)) {
            stmt.setString(1, name);
            stmt.setString(2, password);
            stmt.execute();
            ResultSet resultSet = stmt.getResultSet();

            resultSet.next();
            return resultSet.getInt(1) == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public void updateClientsMoney(String name, String password, Long transactValue) {
        BankClient bankClientPayee = getClientByName(name);

        try (PreparedStatement preparedStatementSecond = connection.prepareStatement
                ("UPDATE bank_client SET money = ? WHERE name = ? AND password = ?")) {

            preparedStatementSecond.setLong(1, bankClientPayee.getMoney() + transactValue);
            preparedStatementSecond.setString(2, name);
            preparedStatementSecond.setString(3, password);

            preparedStatementSecond.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public BankClient getClientById(long id) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("SELECT * FROM bank_client WHERE id=" + id);
        ResultSet resultSet = stmt.getResultSet();
        BankClient bankClient = new BankClient(id, resultSet.getString(2), resultSet.getString(3), resultSet.getLong(4));
        stmt.close();
        return bankClient;
    }

    public boolean isClientHasSum(String name, Long expectedSum) {
        return getClientByName(name).getMoney() >= expectedSum;
    }

    public long getClientIdByName(String name) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_clien where name='" + name + "'");
        ResultSet result = stmt.getResultSet();
        result.next();
        Long id = result.getLong(1);
        result.close();
        stmt.close();
        return id;

    }

    public BankClient getClientByName(String name) {
        String queryClientByName = "SELECT * FROM bank_client WHERE name = ?";
        BankClient clientResult = new BankClient();

        try (PreparedStatement stmt = connection.prepareStatement(queryClientByName)) {
            stmt.setString(1, name);
            stmt.execute();
            ResultSet resultSet = stmt.getResultSet();
            resultSet.next();
            clientResult.setId(resultSet.getInt("id"));
            clientResult.setName(resultSet.getString("name"));
            clientResult.setPassword(resultSet.getString("password"));
            clientResult.setMoney(resultSet.getLong("money"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientResult;
    }

    public void addClient(BankClient client) {
        String queryAddClient = "INSERT INTO bank_client (name , password, money) values (?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(queryAddClient);
            preparedStatement.setString(1, client.getName());
            preparedStatement.setString(2, client.getPassword());
            preparedStatement.setLong(3, client.getMoney());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public boolean deleteClient(BankClient client) throws SQLException {
        String quire = "DELETE FROM bank_client WHERE id= ?";

        if (validateClient(client.getName(), client.getPassword())) {
            PreparedStatement preparedStatement = connection.prepareStatement(quire);
            preparedStatement.setLong(1, getClientIdByName(client.getName()));
            preparedStatement.execute();
            preparedStatement.close();
            return true;
        }
        return false;
    }

    public void createTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("CREATE TABLE if NOT EXISTS bank_client " +
                "(id bigint auto_increment, " +
                "name varchar(256) NOT NULL UNIQUE, " +
                "password varchar(256) NOT NULL, " +
                "money bigint, " +
                "primary key (id))");
        stmt.close();
    }

    public void dropTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS bank_client");
        stmt.close();
    }
}
