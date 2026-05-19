<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="shopParams" value="?category=${param.category}&search=${param.search}&sort=${param.sort}&page=${param.page}" />

<nav class="seller-navbar">
    <div class="nav-brand">
        <a href="${pageContext.request.contextPath}/shop${shopParams}">
            <span class="brand-highlight">FMC</span>
            <span>FindMyComponent</span>
        </a>
    </div>

    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/shop${shopParams}"
           class="nav-item ${empty param.activeTab || param.activeTab == 'marketplace' ? 'active' : ''}">
            Marketplace
        </a>

        <%-- MULTI-LAYER SESSION CHECK --%>
        <%-- Checks both common session variable names to guarantee it catches your login state --%>
        <c:if test="${not empty sessionScope.user || not empty sessionScope.buyer || not empty sessionScope.account}">
            <a href="${pageContext.request.contextPath}/buyer/cart"
               class="nav-item ${param.activeTab == 'cart' ? 'active' : ''}">
                Cart
            </a>
            <a href="${pageContext.request.contextPath}/buyer/orders"
               class="nav-item ${param.activeTab == 'orders' ? 'active' : ''}">
                My Orders
            </a>
            <a href="${pageContext.request.contextPath}/buyer/account"
               class="nav-item ${param.activeTab == 'account' ? 'active' : ''}">
                My Account
            </a>
        </c:if>
    </div>

    <div class="nav-actions">
        <c:choose>
            <%-- Match the exact same logic for the greeting block --%>
            <c:when test="${not empty sessionScope.user || not empty sessionScope.buyer || not empty sessionScope.account}">
                <span class="nav-right">
                    Hi, <c:out value="${sessionScope.user.username != null ? sessionScope.user.username : (sessionScope.buyer.username != null ? sessionScope.buyer.username : 'User')}" />
                </span>
                <a href="${pageContext.request.contextPath}/logout" class="nav-logout">
                    Logout
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login" class="nav-right">
                    Sign In
                </a>
                <a href="${pageContext.request.contextPath}/register" class="nav-logout">
                    Register
                </a>
            </c:otherwise>
        </c:choose>
    </div>
</nav>