<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">

<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="FMC — Order History" />
    <jsp:param name="cssFile" value="orders" />
</jsp:include>

<body>

<jsp:include page="/WEB-INF/templates/nav.jsp">
    <jsp:param name="activeTab" value="orders" />
</jsp:include>

<div class="orders-page-scoped orders-wrapper">

    <div class="page-header">
        <h1>Your Orders</h1>
        <p>Track statuses, manage processing shipments, and view order execution receipts.</p>
    </div>

    <%-- Success Feedback Banner --%>
    <c:if test="${not empty param.success}">
        <div class="alert alert-success" role="alert">
            <span>✓ <c:out value="${param.success}" /></span>
        </div>
    </c:if>

    <%-- Error Feedback Banner --%>
    <c:if test="${not empty param.error}">
        <div class="alert alert-error" role="alert">
            <span>⚠ <c:out value="${param.error}" /></span>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${empty orders}">
            <div class="empty-orders-card">
                <div class="empty-icon">📦</div>
                <h3>No orders placed yet</h3>
                <p>Your hardware procurement pipeline is empty. Explore items on the marketplace.</p>
                <a href="${pageContext.request.contextPath}/shop" class="btn-shop">Browse Component Catalog</a>
            </div>
        </c:when>

        <c:otherwise>
            <div class="orders-table-wrapper">
                <table>
                    <thead>
                    <tr>
                        <th>Order Identification</th>
                        <th>Total Amount</th>
                        <th>Payment Method</th>
                        <th>Order Status</th>
                        <th>Payment Status</th>
                        <th>Operations</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="order" items="${orders}">
                        <tr>
                            <td>
                                <a class="order-num-link" href="${pageContext.request.contextPath}/buyer/orders?action=view&id=${order.id}">
                                    <c:out value="${order.orderNumber}" />
                                </a>
                            </td>

                            <td class="price-col">
                                $<fmt:formatNumber value="${order.totalAmount}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                            </td>

                            <td>
                                <c:out value="${order.paymentMethod}" default="COD" />
                            </td>

                            <td>
                                    <span class="status-pill status-${order.orderStatus}">
                                        <c:out value="${order.orderStatus}" />
                                    </span>
                            </td>

                            <td>
                                    <span class="status-pill status-${order.paymentStatus}">
                                        <c:out value="${order.paymentStatus}" />
                                    </span>
                            </td>

                            <td class="actions-cell">
                                <a href="${pageContext.request.contextPath}/buyer/orders?action=view&id=${order.id}">
                                    View Order Details
                                </a>

                                <c:if test="${order.orderStatus eq 'pending'}">
                                    <form action="${pageContext.request.contextPath}/buyer/orders" method="POST" class="inline-cancel-form" onsubmit="return confirm('Are you sure you want to cancel order ${order.orderNumber}?');">
                                        <input type="hidden" name="action" value="cancel" />
                                        <input type="hidden" name="id" value="${order.id}" />
                                        <button type="submit" class="btn-action-cancel">Cancel Order</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>

</div>

</body>
</html>