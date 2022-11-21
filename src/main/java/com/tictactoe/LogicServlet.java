package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession currentSession = req.getSession();

        Field field = extractField(currentSession);
        int index = getSelectedIndex(req);

        Sign cellSign = field.getField().get(index);
        if (cellSign != Sign.EMPTY){
            RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            requestDispatcher.forward(req, resp);
            return ;
        }

        field.getField().put(index, Sign.CROSS);
        if (checkWin(resp, currentSession, field)){
            return;
        }

        int emptyFieldIndex = field.getEmptyFieldIndex();
        if (emptyFieldIndex > -1){
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            if (checkWin(resp, currentSession, field)){
                return;
            }
        } else {
            currentSession.setAttribute("draw", true);

            List<Sign> data = field.getFieldData();
            currentSession.setAttribute("data", data);
            resp.sendRedirect("/index.jsp");

            return;
        }

        List<Sign> data = field.getFieldData();
        currentSession.setAttribute("field", field);
        currentSession.setAttribute("data", data);

        resp.sendRedirect("/index.jsp");
    }

    private boolean checkWin(HttpServletResponse response, HttpSession session, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (winner == Sign.CROSS || winner == Sign.NOUGHT){
            session.setAttribute("winner", winner);

            List<Sign> fieldData = field.getFieldData();
            session.setAttribute("data", fieldData);

            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }

    private Field extractField(HttpSession session) {
        Object field = session.getAttribute("field");
        if (Field.class != field.getClass()) {
            session.invalidate();
            throw new RuntimeException("Session is broken, try one more time!");
        }
        return (Field) field;
    }

    private int getSelectedIndex(HttpServletRequest req) {
        String clickParam = req.getParameter("click");
        boolean isDigit = clickParam.chars().allMatch(Character::isDigit);
        return isDigit ? Integer.parseInt(clickParam) : 0;
    }
}
