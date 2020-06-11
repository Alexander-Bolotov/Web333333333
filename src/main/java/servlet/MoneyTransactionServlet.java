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

public class MoneyTransactionServlet extends HttpServlet {
    BankClientService bankClientService = new BankClientService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HashMap<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("message", "");
        resp.setContentType("text/html;charset=utf-8");
        resp.encodeRedirectURL("registrationPage.html");
        resp.getWriter().println(PageGenerator.getInstance().getPage("moneyTransactionPage.html", pageVariables));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HashMap<String, Object> pageVariables = new HashMap<>();

        BankClient senderBankClient = new BankClient();
        String nameTo = req.getParameter("nameTo");
        String count = req.getParameter("count");
        String senderName = req.getParameter("SenderName");
        String senderPass = req.getParameter("SenderPass");
        senderBankClient.setName(senderName);
        senderBankClient.setPassword(senderPass);

        if (senderName != null && senderPass != null && nameTo != null && Long.parseLong(count) > 0L
                && bankClientService.sendMoneyToClient(senderBankClient, nameTo,
                Long.parseLong(count))) {

            resp.setStatus(HttpServletResponse.SC_OK);
            pageVariables.put("message", "The transaction was successful");

        } else {
            pageVariables.put("message", "transaction rejected");
            resp.setStatus(HttpServletResponse.SC_OK);
//                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        resp.setContentType("text/html;charset=utf-8");
        resp.getWriter().println(PageGenerator.getInstance().getPage("resultPage.html", pageVariables));
    }
}
