<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="seller-navbar">
    <div class="nav-brand">
        <c:choose>
            <c:when test="${not empty sessionScope.user && sessionScope.user.admin}">
                <a href="${pageContext.request.contextPath}/admin/users">
                    FMC <span class="brand-highlight">ADMIN</span>
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/seller/products">
                    FMC <span class="brand-highlight">SELLER</span>
                </a>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="nav-links">
        <c:choose>
            <c:when test="${not empty sessionScope.user && sessionScope.user.admin}">
                <a href="${pageContext.request.contextPath}/admin/users" class="nav-item">
                    Admin Console
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/seller/products"
                   class="nav-item ${param.activeTab == 'product' ? 'active' : ''}">
                    Product Catalog
                </a>
            </c:otherwise>
        </c:choose>
        <a href="${pageContext.request.contextPath}/shop"
           class="nav-item ${param.activeTab == 'shop' ? 'active' : ''}">
            Shop
        </a>
    </div>

    <div class="nav-actions">
        <span class="nav-right">${sessionScope.user.admin ? 'Admin Console' : 'Seller Dashboard'}</span>
        <a href="${pageContext.request.contextPath}/logout" class="nav-logout">
            Logout
        </a>
    </div>
</nav>
