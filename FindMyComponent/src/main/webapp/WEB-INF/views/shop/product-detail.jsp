<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">

<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="FMC — Product Details" />
    <jsp:param name="cssFile" value="product-detail" />
</jsp:include>

<body>
<jsp:include page="/WEB-INF/templates/nav.jsp">
    <jsp:param name="activeTab" value="cart" />
</jsp:include>

<div class="shop-page-scoped shop-wrapper">

    <div class="breadcrumb-nav-row">
        <a href="${pageContext.request.contextPath}/shop">Marketplace</a>
        <span class="divider">/</span>
        <c:out value="${category.name}" default="Components"/>
    </div>

    <div class="product-detail-grid">

        <div class="product-media-panel">
            <c:choose>
                <c:when test="${not empty product.mainImageUrl}">
                    <img src="${pageContext.request.contextPath}${product.mainImageUrl}" alt="<c:out value='${product.name}'/>" />
                </c:when>
                <c:otherwise>
                    <div class="fallback-no-img">No Image Available</div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="product-info-panel">

            <c:if test="${not empty product.brand}">
                <span class="brand-pill">
                    <c:out value="${product.brand}"/>
                </span>
            </c:if>

            <h2 class="product-title">
                <c:out value="${product.name}"/>
            </h2>

            <div class="merchant-badge-row">
                <div class="badge-icon">🏪</div>
                <div class="badge-meta">
                    <div class="badge-label">Sold Verified By:</div>
                    <div class="badge-value">
                        <c:out value="${seller.username}" default="Verified Merchant"/>
                    </div>
                </div>
            </div>

            <hr class="section-divider" />

            <div class="pricing-status-row">
                <div class="price-text">$<c:out value="${product.price}"/></div>
                <div>
                    <c:choose>
                        <c:when test="${inStock}">
                            <span class="stock-status in-stock">✓ In Stock</span>
                        </c:when>
                        <c:otherwise>
                            <span class="stock-status out-of-stock">✕ Out of Stock</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="description-container">
                <h4 class="block-heading">Item Specifications / Description</h4>
                <p class="description-text">
                    <c:out value="${product.description}" default="No hardware description provided for this component listing."/>
                </p>
            </div>

            <c:if test="${inStock}">
                <%-- FIXED ACTION ROUTE BELOW --%>
                <form action="${pageContext.request.contextPath}/buyer/cart" method="POST" class="cart-action-form">
                    <input type="hidden" name="action" value="add" />
                    <input type="hidden" name="productId" value="${product.id}" />

                    <div class="quantity-selector-row">
                        <label for="quantity-selector">Quantity:</label>
                        <div class="quantity-input-wrapper">
                            <input type="number" id="quantity-selector" name="quantity" value="1" min="1" max="${product.stockQuantity}" />
                            <span class="max-stock-indicator">(Max available: ${product.stockQuantity})</span>
                        </div>
                    </div>

                    <button type="submit" class="btn-submit-cart">
                        🛒 Add Component to Cart
                    </button>
                </form>
            </c:if>

        </div>
    </div>

</div>

</body>
</html>