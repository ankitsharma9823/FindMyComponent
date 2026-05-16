<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="FMC Admin — Categories" />
    <jsp:param name="cssFile" value="categories" />
</jsp:include>

<body>
<div class="admin-layout">
    <jsp:include page="/WEB-INF/templates/admin-sidebar.jsp">
        <jsp:param name="active" value="categories" />
    </jsp:include>

    <main class="admin-main-content">
        <div class="admin-header">
            <img src="${pageContext.request.contextPath}/static/images/logo.png" alt="FMC Logo" />
            <h1>Category Management</h1>
        </div>

        <div class="admin-container">
            <div class="table-header">
                <h2>All Categories</h2>
                <a href="${pageContext.request.contextPath}/admin/categories?action=add" class="btn-primary">+ Add New</a>
            </div>

            <%-- Error Alert Section --%>
            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    <span class="alert-icon">⚠</span>
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <div class="data-table-wrapper">
                <table class="data-table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Status</th>
                        <th class="text-right">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="cat" items="${categories}">
                        <tr class="${!cat.active ? 'row-inactive' : ''}">
                            <td class="id-cell">
                                #${cat.id}
                            </td>
                            <td><strong><c:out value="${cat.name}"/></strong></td>
                            <td class="description-cell">
                                <c:out value="${cat.description}"/>
                            </td>
                            <td>
                                        <span class="status-badge ${cat.active ? 'active' : 'inactive'}">
                                                ${cat.active ? 'Active' : 'Archived'}
                                        </span>
                            </td>
                            <td class="action-buttons text-right">
                                <a href="${pageContext.request.contextPath}/admin/categories?action=edit&id=${cat.id}" class="btn-edit">Edit</a>

                                <c:choose>
                                    <c:when test="${cat.active}">
                                        <form action="${pageContext.request.contextPath}/admin/categories?action=delete" method="post">
                                            <input type="hidden" name="id" value="${cat.id}">
                                            <button type="submit" class="btn-delete" onclick="return confirm('Archive this category?')">Archive</button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="${pageContext.request.contextPath}/admin/categories?action=activate" method="post">
                                            <input type="hidden" name="id" value="${cat.id}">
                                            <button type="submit" class="btn-activate">Restore</button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty categories}">
                        <tr>
                            <td colspan="5" class="empty-state">
                                No categories found. Click "+ Add New" to create one.
                            </td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>
</body>
</html>
