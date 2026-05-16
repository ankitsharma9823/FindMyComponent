<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:if test="${not empty sessionScope.user && sessionScope.user.admin}">
    <c:redirect url="/admin/users" />
</c:if>

<!-- Dynamically load your template head and pass your parameters -->
<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="FMC Admin — Categories" />
    <jsp:param name="cssFile" value="products" />
</jsp:include>

<body>
<jsp:include page="/WEB-INF/templates/seller-nav.jsp">
    <jsp:param name="activeTab" value="product" />
</jsp:include>

<div class="dashboard-container">

    <!-- Header Controls -->
    <div class="dashboard-header">
        <div>
            <h2>Product Catalog Management</h2>
            <p class="role-badge ${isAdmin ? 'admin-badge' : 'seller-badge'}">
                Role: ${isAdmin ? 'Administrator (Global View)' : 'Seller Merchant'}
            </p>
        </div>
        <a href="${pageContext.request.contextPath}/seller/products?action=add" class="btn btn-add">
            + Add New Product
        </a>
    </div>

    <!-- System Response Success Alerts -->
    <c:if test="${not empty param.success}">
        <div class="alert alert-success">
            <span class="alert-icon">✓</span> ${param.success}
        </div>
    </c:if>

    <!-- Product Presentation Data Table -->
    <div class="table-card">
        <table class="product-table">
            <thead>
            <tr>
                <th>Image</th>
                <th>Product Details</th>
                <th>Category ID</th>
                <th>Price</th>
                <th>Stock Status</th>
                <th>Storefront Visibility</th>
                <th class="text-center">Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${empty products}">
                    <tr>
                        <td colspan="7" class="empty-state">
                            No managed products found in this account. Click "+ Add New Product" to populate items.
                        </td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="prod" items="${products}">
                        <tr>
                            <td class="img-col">
                                <c:choose>
                                    <c:when test="${not empty prod.mainImageUrl}">
                                        <img src="${pageContext.request.contextPath}${prod.mainImageUrl}" alt="Thumbnail" />
                                    </c:when>
                                    <c:otherwise>
                                        <div class="no-img-placeholder">No Image</div>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td>
                                <div class="product-name">${prod.name}</div>
                                <c:if test="${not empty prod.brand}">
                                    <span class="product-brand">${prod.brand}</span>
                                </c:if>
                                <c:if test="${isAdmin}">
                                    <div class="seller-owner-id">Seller Owner ID: ${prod.sellerId}</div>
                                </c:if>
                            </td>

                            <td><span class="badge-neutral">ID: ${prod.categoryId}</span></td>
                            <td class="price-text">$${prod.price}</td>

                            <td>
                                <c:choose>
                                    <c:when test="${prod.stockQuantity == 0}">
                                        <span class="status-pill out-of-stock">Out of Stock</span>
                                    </c:when>
                                    <c:when test="${prod.stockQuantity <= 5}">
                                        <span class="status-pill low-stock">Low Stock (${prod.stockQuantity})</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status-pill in-stock">${prod.stockQuantity} Units</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td>
                                <form action="${pageContext.request.contextPath}/seller/products?action=updateStatus" method="POST" style="display:inline;">
                                    <input type="hidden" name="id" value="${prod.id}" />
                                    <input type="hidden" name="isActive" value="${prod.active ? 'false' : 'true'}" />
                                    <button type="submit" class="status-toggle-btn ${prod.active ? 'toggle-active' : 'toggle-inactive'}">
                                            ${prod.active ? '● Active (Hide)' : '○ Paused (Show)'}
                                    </button>
                                </form>
                            </td>

                            <td class="action-cell">
                                <a href="${pageContext.request.contextPath}/seller/products?action=edit&id=${prod.id}" class="action-btn edit-link">
                                    Edit
                                </a>

                                <form action="${pageContext.request.contextPath}/seller/products?action=delete"
                                      method="POST"
                                      style="display:inline;"
                                      onsubmit="return confirm('Are you sure you want to completely drop this item profile?');">
                                    <input type="hidden" name="id" value="${prod.id}" />
                                    <button type="submit" class="action-btn delete-btn">Delete</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>
