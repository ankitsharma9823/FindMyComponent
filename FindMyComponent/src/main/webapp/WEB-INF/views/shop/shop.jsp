<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="FMC Components — Marketplace" />
    <jsp:param name="cssFile" value="shop" />
</jsp:include>

<body>

<!-- Shared Application Navigation Bar Include -->
<jsp:include page="/WEB-INF/templates/nav.jsp" />

<div class="shop-page-scoped shop-wrapper">

    <div class="dashboard-header">
        <div>
            <h2>Component Marketplace</h2>
            <p class="role-badge">Browse verified hardware listings</p>
        </div>
        <div class="market-count-badge">
            <strong>${totalProducts}</strong>
            <span>components</span>
        </div>
    </div>

    <div class="search-filter-bar">
        <form action="${pageContext.request.contextPath}/shop" method="GET" class="filter-form">

            <div class="form-control-group">
                <label for="category-select">Category</label>
                <select id="category-select" name="category" onchange="this.form.submit()">
                    <option value="">All Categories</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.id}" ${selectedCategoryId == cat.id ? 'selected' : ''}>
                                ${cat.name}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-control-group search-field">
                <label for="search-input">Search</label>
                <input type="text" id="search-input" name="search" value="${searchKeyword}" placeholder="Search components, brands..." />
            </div>

            <div class="form-control-group">
                <label for="sort-select">Sort</label>
                <select id="sort-select" name="sort" onchange="this.form.submit()">
                    <option value="newest" ${currentSort == 'newest' ? 'selected' : ''}>Newest Arrivals</option>
                    <option value="price_asc" ${currentSort == 'price_asc' ? 'selected' : ''}>Price: Low to High</option>
                    <option value="price_desc" ${currentSort == 'price_desc' ? 'selected' : ''}>Price: High to Low</option>
                </select>
            </div>

            <button type="submit" class="btn-search-trigger">Apply Filters</button>
        </form>
    </div>

    <div class="results-meta-count">
        <p>Showing <strong>${products.size()}</strong> of <strong>${totalProducts}</strong> total components available</p>
    </div>

    <div class="products-marketplace-grid">
        <c:choose>
            <c:when test="${empty products}">
                <div class="empty-marketplace-state">
                    <h3>No Components Match Your Filters</h3>
                    <p>Try clearing out your search strings or matching against alternative categorization labels.</p>
                    <a href="${pageContext.request.contextPath}/shop" class="btn-clear-filters">Reset Store View</a>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="prod" items="${products}">
                    <div class="product-showcase-card">

                        <div class="card-img-wrapper">
                            <c:choose>
                                <c:when test="${not empty prod.mainImageUrl}">
                                    <img src="${pageContext.request.contextPath}${prod.mainImageUrl}" alt="${prod.name}" loading="lazy" />
                                </c:when>
                                <c:otherwise>
                                    <div class="no-img-card-fallback">Component Image</div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="card-content">
                            <c:if test="${not empty prod.brand}">
                                <span class="card-brand-pill">${prod.brand}</span>
                            </c:if>
                            <h3 class="card-title-text">${prod.name}</h3>
                            <div class="card-pricing-block">$${prod.price}</div>

                            <div class="card-stock-row">
                                <c:choose>
                                    <c:when test="${prod.stockQuantity == 0}">
                                        <span class="status-pill out-of-stock">Out of Stock</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status-pill in-stock">${prod.stockQuantity} Units</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <div class="card-footer-actions">
                            <a href="${pageContext.request.contextPath}/product-details?id=${prod.id}" class="btn-view-spec">
                                View Specs
                            </a>
                        </div>

                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

    <c:if test="${totalPages > 1}">
        <div class="pagination-navigation-row">

            <c:choose>
                <c:when test="${currentPage > 1}">
                    <a href="${pageContext.request.contextPath}/shop?category=${param.category}&search=${param.search}&sort=${param.sort}&page=${currentPage - 1}" class="pag-btn">
                        &laquo; Prev
                    </a>
                </c:when>
                <c:otherwise>
                    <span class="pag-btn disabled">&laquo; Prev</span>
                </c:otherwise>
            </c:choose>

            <div class="page-numbers-group">
                <c:forEach var="i" begin="1" end="${totalPages}">
                    <a href="${pageContext.request.contextPath}/shop?category=${param.category}&search=${param.search}&sort=${param.sort}&page=${i}" class="pag-num ${currentPage == i ? 'active-page' : ''}">
                            ${i}
                    </a>
                </c:forEach>
            </div>

            <c:choose>
                <c:when test="${currentPage < totalPages}">
                    <a href="${pageContext.request.contextPath}/shop?category=${param.category}&search=${param.search}&sort=${param.sort}&page=${currentPage + 1}" class="pag-btn">
                        Next &raquo;
                    </a>
                </c:when>
                <c:otherwise>
                    <span class="pag-btn disabled">Next &raquo;</span>
                </c:otherwise>
            </c:choose>

        </div>
    </c:if>

</div>

</body>
</html>
