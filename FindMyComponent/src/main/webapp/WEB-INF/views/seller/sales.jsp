<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="FMC Analytics — Vendor Sales" />
    <jsp:param name="cssFile" value="sales" />
</jsp:include>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<body>

<jsp:include page="/WEB-INF/templates/seller-nav.jsp">
    <jsp:param name="activeTab" value="sales" />
</jsp:include>

<div class="sales-page-scoped sales-wrapper">

    <div class="dashboard-header">
        <div>
            <h2>Sales Performance Engine</h2>
            <p class="role-badge">Real-time metrics, product performance analysis, and volume metrics</p>
        </div>
    </div>

    <div class="metrics-grid">
        <div class="metric-card">
            <span class="metric-label">Gross Revenue</span>
            <strong class="metric-value text-success">$<c:out value="${totalSales}"/></strong>
            <div class="metric-footer">Total accumulated sales value</div>
        </div>

        <div class="metric-card">
            <span class="metric-label">Total Volume</span>
            <strong class="metric-value"><c:out value="${totalOrders}"/> Orders</strong>
            <div class="metric-footer">Unique user acquisitions</div>
        </div>

        <div class="metric-card">
            <span class="metric-label">Units Dispatched</span>
            <strong class="metric-value"><c:out value="${totalItemsSold}"/> Parts</strong>
            <div class="metric-footer">Hardware components sold</div>
        </div>

        <div class="metric-card target-pending">
            <span class="metric-label">Fulfillment Queue</span>
            <strong class="metric-value text-warn"><c:out value="${pendingOrders}"/> Pending</strong>
            <div class="metric-footer">Requires processing action</div>
        </div>
    </div>

    <div class="analytics-layout-split">

        <div class="data-card chart-panel">
            <h3>Revenue Distribution</h3>
            <p class="section-desc">Visualizing structural revenue split across catalog items</p>
            <div class="chart-container" style="position: relative; height:300px; width:100%">
                <canvas id="revenuePieChart"></canvas>
            </div>
        </div>

        <div class="data-card leaderboard-panel">
            <h3>Top Products Table</h3>
            <p class="section-desc">Products ranked by volume sales throughput</p>

            <c:choose>
                <c:when test="${empty topProducts}">
                    <div class="empty-state-message">No component units sold yet.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive-wrapper">
                        <table class="sales-matrix-table hover-effects">
                            <thead>
                            <tr>
                                <th>Rank</th>
                                <th>Component</th>
                                <th class="text-center">Units Sold</th>
                                <th class="text-right">Revenue</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="prod" items="${topProducts}" varStatus="status">
                                <tr>
                                    <td><span class="rank-badge">${status.count}</span></td>
                                    <td><strong><c:out value="${prod.productName}"/></strong></td>
                                    <td class="text-center font-semibold"><c:out value="${prod.quantitySold}"/></td>
                                    <td class="text-right font-bold text-success">$<c:out value="${prod.totalRevenue}"/></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

    </div>

    <div class="data-card full-width-matrix-card">
        <h3>Sales by Product Table</h3>
        <p class="section-desc">Comprehensive granular log of all product items with active history</p>

        <c:choose>
            <c:when test="${empty salesByProduct}">
                <div class="empty-state-message">
                    <p>No active sales registered for your inventory items yet.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive-wrapper">
                    <table class="sales-matrix-table hover-effects">
                        <thead>
                        <tr>
                            <th>Catalog ID</th>
                            <th>Component Description Name</th>
                            <th class="text-center">Total Volume Dispatched</th>
                            <th class="text-right">Accumulated Yield Value</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="row" items="${salesByProduct}">
                            <tr>
                                <td><span class="mono-id">#<c:out value="${row.productId}"/></span></td>
                                <td><strong class="matrix-product-title"><c:out value="${row.productName}"/></strong></td>
                                <td class="text-center font-semibold"><c:out value="${row.quantitySold}"/></td>
                                <td class="text-right font-bold text-primary">$<c:out value="${row.totalRevenue}"/></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

</div>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const labelsData = [
            <c:forEach var="name" items="${productNames}" varStatus="status">
            "<c:out value='${name}'/>"${!status.last ? ',' : ''}
            </c:forEach>
        ];

        const revenuesData = [
            <c:forEach var="rev" items="${productRevenues}" varStatus="status">
            <c:out value='${rev}'/>${!status.last ? ',' : ''}
            </c:forEach>
        ];

        if (labelsData.length === 0) {
            document.getElementById('revenuePieChart').style.display = 'none';
            document.querySelector('.chart-panel').innerHTML += '<div class="empty-state-message">No sales data available to chart</div>';
            return;
        }

        const ctx = document.getElementById('revenuePieChart').getContext('2d');
        new Chart(ctx, {
            type: 'pie',
            data: {
                labels: labelsData,
                datasets: [{
                    data: revenuesData,
                    backgroundColor: ['#2563eb', '#10b981', '#f59e0b', '#ec4899', '#8b5cf6', '#64748b'],
                    borderWidth: 1,
                    borderColor: '#ffffff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: { boxWidth: 12, font: { size: 12 }, color: '#334155' }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return ' ' + context.label + ': $' + (context.raw || 0).toFixed(2);
                            }
                        }
                    }
                }
            }
        });
    });
</script>

</body>
</html>