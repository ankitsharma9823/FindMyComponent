<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<aside class="admin-sidebar">
    <div class="sidebar-brand">
        <span class="brand-glitch">FMC</span>
        <span class="brand-sub">Admin Console</span>
    </div>

    <nav class="sidebar-nav" aria-label="Admin navigation">
        <ul>
            <li class="${param.active == 'users' ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/admin/users">Users</a>
            </li>
            <li class="${param.active == 'categories' ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/admin/categories">Categories</a>
            </li>
            <li class="${param.active == 'shop' ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/shop">Shop</a>
            </li>
        </ul>
    </nav>

    <div class="sidebar-footer">
        <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
    </div>
</aside>
