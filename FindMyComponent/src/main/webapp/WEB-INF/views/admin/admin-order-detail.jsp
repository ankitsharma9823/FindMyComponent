<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="FMC Admin — Order File" />
    <jsp:param name="cssFile" value="admin-order-detail" />
</jsp:include>

<body>
<div class="admin-layout">
    <jsp:include page="/WEB-INF/templates/admin-sidebar.jsp">
        <jsp:param name="active" value="orders" />
    </jsp:include>

    <main class="admin-main-content">
        <div class="admin-header">
            <img src="${pageContext.request.contextPath}/static/images/logo.png" alt="FMC Logo" />
            <h1>Order Inspection Terminal</h1>
        </div>

        <div class="back-link-wrapper">
            <a href="${pageContext.request.contextPath}/admin/orders" class="back-queue-link">&larr; Return to Transaction Queue</a>
        </div>

        <header class="detail-header">
            <div>
                <h2>Record File: <c:out value="${order.orderNumber}"/></h2>
                <p>Purchasing User Account: Reference ID #${order.userId}</p>
            </div>
            <div>
                <span class="status-pill ${order.orderStatus}">System State: <c:out value="${order.orderStatus}"/></span>
            </div>
        </header>

        <div class="detail-grid">
            <!-- Left Panel Column: Item Line Distribution -->
            <div>
                <h3 class="section-title">Manifest Items</h3>
                <div class="items-display-card">
                    <c:forEach var="item" items="${orderItems}">
                        <div class="order-item-row">
                            <div>
                                <h4><c:out value="${item.productName}"/></h4>
                                <small>Component Inventory Reference: #${item.productId} | Vendor ID: #${item.sellerId}</small>
                            </div>
                            <div>
                                <span class="item-quantity-display">x${item.quantity}</span>
                                <span class="item-subtotal-val">
                                    $<fmt:formatNumber value="${item.subtotal}" minFractionDigits="2" maxFractionDigits="2"/>
                                </span>
                            </div>
                        </div>
                    </c:forEach>

                    <div class="total-receivable-banner">
                        <span>Aggregate Amount Documented</span>
                        <span class="price-color">
                            $<fmt:formatNumber value="${order.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/>
                        </span>
                    </div>
                </div>
            </div>

            <!-- Right Panel Column: Delivery Specs & Pipeline Updates -->
            <div>
                <h3 class="section-title">Shipping Allocation</h3>
                <div class="logistics-card">
                    <p><c:out value="${order.shippingAddress}"/></p>
                </div>

                <div class="actions-control-card">
                    <h3 class="section-title">Workflow Operations</h3>
                    <form action="${pageContext.request.contextPath}/admin/orders" method="POST" class="workflow-form">
                        <input type="hidden" name="id" value="${order.id}" />

                        <!-- Pending Processing Decisions -->
                        <c:if test="${order.orderStatus eq 'pending'}">
                            <button type="submit" name="action" value="accept" class="btn-action-base approve">
                                Approve & Commit Order
                            </button>
                            <button type="submit" name="action" value="reject" class="btn-action-base reject" onclick="return confirm('Reject this transaction? This action is irreversible.')">
                                Reject Order File
                            </button>
                        </c:if>

                        <!-- Production Dispatch Step -->
                        <c:if test="${order.orderStatus eq 'accepted'}">
                            <button type="submit" name="action" value="dispatch" class="btn-action-base ship">
                                Release to Cargo / Dispatch
                            </button>
                        </c:if>

                        <!-- Stock Restoration Failure Rollbacks -->
                        <c:if test="${order.orderStatus eq 'accepted' || order.orderStatus eq 'dispatched'}">
                            <button type="submit" name="action" value="cancel" class="btn-action-base revert" onclick="return confirm('Cancel this transaction and return allocation metrics back to active store counters?')">
                                Void Order & Revert Stock
                            </button>
                        </c:if>

                        <!-- Inactive State Information -->
                        <c:if test="${order.orderStatus eq 'rejected' || order.orderStatus eq 'cancelled'}">
                            <p class="archived-order-msg">This ledger entry is permanently closed and immutable.</p>
                        </c:if>
                    </form>
                </div>
            </div>
        </div>
    </main>
</div>
</body>
</html>