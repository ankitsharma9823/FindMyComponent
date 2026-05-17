<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="Shopping Cart — FindMyComponent" />
    <jsp:param name="cssFile" value="cart" />
</jsp:include>

<body class="shop-page-scoped">

<jsp:include page="/WEB-INF/templates/nav.jsp" />

<main class="cart-main-container">

    <header class="dashboard-header">
        <div>
            <h2>Shopping Cart</h2>
            <p class="role-badge">Secure Transaction</p>
        </div>
        <div class="market-count-badge">
            <strong>${itemCount}</strong> <span>Items Selected</span>
        </div>
    </header>

    <c:choose>
        <c:when test="${empty cartItems}">
            <div class="empty-marketplace-state">
                <h3>Your cart is empty</h3>
                <p>Add components from the marketplace to see them here.</p>
                <a href="${pageContext.request.contextPath}/shop" class="btn-search-trigger">Browse Marketplace</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="cart-layout">

                <section class="cart-items-panel">
                    <c:forEach var="item" items="${cartItems}">
                        <div class="cart-item-row">
                            <div class="cart-item-image">
                                <img src="${pageContext.request.contextPath}${item.productImageUrl}" alt="${item.productName}" />
                            </div>

                            <div class="item-details">
                                <h3 class="card-title-text">
                                    <a href="${pageContext.request.contextPath}/product-details?id=${item.productId}">
                                        <c:out value="${item.productName}" />
                                    </a>
                                </h3>
                                <div class="item-unit-price">
                                    Unit Price: $<fmt:formatNumber value="${item.productPrice}" minFractionDigits="2" />
                                </div>

                                <div class="cart-item-actions">
                                    <form action="${pageContext.request.contextPath}/buyer/cart" method="POST" class="qty-update-form">
                                        <input type="hidden" name="action" value="update" />
                                        <input type="hidden" name="productId" value="${item.productId}" />
                                        <input type="number" name="quantity" value="${item.quantity}" min="1" class="qty-input-field">
                                        <button type="submit" class="btn-update-qty">Update</button>
                                    </form>

                                    <form action="${pageContext.request.contextPath}/buyer/cart" method="POST">
                                        <input type="hidden" name="action" value="remove" />
                                        <input type="hidden" name="productId" value="${item.productId}" />
                                        <button type="submit" class="btn-remove-item">Remove</button>
                                    </form>
                                </div>
                            </div>

                            <div class="item-subtotal-display" style="text-align: right;">
                                <div style="font-size: 11px; color: var(--muted); text-transform: uppercase;">Subtotal</div>
                                <div style="font-size: 20px; font-weight: 800; color: var(--text);">
                                    $<fmt:formatNumber value="${item.totalPrice}" minFractionDigits="2" />
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </section>

                <aside class="cart-summary-card">
                    <h3 class="summary-title">Order Summary</h3>

                    <div class="summary-row">
                        <span>Items Count</span>
                        <span>${itemCount}</span>
                    </div>

                    <div class="summary-row summary-total">
                        <span>Total Payable</span>
                        <span class="total-amount">$<fmt:formatNumber value="${cartTotal}" minFractionDigits="2" /></span>
                    </div>

                    <button class="btn-checkout-now">Proceed to Checkout</button>

                    <form action="${pageContext.request.contextPath}/buyer/cart" method="POST" style="margin-top: 20px; text-align: center;">
                        <input type="hidden" name="action" value="clear" />
                        <button type="submit" style="background:none; border:none; color:var(--muted-2); text-decoration:underline; cursor:pointer; font-size:12px;">Clear All Items</button>
                    </form>
                </aside>

            </div>
        </c:otherwise>
    </c:choose>
</main>

</body>
</html>