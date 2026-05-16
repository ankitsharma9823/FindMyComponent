<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="FMC Admin — User Management" />
    <jsp:param name="cssFile" value="categories" />
</jsp:include>

<body>
<div class="admin-layout">
    <jsp:include page="/WEB-INF/templates/admin-sidebar.jsp">
        <jsp:param name="active" value="users" />
    </jsp:include>

    <main class="admin-main-content">
        <div class="admin-header">
            <img src="${pageContext.request.contextPath}/static/images/logo.png" alt="FMC Logo" />
            <h1>User Management</h1>
        </div>

        <div class="admin-container">
            <div class="table-header">
                <h2>System Users</h2>
            </div>

            <div class="data-table-wrapper">
                <table class="data-table users-table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Full Name</th>
                        <th>Email / Username</th>
                        <th>Phone</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="u" items="${users}">
                        <tr class="${!u.active ? 'row-inactive' : ''}">
                            <td class="id-cell">#${u.id}</td>
                            <td>
                                <strong><c:out value="${u.firstName} ${u.lastName}"/></strong>
                            </td>
                            <td>
                                <div class="primary-cell"><c:out value="${u.email}"/></div>
                                <div class="secondary-cell">@<c:out value="${u.username}"/></div>
                            </td>
                            <td><c:out value="${u.phone != null ? u.phone : 'N/A'}"/></td>
                            <td>
                            <span class="status-badge ${u.active ? 'active' : 'inactive'}">
                                    ${u.active ? 'Active' : 'Suspended'}
                            </span>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/users?action=edit&id=${u.id}" class="btn-edit">Manage</a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>
</body>
</html>
