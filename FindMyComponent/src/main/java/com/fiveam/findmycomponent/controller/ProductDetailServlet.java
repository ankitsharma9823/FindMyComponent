package com.fiveam.findmycomponent.controller;
import com.fiveam.findmycomponent.dao.CategoryDao;
import com.fiveam.findmycomponent.dao.CategoryDaoImpl;
import com.fiveam.findmycomponent.dao.ProductDao;
import com.fiveam.findmycomponent.dao.ProductDaoImpl;
import com.fiveam.findmycomponent.dao.UserDao;
import com.fiveam.findmycomponent.dao.UserDaoImpl;
import com.fiveam.findmycomponent.entity.Category;
import com.fiveam.findmycomponent.entity.Product;
import com.fiveam.findmycomponent.entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ProductDetailServlet - Displays detailed information about a single product
 * URL: /product-details
 *
 * Access: PUBLIC (no login required)
 *
 * URL Parameter:
 * - id: Product ID (e.g., ?id=1)
 *
 * Features:
 * - Shows product details (name, brand, price, description, image)
 * - Shows seller information
 * - Shows stock availability
 * - Provides Add to Cart button (if in stock)
 */
@WebServlet("/product-details")
public class ProductDetailServlet extends HttpServlet {

    private ProductDao productDao;
    private UserDao userDao;
    private CategoryDao categoryDao;

    @Override
    public void init() throws ServletException {
        productDao = new ProductDaoImpl();
        userDao = new UserDaoImpl();
        categoryDao = new CategoryDaoImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get product ID from request parameter
        String idParam = request.getParameter("id");

        // Validate ID parameter
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product ID is required");
            return;
        }

        try {
            int productId = Integer.parseInt(idParam);

            // Find product by ID
            Product product = productDao.findById(productId);

            // Check if product exists
            if (product == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
                return;
            }

            // Check if product is active (only show active products to public)
            if (!product.isActive()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product is not available");
                return;
            }

            // Get seller information
            User seller = userDao.findById(product.getSellerId());
            if (seller == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Seller not found");
                return;
            }

            // Get category information
            Category category = categoryDao.findById(product.getCategoryId());

            // Check if product is in stock
            boolean inStock = product.getStockQuantity() > 0;

            // Set attributes for JSP
            request.setAttribute("product", product);
            request.setAttribute("seller", seller);
            request.setAttribute("category", category);
            request.setAttribute("inStock", inStock);

            // Forward to product detail JSP
            request.getRequestDispatcher("/WEB-INF/views/shop/product-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid product ID format");
        }
    }
}