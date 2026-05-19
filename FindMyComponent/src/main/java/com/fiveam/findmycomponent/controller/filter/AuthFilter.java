package com.fiveam.findmycomponent.controller.filter;
import com.fiveam.findmycomponent.dao.UserDao;
import com.fiveam.findmycomponent.dao.UserDaoImpl;
import com.fiveam.findmycomponent.dao.UserSessionDao;
import com.fiveam.findmycomponent.dao.UserSessionDaoImpl;
import com.fiveam.findmycomponent.entity.Role;
import com.fiveam.findmycomponent.entity.User;
import com.fiveam.findmycomponent.entity.UserSession;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*")
public class AuthFilter implements Filter {

    // Public URL prefixes (static resources)
    private static final List<String> PUBLIC_PREFIXES = Arrays.asList(
            "/static/","/css/", "/js/", "/images/", "/uploads/","/about.jsp","/contact.jsp"
    );

    // Exact match public paths
    private static final List<String> PUBLIC_EXACT_PATHS = Arrays.asList(
            "/shop","/product-details"
    );

    // Auth pages (redirect to dashboard if already logged in)
    private static final List<String> AUTH_PAGES = Arrays.asList(
            "/login", "/register"
    );

    private UserDao userDao;
    private UserSessionDao sessionDao;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        userDao = new UserDaoImpl();
        sessionDao = new UserSessionDaoImpl();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Handle root path "/" separately
        if (path.isEmpty() || path.equals("/")) {
            chain.doFilter(request, response);
            return;
        }

        // ========== CHECK IF PATH IS PUBLIC (static resources) ==========
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // ========== GET LOGGED IN USER (if any) ==========
        HttpSession session = req.getSession(false);
        User user = null;

        if (session != null) {
            user = (User) session.getAttribute("user");
        }

        // If no user in session, check remember-me cookie
        if (user == null) {
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("remember_token".equals(cookie.getName())) {
                        String token = cookie.getValue();
                        UserSession userSession = sessionDao.findByToken(token);

                        if (userSession != null && !userSession.isExpired()) {
                            user = userDao.findById(userSession.getUserId());

                            if (user != null && user.isActive()) {
                                session = req.getSession(true);
                                session.setAttribute("user", user);
                                session.setMaxInactiveInterval(30 * 60);
                            }
                        }
                        break;
                    }
                }
            }
        }

        // ========== ALREADY LOGGED IN? Redirect from login/register to dashboard ==========
        if (user != null && isAuthPage(path)) {
            redirectToDashboard(req, res, user);
            return;
        }

        // ========== CHECK IF PATH REQUIRES AUTHENTICATION ==========
        // If it's an auth page (login/register) and user is null, allow access
        if (isAuthPage(path)) {
            chain.doFilter(request, response);
            return;
        }

        // If no user and path requires authentication, redirect to login
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Check if user is active
        if (!user.isActive()) {
            res.sendRedirect(req.getContextPath() + "/login?error=Account deactivated");
            return;
        }

        // ========== AUTHORIZATION (Role-based access) ==========

        int userRoleId = user.getRoleId();

        // Check /admin/* - ONLY ADMIN
        if (path.startsWith("/admin/")) {
            if (userRoleId != Role.ROLE_ADMIN) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied. Admin only area.");
                return;
            }
        }
        // Check /seller/* - ADMIN or SELLER
        else if (path.startsWith("/seller/")) {
            if (userRoleId != Role.ROLE_ADMIN && userRoleId != Role.ROLE_SELLER) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied. Seller only area.");
                return;
            }
        }
        // Check /buyer/* - Any logged-in user (no additional check needed)
        else if (path.startsWith("/buyer/")) {
            // All authenticated users can access
        }

        // User is authenticated AND authorized, proceed
        chain.doFilter(request, response);
    }

    /**
     * Checks if the path is public (no authentication required)
     */
    private boolean isPublicPath(String path) {
        if (path == null || path.isEmpty()) {
            return true;
        }

        // Check exact match public paths
        for (String exactPath : PUBLIC_EXACT_PATHS) {
            if (path.equals(exactPath)) {
                return true;
            }
        }

        // Check public prefixes (static resources)
        for (String prefix : PUBLIC_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the path is an auth page (login/register)
     */
    private boolean isAuthPage(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        for (String authPage : AUTH_PAGES) {
            if (path.equals(authPage)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Redirects logged-in user to their role-based dashboard
     */
    private void redirectToDashboard(HttpServletRequest req, HttpServletResponse res, User user) throws IOException {
        String contextPath = req.getContextPath();
        switch (user.getRoleId()) {
            case Role.ROLE_ADMIN:
                res.sendRedirect(contextPath + "/admin/users");
                break;
            case Role.ROLE_SELLER:
                res.sendRedirect(contextPath + "/seller/products");
                break;
            case Role.ROLE_BUYER:
            default:
                res.sendRedirect(contextPath + "/buyer/shop");
                break;
        }
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}