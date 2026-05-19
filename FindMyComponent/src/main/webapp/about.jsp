<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="About Us — FindMyComponent" />
    <jsp:param name="cssFile" value="about" />
</jsp:include>

<body>

<%-- Dynamic Navbar Routing Injection --%>
<jsp:include page="/WEB-INF/templates/nav.jsp">
    <jsp:param name="activeTab" value="about" />
</jsp:include>

<div class="about-page-scoped about-wrapper">

    <div class="about-hero">
        <h1>Welcome to FindMyComponent</h1>
        <p class="hero-subtitle">Your premier destination for verified micro-controllers, electrical hardware components, and processing units.</p>
    </div>

    <div class="about-grid">

        <div class="about-card main-story">
            <h2>Our Story & Objective</h2>
            <p>Founded by tech builders and engineers, <strong>FindMyComponent (FMC)</strong> was established to eliminate the constant friction of hunting down rare micro-chips, hardware sensors, and integrated electronics. We bridge the gap between large production logistics scales and individual project builders.</p>
            <p>Whether you're developing high-volume consumer gadgets or sourcing a single critical processing unit for an embedded design, our marketplace environment ensures prompt verified item allocations.</p>

            <div class="shop-features-row">
                <div class="feature-item">
                    <span class="feature-icon">⚡</span>
                    <h4>Instant Logistics</h4>
                    <p>Real-time inventory levels directly from vendor stockrooms.</p>
                </div>
                <div class="feature-item">
                    <span class="feature-icon">🛡️</span>
                    <h4>Verified Quality</h4>
                    <p>Every single listing undergoes diagnostic profile vetting loops.</p>
                </div>
            </div>
        </div>

        <div class="about-card sidebar-details">
            <h3>Shop Operations</h3>
            <p class="section-desc">FMC Marketplace global core operating parameters</p>

            <ul class="details-list">
                <li>
                    <strong>Ecosystem Status:</strong>
                    <span class="badge badge-success">Operational Online</span>
                </li>
                <li>
                    <strong>Dispatch Hours:</strong>
                    <span>Mon - Fri (08:00 AM - 06:00 PM)</span>
                </li>
                <li>
                    <strong>Support Interface:</strong>
                    <span>support@findmycomponent.com</span>
                </li>
                <li>
                    <strong>Catalog Spectrum:</strong>
                    <span>Micro-controllers, IoT Sensors, Display Modules, Passives</span>
                </li>
            </ul>

            <div class="cta-box">
                <h4>Looking for a specific chip?</h4>
                <a href="${pageContext.request.contextPath}/shop" class="cta-button">Browse Live Catalog</a>
            </div>
        </div>

    </div>

    <div class="about-card highlights-section">
        <h3>Why Engineers Choose FMC Marketplace</h3>
        <div class="highlights-grid">
            <div class="highlight-block">
                <h5>Automated Discovery</h5>
                <p>Advanced server categorization structures let you find exact hardware matches via text parsing filters instantly.</p>
            </div>
            <div class="highlight-block">
                <h5>Secure Transactions</h5>
                <p>Every session scope layer features secure structural token encryption, protecting shopping carts and order logs completely.</p>
            </div>
            <div class="highlight-block">
                <h5>Scalable Procurement</h5>
                <p>We service individual hobbyists with single-unit shipments and enterprise developers with corporate wholesale fulfillment matrix bounds.</p>
            </div>
        </div>
    </div>

</div>

</body>
</html>