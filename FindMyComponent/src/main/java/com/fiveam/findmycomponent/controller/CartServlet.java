package com.fiveam.findmycomponent.controller;

import com.fiveam.findmycomponent.dao.CartDao;
import com.fiveam.findmycomponent.dao.CartDaoImpl;
import com.fiveam.findmycomponent.dao.ProductDao;
import com.fiveam.findmycomponent.dao.ProductDaoImpl;
import com.fiveam.findmycomponent.entity.CartItem;
import com.fiveam.findmycomponent.entity.Product;
import com.fiveam.findmycomponent.entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * CartServlet - Manages shopping cart operations for buyers
 * URL: /buyer/cart
 *
 * Access: LOGIN REQUIRED (AuthFilter guarantees user is logged in)
 *
 * GET requests:
 * - /buyer/cart - Display cart page with all items
 *
 * POST requests:
 * - action=add - Add product to cart
 * - action=update - Update item quantity
 * - action=remove - Remove item from cart
 * - action=clear - Clear entire cart
 */
@WebServlet("/buyer/cart")
public class CartServlet extends HttpServlet {

    private CartDao cartDao;
    private ProductDao productDao;

    @Override
    public void init() throws ServletException {
        cartDao = new CartDaoImpl();
        productDao = new ProductDaoImpl();
    }

    // ==================== HTTP METHODS ====================

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // AuthFilter guarantees user is logged in
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        // Get cart data for this user
        List<CartItem> cartItems = cartDao.getCartByUserId(user.getId());
        double cartTotal = cartDao.getCartTotal(user.getId());
        int itemCount = cartDao.getCartItemCount(user.getId());

        // Set attributes for JSP
        request.setAttribute("cartItems", cartItems);
        request.setAttribute("cartTotal", cartTotal);
        request.setAttribute("itemCount", itemCount);

        // Forward to cart page
        request.getRequestDispatcher("/WEB-INF/buyer/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            handleAdd(request, response, user);
        } else if ("update".equals(action)) {
            handleUpdate(request, response, user);
        } else if ("remove".equals(action)) {
            handleRemove(request, response, user);
        } else if ("clear".equals(action)) {
            handleClear(request, response, user);
        }

        // Redirect back to cart page after any action
        response.sendRedirect(request.getContextPath() + "/buyer/cart");
    }

    // ==================== ACTION HANDLERS ====================

    /**
     * Handles adding a product to cart
     */
    private void handleAdd(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {

        String productIdParam = request.getParameter("productId");
        String quantityParam = request.getParameter("quantity");

        if (productIdParam == null || productIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/buyer/cart?error=Product ID required");
            return;
        }

        try {
            int productId = Integer.parseInt(productIdParam);
            int quantity = (quantityParam != null && !quantityParam.isEmpty())
                    ? Integer.parseInt(quantityParam) : 1;

            if (quantity <= 0) {
                response.sendRedirect(request.getContextPath() + "/buyer/cart?error=Quantity must be greater than 0");
                return;
            }

            // Check if product exists and is active
            Product product = productDao.findById(productId);
            if (product == null || !product.isActive()) {
                response.sendRedirect(request.getContextPath() + "/buyer/cart?error=Product not available");
                return;
            }

            // Check stock availability
            if (product.getStockQuantity() < quantity) {
                response.sendRedirect(request.getContextPath() + "/buyer/cart?error=Insufficient stock. Only "
                        + product.getStockQuantity() + " available");
                return;
            }

            // Add to cart (will update quantity if already exists)
            cartDao.addToCart(user.getId(), productId, quantity);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/buyer/cart?error=Invalid product ID");
        }
    }

    /**
     * Handles updating quantity of a cart item
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, User user) {

        String productIdParam = request.getParameter("productId");
        String quantityParam = request.getParameter("quantity");

        if (productIdParam == null || quantityParam == null) {
            return;
        }

        try {
            int productId = Integer.parseInt(productIdParam);
            int quantity = Integer.parseInt(quantityParam);

            if (quantity <= 0) {
                // Remove item if quantity is 0 or negative
                cartDao.removeFromCart(user.getId(), productId);
            } else {
                cartDao.updateQuantity(user.getId(), productId, quantity);
            }
        } catch (NumberFormatException e) {
            // Invalid input, ignore
        }
    }

    /**
     * Handles removing a single item from cart
     */
    private void handleRemove(HttpServletRequest request, HttpServletResponse response, User user) {

        String productIdParam = request.getParameter("productId");

        if (productIdParam != null) {
            try {
                int productId = Integer.parseInt(productIdParam);
                cartDao.removeFromCart(user.getId(), productId);
            } catch (NumberFormatException e) {
                // Invalid input, ignore
            }
        }
    }

    /**
     * Handles clearing entire cart
     */
    private void handleClear(HttpServletRequest request, HttpServletResponse response, User user) {
        cartDao.clearCart(user.getId());
    }
}