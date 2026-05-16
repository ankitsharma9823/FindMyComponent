<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:if test="${not empty sessionScope.user && sessionScope.user.admin}">
    <c:redirect url="/admin/users" />
</c:if>

<!-- Dynamically load your template head and pass your parameters -->
<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="${isAddMode ? 'Add Product' : 'Edit Product'}" />
    <jsp:param name="cssFile" value="product-form" />
</jsp:include>

<body>
<jsp:include page="/WEB-INF/templates/seller-nav.jsp">
    <jsp:param name="activeTab" value="form" />
</jsp:include>
<div class="form-container">
    <h2>${isAddMode ? 'Add New Product' : 'Edit Existing Product'}</h2>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>
    <c:if test="${not empty param.success}">
        <div class="alert alert-success">${param.success}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/seller/products?action=save"
          method="POST"
          enctype="multipart/form-data">

        <input type="hidden" name="id" value="${isAddMode ? '' : (not empty product ? product.id : param.id)}" />

        <div class="form-group">
            <label for="name">Product Name <span class="required">*</span></label>
            <input type="text" id="name" name="name"
                   value="${not empty preservedName ? preservedName : product.name}" required />
        </div>

        <div class="form-group">
            <label for="categoryId">Category <span class="required">*</span></label>
            <select id="categoryId" name="categoryId" required>
                <option value="">-- Select a Category --</option>
                <c:forEach var="cat" items="${categories}">
                    <c:set var="currentCatId" value="${not empty preservedCategoryId ? preservedCategoryId : product.categoryId}" />
                    <option value="${cat.id}" ${currentCatId == cat.id ? 'selected' : ''}>
                            ${cat.name}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label for="brand">Brand</label>
            <input type="text" id="brand" name="brand"
                   value="${not empty preservedBrand ? preservedBrand : product.brand}" />
        </div>

        <div class="form-group">
            <label for="description">Description</label>
            <textarea id="description" name="description" rows="4">${not empty preservedDescription ? preservedDescription : product.description}</textarea>
        </div>

        <div class="form-row">
            <div class="form-group col">
                <label for="price">Price ($) <span class="required">*</span></label>
                <input type="number" id="price" name="price" step="0.01" min="0.01"
                       value="${not empty preservedPrice ? preservedPrice : product.price}" required />
            </div>

            <div class="form-group col">
                <label for="stockQuantity">Stock Quantity <span class="required">*</span></label>
                <input type="number" id="stockQuantity" name="stockQuantity" min="0" step="1"
                       value="${not empty preservedStockQuantity ? preservedStockQuantity : product.stockQuantity}" required />
            </div>
        </div>

        <div class="form-group">
            <label for="productImage">Product Image (.png only, max 5MB)</label>
            <input type="file" id="productImage" name="productImage" accept=".png" />

            <c:if test="${!isAddMode && not empty product.mainImageUrl}">
                <div class="current-image-preview">
                    <p>Current Image:</p>
                    <img src="${pageContext.request.contextPath}${product.mainImageUrl}" alt="Product Preview Image" />
                </div>
            </c:if>
        </div>

        <div class="form-group checkbox-group">
            <c:set var="isActiveChecked" value="${not empty preservedIsActive ? preservedIsActive : (not empty product ? product.active : true)}" />
            <input type="checkbox" id="isActive" name="isActive" value="true" ${isActiveChecked ? 'checked' : ''} />
            <label for="isActive">Make this product visible to buyers right away</label>
        </div>

        <div class="form-actions">
            <a href="${pageContext.request.contextPath}/seller/products" class="btn btn-secondary">Cancel</a>
            <button type="submit" class="btn btn-primary">
                ${isAddMode ? 'Create Product' : 'Save Changes'}
            </button>
        </div>

    </form>
</div>

</body>
</html>
