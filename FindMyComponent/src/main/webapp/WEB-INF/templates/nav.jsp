<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="main-navbar">
    <div class="nav-container">
        <a href="${pageContext.request.contextPath}/shop" class="nav-logo">
            <span class="nav-logo-mark">FMC</span>
            <span>FindMyComponent</span>
        </a>

        <ul class="main-nav-links">
            <li>
                <a href="${pageContext.request.contextPath}/shop" class="main-nav-item active">Browse Shop</a>
            </li>

            <c:choose>
                <c:when test="${not empty sessionScope.user && sessionScope.user.admin}">
                    <li>
                        <a href="${pageContext.request.contextPath}/admin/users" class="main-nav-item">Admin Console</a>
                    </li>
                </c:when>
                <c:when test="${not empty sessionScope.user && sessionScope.user.seller}">
                    <li>
                        <a href="${pageContext.request.contextPath}/seller/products" class="main-nav-item">Seller Dashboard</a>
                    </li>
                </c:when>
            </c:choose>

            <li>
                <a href="${pageContext.request.contextPath}/cart" class="main-nav-item">Cart</a>
            </li>
        </ul>

        <div class="main-nav-actions">
            <c:choose>
                <c:when test="${not empty sessionScope.user}">
                    <span class="nav-welcome">
                        <c:out value="${sessionScope.user.fullName}" />
                    </span>
                    <a href="${pageContext.request.contextPath}/logout" class="main-nav-logout">Logout</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" class="main-nav-login">Sign In</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>
