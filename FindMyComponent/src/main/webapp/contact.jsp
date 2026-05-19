<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/templates/head.jsp">
    <jsp:param name="title" value="Contact Us — FindMyComponent" />
    <jsp:param name="cssFile" value="contact" />
</jsp:include>

<body>

<%-- Loads your core application navbar header --%>
<jsp:include page="/WEB-INF/templates/nav.jsp">
    <jsp:param name="activeTab" value="contact" />
</jsp:include>

<div class="contact-page-scoped contact-wrapper">

    <div class="contact-hero">
        <h1>Contact Support & Operations</h1>
        <p class="hero-subtitle">Have questions about an hardware order, components metrics, or vendor onboarding? Reach out below.</p>
    </div>

    <div class="contact-grid">

        <div class="contact-card form-panel">
            <h2>Send Us a Message</h2>
            <p class="panel-desc">Fill out the fields below and our systems help desk will follow up via email.</p>

            <form action="${pageContext.request.contextPath}/contact/submit" method="POST" class="fmc-contact-form">
                <div class="form-group">
                    <label for="userName">Full Profile Name</label>
                    <input type="text" id="userName" name="name" placeholder="e.g., Ankit Kumar" required
                           value="<c:out value='${sessionScope.user.username != null ? sessionScope.user.username : (sessionScope.buyer.username != null ? sessionScope.buyer.username : "")}' />" />
                </div>

                <div class="form-group">
                    <label for="userEmail">Active Return Email Address</label>
                    <input type="email" id="userEmail" name="email" placeholder="name@example.com" required />
                </div>

                <div class="form-group">
                    <label for="msgSubject">Inquiry Classification Category</label>
                    <select id="msgSubject" name="subject">
                        <option value="order">Order Tracking & Components Shipping</option>
                        <option value="vendor">Merchant Workspace / Store Account</option>
                        <option value="technical">Technical Platform Bug / API Error</option>
                        <option value="general">General Marketplace Feedback</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="userMessage">Detailed Description Statement</label>
                    <textarea id="userMessage" name="message" rows="5" placeholder="Describe your technical issue or general question..." required></textarea>
                </div>

                <button type="submit" class="submit-button">Submit Message Routing</button>
            </form>
        </div>

        <div class="contact-card info-panel">
            <h3>Direct Communication Nodes</h3>
            <p class="panel-desc">Alternative communication channels for platform operations</p>

            <div class="info-block">
                <h5>Wholesale Vendor Support</h5>
                <p>merchants@findmycomponent.com</p>
                <small>Response loop execution time: Sub-24 Hours</small>
            </div>

            <div class="info-block">
                <h5>Platform Hardware Lab</h5>
                <p>FindMyComponent Hub, Suite 404</p>
                <p>Industrial Tech Zone, Block-C</p>
            </div>

            <div class="info-block security-note">
                <h5>🛡️ Account Token Notice</h5>
                <p>Never send plaintext account passwords or financial payment tokens inside support email loops. Use our authenticated system dashboard settings instead.</p>
            </div>
        </div>

    </div>

</div>

</body>
</html>