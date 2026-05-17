<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="${product.name} — FindMyComponent" />
    <jsp:param name="cssFile" value="product-detail" />
</jsp:include>

<body>

<!-- Shared Public Navigation Bar Include -->
<jsp:include page="/WEB-INF/templates/nav.jsp" />

<div class="detail-page-scoped detail-wrapper">

    <!-- Breadcrumbs Navigation Trail -->
    <nav class="breadcrumb">
        <a href="${pageContext.request.contextPath}/shop">Marketplace</a>
        <span class="divider">/</span>
        <span class="current-cat">${not empty category ? category.name : 'Uncategorized'}</span>
    </nav>

    <div class="product-layout">

        <!-- Left Side: Image Presentation Box -->
        <div class="image-gallery-card">
            <c:choose>
                <c:when test="${not empty product.mainImageUrl}">
                    <img class="main-display-img" src="${pageContext.request.contextPath}${product.mainImageUrl}" alt="${product.name}" />
                </c:when>
                <c:otherwise>
                    <div class="no-img-big">No High-Res Image Available</div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Right Side: Purchasing Info & Specifications Details -->
        <div class="product-purchase-details">

            <!-- Brand Badge -->
            <c:if test="${not empty product.brand}">
                <span class="brand-tag">${product.brand}</span>
            </c:if>

            <!-- Product Title Header -->
            <h1 class="item-title">${product.name}</h1>

            <!-- Integrated Seller Info Badge -->
            <div class="merchant-badge">
                <span class="merchant-icon">🏪</span>
                <div class="merchant-info">
                    <span class="lbl">Sold Verified By:</span>
                    <span class="name">
                        <c:out value="${seller.firstName} ${seller.lastName}" />
                    </span>
                </div>
            </div>

            <hr class="section-divider" />

            <!-- Price and Availability Matrix -->
            <div class="pricing-container">
                <div class="price-block">$${product.price}</div>
                <div>
                    <c:choose>
                        <c:when test="${inStock}">
                            <span class="badge badge-instock">✓ In Stock</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge badge-outstock">𐄂 Out of Stock</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Technical Specification Description Text Block -->
            <div class="description-container">
                <h3>Item Specifications / Description</h3>
                <p class="description-text">
                    <c:choose>
                        <c:when test="${not empty product.description}">
                            <c:out value="${product.description}" />
                        </c:when>
                        <c:otherwise>
                            <em>The seller provided no written descriptive profiles for this component asset entry.</em>
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>

            <!-- Transaction / Add to Cart Actions Block -->
            <div class="purchase-action-panel">
                <c:choose>
                    <c:when test="${inStock}">
                        <!-- Form action routes to Cart controller endpoint -->
                        <form action="${pageContext.request.contextPath}/buyer/cart?action=add" method="POST">
                            <input type="hidden" name="productId" value="${product.id}" />

                            <div class="quantity-selector">
                                <label for="quantity">Quantity:</label>
                                <input type="number" id="quantity" name="quantity" value="1" min="1" max="${product.stockQuantity}" />
                                <span class="max-stock-hint">(Max available: ${product.stockQuantity})</span>
                            </div>

                            <button type="submit" class="btn btn-checkout-cart">
                                🛒 Add Component to Cart
                            </button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <button class="btn btn-disabled" disabled>
                            Backorder / Out of Stock
                        </button>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
    </div>
</div>

</body>
</html>