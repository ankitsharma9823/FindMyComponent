package com.fiveam.findmycomponent.controller;

import com.fiveam.findmycomponent.dao.UserDao;
import com.fiveam.findmycomponent.dao.UserDaoImpl;
import com.fiveam.findmycomponent.dao.UserSessionDao;
import com.fiveam.findmycomponent.dao.UserSessionDaoImpl;
import com.fiveam.findmycomponent.entity.User;
import com.fiveam.findmycomponent.entity.Role;
import com.fiveam.findmycomponent.entity.UserSession;
import com.fiveam.findmycomponent.utils.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * LoginServlet - Handles user login authentication
 * URL: /login
 * - GET: Displays login form
 * - POST: Processes login form submission
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserDao userDAO;
    private UserSessionDao sessionDAO;

    // Remember Me cookie settings
    private static final int REMEMBER_ME_DAYS = 7;
    private static final int SESSION_TIMEOUT_MINUTES = 30;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDaoImpl();
        sessionDAO = new UserSessionDaoImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward to login page
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get form parameters
        String usernameOrEmail = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        // Validation: Check if username/email is provided
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            request.setAttribute("error", "Username or email is required");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        // Validation: Check if password is provided
        if (password == null || password.isEmpty()) {
            request.setAttribute("error", "Password is required");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        // Trim whitespace
        usernameOrEmail = usernameOrEmail.trim();

        try {
            // Try to find user by username first, then by email
            User user = userDAO.findByUsername(usernameOrEmail);
            if (user == null) {
                user = userDAO.findByEmail(usernameOrEmail);
            }

            // Check if user exists
            if (user == null) {
                request.setAttribute("error", "Invalid username/email or password");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                return;
            }

            // Verify password
            boolean passwordMatches = PasswordUtil.verifyPassword(password, user.getPasswordHash());

            if (!passwordMatches) {
                request.setAttribute("error", "Invalid username/email or password");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                return;
            }

            // Check if account is active
            if (!user.isActive()) {
                request.setAttribute("error", "Your account has been deactivated. Please contact support.");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                return;
            }

            // Login successful - update last login timestamp
            userDAO.updateLastLogin(user.getId());

            // Create HTTP session
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(SESSION_TIMEOUT_MINUTES * 60); // Convert to seconds

            // Handle "Remember Me" if checked
            if (rememberMe != null && rememberMe.equals("on")) {
                createRememberMeToken(user, request, response);
            }

            // Redirect based on user role
            redirectBasedOnRole(request,response, user);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred during login. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }

    /**
     * Creates a remember-me token and sets cookie
     */
    private void createRememberMeToken(User user, HttpServletRequest request, HttpServletResponse response) {
        // Generate random token
        String token = UUID.randomUUID().toString().replace("-", "");

        // Calculate expiry date (7 days from now)
        long expiryMillis = System.currentTimeMillis() + (REMEMBER_ME_DAYS * 24 * 60 * 60 * 1000L);
        Date expiresAt = new Date(expiryMillis);

        // Create and save session object
        UserSession userSession = new UserSession(user.getId(), token, expiresAt);
        boolean saved = sessionDAO.save(userSession);

        if (saved) {
            // Create cookie
            Cookie rememberCookie = new Cookie("remember_token", token);
            rememberCookie.setMaxAge(REMEMBER_ME_DAYS * 24 * 60 * 60); // 7 days in seconds
            rememberCookie.setPath(request.getContextPath());
            rememberCookie.setHttpOnly(true);
            response.addCookie(rememberCookie);
        }
    }

    /**
     * Redirects user based on their role
     */
    private void redirectBasedOnRole(HttpServletRequest request,HttpServletResponse response, User user) throws IOException {
        String contextPath = request.getContextPath();

        switch (user.getRoleId()) {
            case Role.ROLE_ADMIN:
                response.sendRedirect(contextPath + "/admin/users");
                break;
            case Role.ROLE_SELLER:
                response.sendRedirect(contextPath + "/seller/products");
                break;
            case Role.ROLE_BUYER:
            default:
                response.sendRedirect(contextPath + "/shop");
                break;
//            default:
//                response.sendRedirect(contextPath + "/index.jsp");
//                break;
        }
    }
}