<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="seller-navbar">
    <div class="nav-brand">
        <a href="${pageContext.request.contextPath}/seller/products">
            <span class="brand-highlight">FMC Merchant</span>
            <span>Seller Hub</span>
        </a>
    </div>

    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/seller/products"
           class="nav-item ${param.activeTab == 'product' ? 'active' : ''}">
            Product Catalog
        </a>
        <a href="${pageContext.request.contextPath}/seller/products?action=add"
           class="nav-item ${param.activeTab == 'form' ? 'active' : ''}">
            Add Inventory
        </a>
        <!-- Integrated Sales Analytics Engine Link -->
        <a href="${pageContext.request.contextPath}/seller/sales"
           class="nav-item ${param.activeTab == 'sales' ? 'active' : ''}">
            Sales Analytics
        </a>
    </div>

    <div class="nav-actions">
        <span class="nav-right">Merchant ID: ${sessionScope.user.id}</span>
        <a href="${pageContext.request.contextPath}/logout" class="nav-logout"
           onclick="return confirm('Drop merchant workspace session?');">
            Logout
        </a>
    </div>
</nav>