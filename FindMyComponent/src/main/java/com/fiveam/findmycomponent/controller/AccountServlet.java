package com.fiveam.findmycomponent.controller;
import com.fiveam.findmycomponent.dao.UserDao;
import com.fiveam.findmycomponent.dao.UserDaoImpl;
import com.fiveam.findmycomponent.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * AccountServlet - Allows users to view and update their profile
 * URL: /buyer/account
 *
 * Access: Any logged-in user (AuthFilter ensures authentication)
 *
 * Editable fields:
 * - First Name
 * - Last Name
 * - Phone
 *
 * Read-only fields:
 * - Username
 * - Email
 * - Role
 */
@WebServlet("/buyer/account")
public class AccountServlet extends HttpServlet {

    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDaoImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get logged-in user from session
        HttpSession session = request.getSession(false);
        User loggedInUser = (User) session.getAttribute("user");

        // Fetch fresh user data from database
        User user = userDao.findById(loggedInUser.getId());

        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/buyer/account.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get logged-in user from session
        HttpSession session = request.getSession(false);
        User loggedInUser = (User) session.getAttribute("user");

        // Get form parameters
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phone = request.getParameter("phone");

        // Fetch fresh user data from database
        User user = userDao.findById(loggedInUser.getId());

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/buyer/account?error=User not found");
            return;
        }

        // Update editable fields only
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);

        // Save to database
        boolean updated = userDao.update(user);

        if (updated) {
            // Update session user as well
            session.setAttribute("user", user);
            response.sendRedirect(request.getContextPath() + "/buyer/account?success=Profile updated successfully");
        } else {
            response.sendRedirect(request.getContextPath() + "/buyer/account?error=Failed to update profile");
        }
    }
}