<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%@ include file="header.jsp" %>

<body>

<a name="top"/>

<div id="container">
    <jsp:include page="menu.jsp">
        <jsp:param name="current" value="documentation"/>
    </jsp:include>

    <div id="content">
        <div id="main-col">
            <h1 class="bottomspace">Frequently Asked Questions</h1>

            <h3>Are there other payment options than PayPal?</h3>
            <p>Yes, you can use any of the following alternatives:</p>
            <ul>
                <li>Make a payment to mail@subsonic.org on <a href="http://www.skrill.com/" target="_blank">Skrill</a>.</li>
                <li>Bitcoins are welcome on 16BzYq6HXPkCYdDaafpQWiaJqmqUfnYmFA</li>
                <li>Transfer money to our bank account; please <a href="mailto:mail@subsonic.org">contact us</a> for bank details.</li>
            </ul>
            <p>
                The price is always the same, $1 per month, so if you pay for instance the equivalent of $12 your license
                will be valid for 12 months.
            </p>

            <h3>How long does it take to get the license after I have made the payment?</h3>
            <p>Normally no more than ten minutes.  If it should take longer, please <a href="mailto:mail@subsonic.org">take contact</a>,
                but please check your spam filter first.</p>

            <h3>How do I install the license key I received by email?</h3>
            <p>Please follow the guide in the <a href="getting-started.jsp#3">Getting Started</a> documentation.</p>

            <h3>I can't access my Subsonic server from the internet or from my phone.</h3>
            <p>Please follow the guide in the <a href="getting-started.jsp#2">Getting Started</a> documentation.</p>

            <h3>I forgot my Subsonic password. Can it be retrieved?</h3>
            <p>Yes. Please read <a href="http://forum.subsonic.org/forum/viewtopic.php?t=3770">this forum post</a>.</p>

            <h3>I've lost my license key. Can I have it resent?</h3>
            <p>Yes. Please go <a href="http://subsonic.org/backend/requestLicense.view">here</a>.</p>

            <%--TODO: Network shares--%>
        </div>

        <div id="side-col">
            <%@ include file="google-translate.jsp" %>
            <%@ include file="download-subsonic.jsp" %>
            <%@ include file="premium-column.jsp" %>

        </div>

        <div class="clear">
        </div>
    </div>
    <hr/>
    <%@ include file="footer.jsp" %>
</div>


</body>
</html>
