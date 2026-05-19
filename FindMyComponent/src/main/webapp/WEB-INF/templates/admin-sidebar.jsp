<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<aside class="admin-sidebar">
    <div class="sidebar-brand">
        <span class="brand-glitch">FMC Admin</span>
        <span class="brand-sub">Management Panel</span>
    </div>

    <nav class="sidebar-nav">
        <ul>
            <li class="${param.active == 'users' ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/admin/users">Users Loop</a>
            </li>
            <li class="${param.active == 'categories' ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/admin/categories">Categories</a>
            </li>
            <!-- Added Link targeting the AdminOrderServlet pipeline -->
            <li class="${param.active == 'orders' ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/admin/orders">Manage Orders</a>
            </li>
        </ul>
    </nav>

    <div class="sidebar-footer">
        <a href="${pageContext.request.contextPath}/logout" class="btn-logout"
           onclick="return confirm('Are you sure you want to drop your current active session?');">Logout</a>
    </div>
</aside>