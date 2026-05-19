<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- Core parameters to keep navbar states fluid --%>
<c:set var="shopParams" value="?category=${param.category}&search=${param.search}&sort=${param.sort}&page=${param.page}" />

<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="FMC Components — My Account" />
    <jsp:param name="cssFile" value="account" />
</jsp:include>

<body>

<jsp:include page="/WEB-INF/templates/nav.jsp">
    <jsp:param name="activeTab" value="account" />
</jsp:include>

<div class="account-page-scoped account-wrapper">

    <!-- Header Section -->
    <div class="dashboard-header">
        <div>
            <h2>Account Profile</h2>
            <p class="role-badge">Manage your personal details and contact information</p>
        </div>
    </div>

    <!-- Alert Banners for Controller Feedback (Using the exact param names from your Servlet) -->
    <c:if test="${not empty param.success}">
        <div class="alert-banner alert-success">
            <span class="alert-icon">✓</span>
            <p><c:out value="${param.success}"/></p>
        </div>
    </c:if>

    <c:if test="${not empty param.error}">
        <div class="alert-banner alert-error">
            <span class="alert-icon">✕</span>
            <p><c:out value="${param.error}"/></p>
        </div>
    </c:if>

    <!-- Main Workspace Layout Split -->
    <div class="profile-layout-grid">

        <!-- Left Column: Read-Only System Identity -->
        <div class="profile-card identity-card">
            <div class="avatar-placeholder-ring">
                <span>
                    <c:out value="${not empty user.firstName ? user.firstName.substring(0,1) : 'U'}"/><c:out value="${not empty user.lastName ? user.lastName.substring(0,1) : ''}"/>
                </span>
            </div>
            <h3 class="identity-name"><c:out value="${user.firstName} ${user.lastName}"/></h3>
            <p class="identity-username">@<c:out value="${user.username}"/></p>

            <hr class="card-divider" />

            <div class="system-meta-row">
                <span class="meta-label">Primary Email</span>
                <span class="meta-value"><c:out value="${user.email}"/></span>
            </div>
        </div>

        <!-- Right Column: Editable Account Details Form -->
        <div class="profile-card form-card">
            <div class="card-header-block">
                <h3>Edit Profile Information</h3>
                <p>Modify details linked to your delivery and purchasing pipeline.</p>
            </div>

            <!-- Mapped exactly to your servlet's URL pattern -->
            <form action="${pageContext.request.contextPath}/buyer/account" method="POST" class="profile-update-form">

                <div class="form-row-split">
                    <div class="form-control-group">
                        <label for="firstName-input">First Name</label>
                        <!-- Using fields matching your servlet: request.getParameter("firstName") -->
                        <input type="text" id="firstName-input" name="firstName"
                               value="<c:out value='${user.firstName}'/>" required />
                    </div>

                    <div class="form-control-group">
                        <label for="lastName-input">Last Name</label>
                        <!-- Using fields matching your servlet: request.getParameter("lastName") -->
                        <input type="text" id="lastName-input" name="lastName"
                               value="<c:out value='${user.lastName}'/>" required />
                    </div>
                </div>

                <div class="form-control-group">
                    <label for="phone-input">Phone Number</label>
                    <!-- Using fields matching your servlet: request.getParameter("phone") -->
                    <input type="tel" id="phone-input" name="phone"
                           value="<c:out value='${user.phone}'/>" placeholder="e.g. +977 98XXXXXXXX" />
                </div>

                <hr class="card-divider" />

                <!-- Explicitly Immutable Fields for Visual Clarity (No name attributes so they aren't submitted) -->
                <div class="disabled-fields-preview">
                    <div class="form-control-group fully-disabled">
                        <label>Username (Immutable)</label>
                        <input type="text" value="<c:out value='${user.username}'/>" disabled />
                    </div>

                    <div class="form-control-group fully-disabled">
                        <label>Email Address (Locked)</label>
                        <input type="text" value="<c:out value='${user.email}'/>" disabled />
                    </div>
                </div>

                <div class="form-actions-row">
                    <button type="submit" class="btn-save-profile">Save Changes</button>
                    <a href="${pageContext.request.contextPath}/shop${shopParams}" class="btn-cancel-view">Return to Market</a>
                </div>

            </form>
        </div>

    </div>

</div>
</body>
</html>