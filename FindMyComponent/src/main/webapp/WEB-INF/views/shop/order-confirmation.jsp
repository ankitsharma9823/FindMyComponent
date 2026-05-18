<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">

<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="FMC — Procurement Confirmed" />
    <jsp:param name="cssFile" value="order-confirmation" />
</jsp:include>

<body class="confirmation-body">

<div class="confirmation-page-scoped confirmation-card">
    <div class="success-icon">🎉</div>
    <h1>Procurement Initialized!</h1>
    <p>Your hardware component requisition was transmitted successfully to the corresponding verification merchants.</p>

    <div class="receipt-summary">
        <div class="receipt-row">
            <span class="receipt-label">Reference ID:</span>
            <span class="receipt-val">
                <c:out value="${sessionScope.lastOrderNumber}" default="ORD-UNKNOWN"/>
            </span>
        </div>
        <div class="receipt-row">
            <span class="receipt-label">Total Amount:</span>
            <span class="receipt-val">
                $<fmt:formatNumber value="${sessionScope.lastOrderTotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
            </span>
        </div>
        <div class="receipt-row">
            <span class="receipt-label">Payment Status:</span>
            <span class="receipt-val">COD (Pending)</span>
        </div>
    </div>

    <div class="actions-group">
        <a href="${pageContext.request.contextPath}/buyer/orders" class="btn btn-primary">Track Status</a>
        <a href="${pageContext.request.contextPath}/shop" class="btn btn-secondary">Marketplace</a>
    </div>
</div>

</body>
</html>