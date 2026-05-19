<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="FMC Admin — Orders" />
    <jsp:param name="cssFile" value="admin-orders" />
</jsp:include>

<body>
<div class="admin-layout">
    <jsp:include page="/WEB-INF/templates/admin-sidebar.jsp">
        <jsp:param name="active" value="orders" />
    </jsp:include>

    <main class="admin-main-content">
        <div class="admin-header">
            <img src="${pageContext.request.contextPath}/static/images/logo.png" alt="FMC Logo" />
            <h1>Order Management</h1>
        </div>

        <!-- Success & Error Alert Elements -->
        <c:if test="${not empty param.error}">
            <div class="alert-banner error">
                <span class="alert-icon">⚠</span>
                <c:out value="${param.error}"/>
            </div>
        </c:if>
        <c:if test="${not empty param.success}">
            <div class="alert-banner success">
                <span class="alert-icon">✓</span>
                <c:out value="${param.success}"/>
            </div>
        </c:if>

        <!-- Pipeline Filter Links System -->
        <div class="filter-tabs">
            <a href="${pageContext.request.contextPath}/admin/orders" class="tab-link ${empty currentStatusFilter ? 'all-active' : ''}">All Orders</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=pending" class="tab-link ${currentStatusFilter eq 'pending' ? 'pending-active' : ''}">Pending</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=accepted" class="tab-link ${currentStatusFilter eq 'accepted' ? 'accepted-active' : ''}">Accepted</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=dispatched" class="tab-link ${currentStatusFilter eq 'dispatched' ? 'dispatched-active' : ''}">Dispatched</a>
        </div>

        <div class="admin-container">
            <div class="table-header">
                <h2>Customer Market Transactions</h2>
            </div>

            <!-- Standardized Wrapper Layout Matching Main CSS Core -->
            <div class="orders-table-card">
                <table class="orders-data-table">
                    <thead>
                    <tr>
                        <th>Order Number</th>
                        <th>User ID</th>
                        <th>Total Amount</th>
                        <th>Status</th>
                        <th>Payment Tracking</th>
                        <th class="text-right">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty orders}">
                            <tr>
                                <td colspan="6" class="empty-table-state">
                                    No transaction records match the specified filter matrix.
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="ord" items="${orders}">
                                <tr>
                                    <td class="order-num-cell" data-label="Order Number"><c:out value="${ord.orderNumber}"/></td>
                                    <td data-label="User ID">#${ord.userId}</td>
                                    <td class="amount-cell" data-label="Total Amount">
                                        $<fmt:formatNumber value="${ord.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/>
                                    </td>
                                    <td data-label="Status">
                                        <span class="pipeline-status ${ord.orderStatus}">
                                            <c:out value="${ord.orderStatus}"/>
                                        </span>
                                    </td>
                                    <td class="payment-info-cell" data-label="Payment Tracking">
                                        <c:out value="${ord.paymentMethod}"/> (<c:out value="${ord.paymentStatus}"/>)
                                    </td>
                                    <td class="text-right" data-label="Actions">
                                        <a href="${pageContext.request.contextPath}/admin/orders?action=view&id=${ord.id}" class="btn-manage-order">
                                            Inspect File
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>
</body>
</html>