package servlet;

import exception.DBException;
import model.BankClient;
import service.BankClientService;
import util.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

public class RegistrationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HashMap<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("message", "");
        resp.setContentType("text/html;charset=utf-8");
        resp.encodeRedirectURL("registrationPage.html");
        resp.getWriter().println(PageGenerator.getInstance().getPage("registrationPage.html", pageVariables));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String password = req.getParameter("password");
        long money = Long.parseLong(req.getParameter("money"));
        BankClient newBankClient = new BankClient(name, password, money);
        HashMap<String, Object> pageVariables = new HashMap<>();


        if (name != null && password != null && money >= 0L && (new BankClientService().addClient(newBankClient))) {
            resp.setStatus(HttpServletResponse.SC_OK);
            pageVariables.put("message", "Add client successful");
        } else {
            pageVariables.put("message", "Client not add");
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        resp.setContentType("text/html;charset=utf-8");
            resp.getWriter().println(PageGenerator.getInstance().getPage("resultPage.html" , pageVariables));

    }
}
