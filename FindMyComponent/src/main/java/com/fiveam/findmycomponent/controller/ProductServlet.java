package com.fiveam.findmycomponent.controller;
import com.fiveam.findmycomponent.dao.CategoryDao;
import com.fiveam.findmycomponent.dao.CategoryDaoImpl;
import com.fiveam.findmycomponent.dao.ProductDao;
import com.fiveam.findmycomponent.dao.ProductDaoImpl;
import com.fiveam.findmycomponent.entity.Category;
import com.fiveam.findmycomponent.entity.Product;
import com.fiveam.findmycomponent.entity.Role;
import com.fiveam.findmycomponent.entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * ProductServlet - Manages products for sellers and admins
 * URL: /seller/products
 *
 * AuthFilter already ensures user is logged in and has SELLER or ADMIN role
 */
@WebServlet("/seller/products")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,  // 1 MB
        maxFileSize = 1024 * 1024 * 5,    // 5 MB
        maxRequestSize = 1024 * 1024 * 10 // 10 MB
)
public class ProductServlet extends HttpServlet {

    private static final String UPLOAD_BASE_DIR = System.getProperty("user.home")
            + File.separator + "findmycomponent-uploads"
            + File.separator + "products";

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".png");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private ProductDao productDao;
    private CategoryDao categoryDao;

    // ==================== LIFECYCLE METHODS ====================

    @Override
    public void init() throws ServletException {
        productDao = new ProductDaoImpl();
        categoryDao = new CategoryDaoImpl();

        // Create upload directory if not exists
        File uploadDir = new File(UPLOAD_BASE_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    // ==================== HTTP METHOD HANDLERS ====================

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User loggedInUser = (User) session.getAttribute("user");
        boolean isAdmin = (loggedInUser.getRoleId() == Role.ROLE_ADMIN);

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            // Show add product form
            showAddForm(request, response);

        } else if ("edit".equals(action)) {
            // Show edit product form
            showEditForm(request, response, loggedInUser, isAdmin);

        } else {
            // List products
            showProductList(request, response, loggedInUser, isAdmin);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User loggedInUser = (User) session.getAttribute("user");
        boolean isAdmin = (loggedInUser.getRoleId() == Role.ROLE_ADMIN);

        String action = request.getParameter("action");

        if ("save".equals(action)) {
            handleSave(request, response, loggedInUser, isAdmin);
        } else if ("delete".equals(action)) {
            handleDelete(request, response, loggedInUser, isAdmin);
        } else if ("updateStatus".equals(action)) {
            handleUpdateStatus(request, response, loggedInUser, isAdmin);
        } else {
            response.sendRedirect(request.getContextPath() + "/seller/products");
        }
    }

    // ==================== DISPLAY METHODS ====================

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Category> categories = categoryDao.findAllActive();
        request.setAttribute("categories", categories);
        request.setAttribute("isAddMode", true);
        request.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response,
                              User loggedInUser, boolean isAdmin)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/seller/products");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            Product product = productDao.findById(id);

            if (product == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Authorization check: admin or owner
            if (!isAdmin && product.getSellerId() != loggedInUser.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You can only edit your own products");
                return;
            }

            List<Category> categories = categoryDao.findAllActive();
            request.setAttribute("categories", categories);
            request.setAttribute("product", product);
            request.setAttribute("isAddMode", false);
            request.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/seller/products");
        }
    }

    private void showProductList(HttpServletRequest request, HttpServletResponse response,
                                 User loggedInUser, boolean isAdmin)
            throws ServletException, IOException {

        List<Product> products;
        if (isAdmin) {
            products = productDao.findAll();
        } else {
            products = productDao.findBySellerId(loggedInUser.getId());
        }
        request.setAttribute("products", products);
        request.setAttribute("isAdmin", isAdmin);
        request.getRequestDispatcher("/WEB-INF/views/seller/products.jsp").forward(request, response);
    }

    // ==================== ACTION HANDLERS ====================

    private void handleSave(HttpServletRequest request, HttpServletResponse response,
                            User loggedInUser, boolean isAdmin)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        boolean isAddMode = (idParam == null || idParam.isEmpty());

        String name = request.getParameter("name");
        String categoryIdParam = request.getParameter("categoryId");
        String brand = request.getParameter("brand");
        String description = request.getParameter("description");
        String priceParam = request.getParameter("price");
        String stockParam = request.getParameter("stockQuantity");
        String isActiveParam = request.getParameter("isActive");
        Part imagePart = request.getPart("productImage");

        // Validation
        if (name == null || name.trim().isEmpty()) {
            preserveFormDataAndForward(request, response, "Product name is required",
                    loggedInUser, isAdmin, isAddMode);
            return;
        }

        if (categoryIdParam == null || categoryIdParam.isEmpty()) {
            preserveFormDataAndForward(request, response, "Please select a category",
                    loggedInUser, isAdmin, isAddMode);
            return;
        }

        if (priceParam == null || priceParam.isEmpty()) {
            preserveFormDataAndForward(request, response, "Price is required",
                    loggedInUser, isAdmin, isAddMode);
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceParam);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            preserveFormDataAndForward(request, response, "Price must be greater than 0",
                    loggedInUser, isAdmin, isAddMode);
            return;
        }

        int stockQuantity;
        try {
            stockQuantity = Integer.parseInt(stockParam);
            if (stockQuantity < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            preserveFormDataAndForward(request, response, "Stock quantity cannot be negative",
                    loggedInUser, isAdmin, isAddMode);
            return;
        }

        int categoryId = Integer.parseInt(categoryIdParam);
        boolean isActive = "true".equals(isActiveParam);

        // Validate image
        String imageError = validateImage(imagePart);
        if (imageError != null) {
            preserveFormDataAndForward(request, response, imageError,
                    loggedInUser, isAdmin, isAddMode);
            return;
        }

        // Save image if uploaded
        String mainImageUrl = null;
        if (imagePart != null && imagePart.getSize() > 0) {
            mainImageUrl = saveImage(imagePart);
        }

        if (!isAddMode) {
            // Update existing product
            int id = Integer.parseInt(idParam);
            Product existing = productDao.findById(id);

            if (existing == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Authorization check
            if (!isAdmin && existing.getSellerId() != loggedInUser.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You can only edit your own products");
                return;
            }

            existing.setName(name);
            existing.setCategoryId(categoryId);
            existing.setBrand(brand);
            existing.setDescription(description);
            existing.setPrice(price);
            existing.setStockQuantity(stockQuantity);
            existing.setActive(isActive);

            if (mainImageUrl != null) {
                existing.setMainImageUrl(mainImageUrl);
            }

            productDao.update(existing);

        } else {
            // Add new product
            Product product = new Product(name, price, stockQuantity);
            product.setSellerId(loggedInUser.getId());
            product.setCategoryId(categoryId);
            product.setBrand(brand);
            product.setDescription(description);
            product.setMainImageUrl(mainImageUrl);
            product.setActive(isActive);

            productDao.save(product);
        }

        // Redirect on success
        response.sendRedirect(request.getContextPath() + "/seller/products?success=Product saved successfully");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response,
                              User loggedInUser, boolean isAdmin) throws IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/seller/products");
            return;
        }

        int id = Integer.parseInt(idParam);
        Product product = productDao.findById(id);

        if (product == null) {
            response.sendRedirect(request.getContextPath() + "/seller/products");
            return;
        }

        // Authorization check
        if (!isAdmin && product.getSellerId() != loggedInUser.getId()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You can only delete your own products");
            return;
        }

        productDao.delete(id);
        response.sendRedirect(request.getContextPath() + "/seller/products?success=Product deleted successfully");
    }

    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response,
                                    User loggedInUser, boolean isAdmin) throws IOException {

        String idParam = request.getParameter("id");
        String isActiveParam = request.getParameter("isActive");

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/seller/products");
            return;
        }

        int id = Integer.parseInt(idParam);
        Product product = productDao.findById(id);

        if (product == null) {
            response.sendRedirect(request.getContextPath() + "/seller/products");
            return;
        }

        // Authorization check
        if (!isAdmin && product.getSellerId() != loggedInUser.getId()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You can only modify your own products");
            return;
        }

        boolean isActive = "true".equals(isActiveParam);
        productDao.updateStatus(id, isActive);

        String status = isActive ? "activated" : "deactivated";
        response.sendRedirect(request.getContextPath() + "/seller/products?success=Product " + status + " successfully");
    }

    // ==================== IMAGE HANDLING METHODS ====================

    private String validateImage(Part imagePart) {
        if (imagePart == null || imagePart.getSize() == 0) {
            return null; // No image uploaded, not an error
        }

        if (imagePart.getSize() > MAX_FILE_SIZE) {
            return "Image size exceeds 5MB limit";
        }

        String fileName = getSubmittedFileName(imagePart);
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1) {
            return "File must have .png extension";
        }

        String extension = fileName.substring(lastDot).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return "Only PNG images are allowed";
        }

        return null; // No error
    }

    private String saveImage(Part imagePart) throws IOException {
        // Generate unique filename
        String uniqueFileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ".png";

        File uploadDir = new File(UPLOAD_BASE_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        File imageFile = new File(uploadDir, uniqueFileName);
        imagePart.write(imageFile.getAbsolutePath());

        return "/uploads/products/" + uniqueFileName;
    }

    private String getSubmittedFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1);
            }
        }
        return null;
    }

    // ==================== HELPER METHODS ====================

    private void preserveFormDataAndForward(HttpServletRequest request, HttpServletResponse response,
                                            String errorMessage, User loggedInUser,
                                            boolean isAdmin, boolean isAddMode)
            throws ServletException, IOException {

        request.setAttribute("error", errorMessage);
        request.setAttribute("isAddMode", isAddMode);

        // Get categories for dropdown
        List<Category> categories = categoryDao.findAllActive();
        request.setAttribute("categories", categories);

        // Preserve all form data so user doesn't have to re-enter
        request.setAttribute("preservedName", request.getParameter("name"));
        request.setAttribute("preservedCategoryId", request.getParameter("categoryId"));
        request.setAttribute("preservedBrand", request.getParameter("brand"));
        request.setAttribute("preservedDescription", request.getParameter("description"));
        request.setAttribute("preservedPrice", request.getParameter("price"));
        request.setAttribute("preservedStockQuantity", request.getParameter("stockQuantity"));
        request.setAttribute("preservedIsActive", request.getParameter("isActive"));

        // Forward (not redirect) to keep the request data
        request.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(request, response);
    }
}