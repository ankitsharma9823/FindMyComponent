<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="FMC Admin — Edit User" />
    <jsp:param name="cssFile" value="categories" />
</jsp:include>

<body>
<div class="admin-layout">
    <jsp:include page="/WEB-INF/templates/admin-sidebar.jsp">
        <jsp:param name="active" value="users" />
    </jsp:include>

    <main class="admin-main-content narrow-content">
        <div class="admin-header">
            <img src="${pageContext.request.contextPath}/static/images/logo.png" alt="FMC Logo" />
            <h1>Edit User Profile</h1>
        </div>

        <div class="admin-card">
        <form action="${pageContext.request.contextPath}/admin/users?action=update" method="post">
            <input type="hidden" name="id" value="${editUser.id}">

            <div class="profile-summary">
                <div>
                    <label>Role</label>
                    <div>${roleName}</div>
                </div>
                <div>
                    <label>Username</label>
                    <div>@${editUser.username}</div>
                </div>
            </div>

            <div class="form-group">
                <label for="firstName">First Name</label>
                <input type="text" id="firstName" name="firstName" value="<c:out value='${editUser.firstName}'/>" required>
            </div>

            <div class="form-group">
                <label for="lastName">Last Name</label>
                <input type="text" id="lastName" name="lastName" value="<c:out value='${editUser.lastName}'/>" required>
            </div>

            <div class="form-group">
                <label for="phone">Phone Number</label>
                <input type="text" id="phone" name="phone" value="<c:out value='${editUser.phone}'/>">
            </div>

            <c:choose>
                <c:when test="${isEditingSelf}">
                    <div class="alert alert-info">
                        <span>Account status cannot be modified for your own session.</span>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="form-group">
                        <label>Account Status</label>
                        <select name="isActive">
                            <option value="true" ${editUser.active ? 'selected' : ''}>ACTIVE</option>
                            <option value="false" ${!editUser.active ? 'selected' : ''}>SUSPENDED / INACTIVE</option>
                        </select>
                    </div>
                </c:otherwise>
            </c:choose>

            <div class="form-actions">
                <button type="submit" class="btn-primary">Update Profile</button>
                <a href="${pageContext.request.contextPath}/admin/users" class="btn-cancel">Cancel</a>
            </div>
        </form>
        </div>
    </main>
    </div>
</body>
</html>
