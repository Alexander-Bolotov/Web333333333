package service;

import dao.BankClientDAO;
import exception.DBException;
import model.BankClient;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class BankClientService {
    BankClientDAO dao = getBankClientDAO();

    public BankClientService() {
    }



    public BankClient getClientByName(String name) {
        return getBankClientDAO().getClientByName(name);
    }

    public List<BankClient> getAllClient() {
        return getBankClientDAO().getAllBankClient();
    }

//    public boolean deleteClient(String name) throws DBException {
//        try {
//            getBankClientDAO().deleteClient(getClientByName(name));
//            return true;
//        } catch (SQLException e) {
//            throw new DBException(e);
//        }
//    }

    public boolean addClient(BankClient client) {
        if (!dao.nameIsExist(client.getName())) {
            getBankClientDAO().addClient(client);
            return true;
        }
        return false;
    }

//    public boolean sendMoneyToClient(BankClient sender, String namePayee, Long value) {
//        sender = dao.getClientByName(sender.getName());
//        String nameSender = sender.getName();
//
//        BankClient clientPayee = dao.getClientByName(namePayee);
//        String passwordPayee = clientPayee.getPassword();
//
//        if (dao.validateClient(nameSender, sender.getPassword()) &&
//                dao.isClientHasSum(nameSender, value)) {
//
//            dao.updateClientsMoney(nameSender, sender.getPassword(), -value);
//            dao.updateClientsMoney(namePayee, passwordPayee, value);
//
//            return true;
//        }
//
//        return false;
//    }

    public boolean sendMoneyToClient(BankClient sender, String name, Long value) {
        if (dao.isClientHasSum(sender.getName(), value) && dao.validateClient(sender.getName(), sender.getPassword())) {
            dao.updateClientsMoney(sender.getName(), sender.getPassword(), -value);
            dao.updateClientsMoney(name, dao.getClientByName(name).getPassword(), value);
            return true;
        } else {
            return false;
        }
    }

    public void cleanUp() throws DBException {
        try {
            getBankClientDAO().dropTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void createTable() throws DBException {
        try {
            getBankClientDAO().createTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    private static Connection getMysqlConnection() {
        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());

            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").        //db type
                    append("localhost:").           //host name
                    append("3306/").                //port
                    append("db_example?").          //db name
                    append("user=root&").           //login
                    append("password=root").       //password
                    append("&serverTimezone=UTC");

            System.out.println("URL: " + url + "\n");

            return DriverManager.getConnection(url.toString());
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    private static BankClientDAO getBankClientDAO() {
        return new BankClientDAO(getMysqlConnection());
    }
}
