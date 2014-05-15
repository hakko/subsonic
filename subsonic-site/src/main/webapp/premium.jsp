<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%@ include file="header.jsp" %>

<body>

<a name="top"/>

<div id="container">
    <jsp:include page="menu.jsp">
        <jsp:param name="current" value="premium"/>
    </jsp:include>

    <div id="content">
        <div id="main-col">
            <h1>
                <img src="inc/img/premium_large.png" alt=""/>
                <span style="vertical-align: text-top">
                    Subsonic Premium &ndash; Only $1 per month
                </span>
            </h1>

            <h3 style="padding-top: 1em;padding-bottom: 1em">Upgrade to Subsonic Premium to enjoy these extra features:</h3>

            <div class="floatcontainer margin10-t margin10-b">
                <ul class="stars column-left">
                    <li><a href="apps.jsp">Apps</a> for Android, iPhone, Windows Phone, BlackBerry, Roku, Mac, Chrome and more*.</li>
                    <li>Video streaming.</li>
                    <li>Your personal server address: <em>yourname</em>.subsonic.org</li>
                    <li>No ads in the web interface.</li>
                </ul>
                <ul class="stars column-right">
                    <li>Podcast receiver.</li>
                    <li>Play your media on compatible DLNA/UPnP devices.</li>
                    <li>Share your media on Facebook, Twitter, Google+.</li>
                    <li>Other features to be released later.</li>
                </ul>
            </div>

            <p style="font-size:11px;">* Some apps must be purchased separately.</p>

            <p>The basic version of Subsonic is free. When you first install Subsonic, the premium features are available for 30 days so
                you can try them out before deciding to upgrade.</p>

            <table style="padding-top:1em;padding-bottom:1.7em;width:90%">
                <tr>
                    <td style="font-size:26pt;padding:20pt">1</td>
                    <td>
                        <div style="font-size:14pt">Buy</div>
                        <div style="padding-top:5pt">Select a payment option below to go to PayPal where you can pay by credit card or by using your PayPal account.</div>
                    </td>
                </tr>
                <tr>
                    <td style="font-size:26pt;padding:20pt">2</td>
                    <td>
                        <div style="font-size:14pt">Receive</div>
                        <div style="padding-top:5pt">You'll receive the license key by email within a few minutes.</div>
                    </td>
                </tr>
                <tr>
                    <td style="font-size:26pt;padding:20pt">3</td>
                    <td>
                        <div style="font-size:14pt">Register</div>
                        <div style="padding-top:5pt"><a href="getting-started.jsp#3">Register</a> the license key on your Subsonic server to unlock all the premium features.</div>
                    </td>
                </tr>

            </table>

            <div class="featureitem">
                <div class="noheading"></div>
                <div class="content">
                    <table style="padding-bottom: 1em">
                        <tr>
                            <th style="padding-bottom: 0.3em;padding-right: 3em">$1 per month</th>
                            <th style="padding-bottom: 0.3em">$1 per month</th>
                        </tr>
                        <tr>
                            <td style="vertical-align:top;padding-left: 1em; padding-right: 3em">Select this option to automatically renew your Subsonic Premium subscription every year.</td>
                            <td style="vertical-align:top">Select this option to buy a Subsonic Premium subscription for 1 - 10 years.</td>
                        </tr>
                        <tr>
                            <th style="padding-right: 3em;vertical-align: bottom">
                                <form action="https://www.paypal.com/cgi-bin/webscr" method="post">
                                    <input type="hidden" name="cmd" value="_s-xclick">
                                    <input type="hidden" name="hosted_button_id" value="SDHSJ5T5E2DC4">
                                    <input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_subscribeCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
                                    <input type="hidden" name="lc" value="US">
                                    <img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1">
                                </form>
                            </th>
                            <td style="padding-right: 3em">
                                <form action="https://www.paypal.com/cgi-bin/webscr" method="post" target="_top">
                                    <input type="hidden" name="cmd" value="_s-xclick">
                                    <input type="hidden" name="hosted_button_id" value="TNL7NC2HVY5E6">
                                    <input type="hidden" name="on0" value="Duration">
                                    <input type="hidden" name="currency_code" value="USD">
                                    <input type="hidden" name="lc" value="US">
                                    <table>
                                        <tr><td><select name="os0">
                                            <option value="1 year"> 1 year of Subsonic Premium &ndash; $12</option>
                                            <option value="2 years">2 years of Subsonic Premium &ndash; $24</option>
                                            <option value="3 years">3 years of Subsonic Premium &ndash; $36</option>
                                            <option value="4 years">4 years of Subsonic Premium &ndash; $48</option>
                                            <option value="5 years">5 years of Subsonic Premium &ndash; $60</option>
                                            <option value="6 years">6 years of Subsonic Premium &ndash; $72</option>
                                            <option value="7 years">7 years of Subsonic Premium &ndash; $84</option>
                                            <option value="8 years">8 years of Subsonic Premium &ndash; $96</option>
                                            <option value="9 years">9 years of Subsonic Premium &ndash; $108</option>
                                            <option value="10 years">10 years of Subsonic Premium &ndash; $120</option>
                                        </select></td></tr>
                                        <tr><th>
                                            <input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_buynowCC_LG.gif" border="0" name="submit" style="padding-top:0.5em" alt="PayPal - The safer, easier way to pay online!">
                                        </th></tr>
                                    </table>
                                    <img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1">
                                </form>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>

            <p>
                Includes free upgrades to future Subsonic releases. Note: The Subsonic Premium license is valid for personal, non-commercial use.
                For commercial use, please <a href="mailto:mail@subsonic.org">contact us</a> for licensing options.
            </p>

            <p>
                <a href="faq.jsp">Other payment alternatives</a> are also available.
            </p>

            <p>
               If you purchased a license prior to Subsonic 4.8 it will remain valid forever.
            </p>

            <p>
                If you have any questions, feel free to send an email to <a href="mailto:mail@subsonic.org">mail@subsonic.org</a>
            </p>

        </div>

        <div id="side-col">
            <%@ include file="google-translate.jsp" %>
            <%@ include file="download-subsonic.jsp" %>
        </div>

        <div class="clear">
        </div>
    </div>
    <hr/>
    <%@ include file="footer.jsp" %>
</div>


</body>
</html>
