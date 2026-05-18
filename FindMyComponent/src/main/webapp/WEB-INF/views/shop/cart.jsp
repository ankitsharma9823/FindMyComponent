<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="Shopping Cart — FindMyComponent" />
    <jsp:param name="cssFile" value="cart" />
</jsp:include>

<body>

<jsp:include page="/WEB-INF/templates/nav.jsp">
    <jsp:param name="activeTab" value="cart" />
</jsp:include>

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
                                <img src="${pageContext.request.contextPath}${item.productImageUrl}" alt="<c:out value='${item.productName}'/>" />
                            </div>

                            <div class="item-details">
                                <h3 class="card-title-text">
                                    <a href="${pageContext.request.contextPath}/product-details?id=${item.productId}">
                                        <c:out value="${item.productName}" />
                                    </a>
                                </h3>
                                <div class="item-unit-price">
                                    Unit Price: $<fmt:formatNumber value="${item.productPrice}" minFractionDigits="2" maxFractionDigits="2" />
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

                            <div class="item-subtotal-display">
                                <div class="subtotal-lbl">Subtotal</div>
                                <div class="subtotal-val">
                                    $<fmt:formatNumber value="${item.totalPrice}" minFractionDigits="2" maxFractionDigits="2" />
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
                        <span class="total-amount">$<fmt:formatNumber value="${cartTotal}" minFractionDigits="2" maxFractionDigits="2" /></span>
                    </div>

                    <form action="${pageContext.request.contextPath}/buyer/orders" method="POST" class="checkout-action-form" style="margin-bottom: 16px;">
                        <input type="hidden" name="action" value="place" />

                        <div class="shipping-address-box" style="margin-bottom: 16px; display: flex; flex-direction: column; gap: 6px;">
                            <label for="shippingAddress" style="font-size: 14px; font-weight: 600; color: var(--muted);">Delivery Address:</label>
                            <input type="text" id="shippingAddress" name="shippingAddress" placeholder="Enter physical street address" required
                                   style="width: 100%; height: 40px; background: var(--field); color: var(--text); border: 1px solid var(--border); border-radius: var(--radius-sm); padding: 0 12px; font-family: inherit; font-size: 14px; outline: none;" />
                        </div>

                        <button type="submit" class="btn-checkout-now">
                            Proceed to Checkout
                        </button>
                    </form>

                    <form action="${pageContext.request.contextPath}/buyer/cart" method="POST" class="clear-cart-form">
                        <input type="hidden" name="action" value="clear" />
                        <button type="submit" class="btn-clear-all">Clear All Items</button>
                    </form>
                </aside>

            </div>
        </c:otherwise>
    </c:choose>
</main>
</body>
</html>