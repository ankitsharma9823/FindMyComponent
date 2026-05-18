<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">

<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="FMC — Order Details" />
    <jsp:param name="cssFile" value="order-detail" />
</jsp:include>

<body>

<jsp:include page="/WEB-INF/templates/nav.jsp">
    <jsp:param name="activeTab" value="orders" />
</jsp:include>

<div class="detail-page-scoped detail-wrapper">

    <div class="breadcrumb-row">
        <a href="${pageContext.request.contextPath}/shop">Marketplace</a>
        <span class="divider">/</span>
        <a href="${pageContext.request.contextPath}/buyer/orders">My Orders</a>
        <span class="divider">/</span>
        <span class="current-page">Order Details</span>
    </div>

    <div class="detail-header">
        <div>
            <h1>Order <c:out value="${order.orderNumber}"/></h1>
            <p class="meta-date">Placed on structural network records</p>
        </div>
        <div class="status-badge-group">
            <span class="status-pill status-${order.orderStatus}">
                Order: <c:out value="${order.orderStatus}"/>
            </span>
            <span class="status-pill status-${order.paymentStatus}">
                Payment: <c:out value="${order.paymentStatus}"/>
            </span>
        </div>
    </div>

    <div class="detail-grid">

        <div class="items-panel">
            <h3 class="panel-heading">Line Items Procurement</h3>

            <div class="items-list">
                <c:forEach var="item" items="${orderItems}">
                    <div class="item-card">
                        <div class="item-meta">
                            <h4 class="item-name"><c:out value="${item.productName}"/></h4>
                            <p class="item-seller">Seller Verification Node ID: #<c:out value="${item.sellerId}"/></p>
                        </div>
                        <div class="item-pricing">
                            <span class="unit-breakdown">
                                <c:out value="${item.quantity}"/> x $<fmt:formatNumber value="${item.productPrice}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                            </span>
                            <span class="item-subtotal">
                                $<fmt:formatNumber value="${item.subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                            </span>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <c:if test="${order.orderStatus eq 'pending'}">
                <div class="panel-action-footer">
                    <p>Change of plans? You can safely void this procurement order while status is pending.</p>
                    <form action="${pageContext.request.contextPath}/buyer/orders" method="POST" onsubmit="return confirm('Confirm complete cancellation of order ${order.orderNumber}?');">
                        <input type="hidden" name="action" value="cancel" />
                        <input type="hidden" name="id" value="${order.id}" />
                        <button type="submit" class="btn-cancel-order">Cancel Entire Order</button>
                    </form>
                </div>
            </c:if>
        </div>

        <div class="summary-sidebar">

            <div class="summary-block">
                <h3 class="panel-heading">Delivery Coordinates</h3>
                <p class="address-text"><c:out value="${order.shippingAddress}"/></p>
            </div>

            <div class="summary-block">
                <h3 class="panel-heading">Financial Statement</h3>
                <table class="financial-table">
                    <tr>
                        <td>Payment Mode:</td>
                        <td class="val-right"><c:out value="${order.paymentMethod}"/></td>
                    </tr>
                    <tr class="total-row">
                        <td>Total Settled:</td>
                        <td class="val-right total-amount">
                            $<fmt:formatNumber value="${order.totalAmount}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                        </td>
                    </tr>
                </table>
            </div>

        </div>
    </div>

</div>

</body>
</html>