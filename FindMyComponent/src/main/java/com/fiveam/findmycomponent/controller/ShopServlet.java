package com.fiveam.findmycomponent.controller;

import com.fiveam.findmycomponent.dao.CategoryDao;
import com.fiveam.findmycomponent.dao.CategoryDaoImpl;
import com.fiveam.findmycomponent.dao.ProductDao;
import com.fiveam.findmycomponent.dao.ProductDaoImpl;
import com.fiveam.findmycomponent.entity.Category;
import com.fiveam.findmycomponent.entity.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

//@WebServlet("/shop")
public class ShopServlet extends HttpServlet {

    private ProductDao productDao;
    private CategoryDao categoryDao;

    private static final String SORT_PRICE_ASC = "price_asc";
    private static final String SORT_PRICE_DESC = "price_desc";
    private static final String SORT_NEWEST = "newest";

    private static final int PAGE_SIZE = 12;

    @Override
    public void init() throws ServletException {
        productDao = new ProductDaoImpl();
        categoryDao = new CategoryDaoImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get parameters
        String categoryParam = request.getParameter("category");
        String searchKeyword = request.getParameter("search");
        String sortParam = request.getParameter("sort");
        String pageParam = request.getParameter("page");

        // Get products based on category filter
        List<Product> allProducts;
        Integer selectedCategoryId = null;

        if (categoryParam != null && !categoryParam.isEmpty()) {
            try {
                selectedCategoryId = Integer.parseInt(categoryParam);
                // Get products by category, then filter active ones
                List<Product> categoryProducts = productDao.findByCategoryId(selectedCategoryId);
                allProducts = categoryProducts.stream()
                        .filter(Product::isActive)
                        .collect(Collectors.toList());
                request.setAttribute("selectedCategoryId", selectedCategoryId);
            } catch (NumberFormatException e) {
                allProducts = productDao.findAllActive();
            }
        } else {
            allProducts = productDao.findAllActive();
        }

        // Apply search filter (in Java)
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            String search = searchKeyword.trim().toLowerCase();
            allProducts = allProducts.stream()
                    .filter(p -> p.getName().toLowerCase().contains(search) ||
                            (p.getBrand() != null && p.getBrand().toLowerCase().contains(search)))
                    .collect(Collectors.toList());
            request.setAttribute("searchKeyword", searchKeyword);
        }

        // Apply sorting (in Java)
        if (SORT_PRICE_ASC.equals(sortParam)) {
            allProducts.sort(Comparator.comparing(Product::getPrice));
        } else if (SORT_PRICE_DESC.equals(sortParam)) {
            allProducts.sort(Comparator.comparing(Product::getPrice).reversed());
        } else {
            // Default: newest first
            allProducts.sort(Comparator.comparing(Product::getCreatedAt).reversed());
        }

        // ========== PAGINATION ==========
        int totalProducts = allProducts.size();
        int totalPages = (int) Math.ceil((double) totalProducts / PAGE_SIZE);

        int currentPage = 1;
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
                if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        int startIndex = (currentPage - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalProducts);

        List<Product> products = allProducts.subList(startIndex, endIndex);

        // Get categories for filter dropdown
        List<Category> categories = categoryDao.findAllActive();

        // Set attributes
        request.setAttribute("products", products);
        request.setAttribute("categories", categories);
        request.setAttribute("currentSort", sortParam);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("pageSize", PAGE_SIZE);

        // Forward to JSP
        request.getRequestDispatcher("/WEB-INF/views/shop/shop.jsp").forward(request, response);
    }
}