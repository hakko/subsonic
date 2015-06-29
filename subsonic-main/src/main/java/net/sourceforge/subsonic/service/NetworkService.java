/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.NATPMPRouter;
import net.sourceforge.subsonic.domain.Router;
import net.sourceforge.subsonic.domain.SBBIRouter;
import net.sourceforge.subsonic.domain.WeUPnPRouter;
import net.sourceforge.subsonic.util.StringUtil;
import net.sourceforge.subsonic.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

/**
 * Provides network-related services, including port forwarding on UPnP routers and
 * URL redirection from http://xxxx.subsonic.org.
 *
 * @author Sindre Mehus
 */
public class NetworkService {

    private static final Logger LOG = Logger.getLogger(NetworkService.class);
    private static final long PORT_FORWARDING_DELAY = 3600L;
    private static final long URL_REDIRECTION_DELAY = 2 * 3600L;

    private static final String URL_REDIRECTION_REGISTER_URL = getBackendUrl() + "/backend/redirect/register.view";
    private static final String URL_REDIRECTION_UNREGISTER_URL = getBackendUrl() + "/backend/redirect/unregister.view";
    private static final String URL_REDIRECTION_TEST_URL = getBackendUrl() + "/backend/redirect/test.view";

    private SettingsService settingsService;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
    private final PortForwardingTask portForwardingTask = new PortForwardingTask();
    private final URLRedirectionTask urlRedirectionTask = new URLRedirectionTask();
    private Future<?> portForwardingFuture;
    private Future<?> urlRedirectionFuture;

    private final Status portForwardingStatus = new Status();
    private final Status urlRedirectionStatus = new Status();
    private boolean testUrlRedirection;

    public void init() {
        initPortForwarding();
        initUrlRedirection(false);
    }

    /**
     * Configures UPnP port forwarding.
     */
    public synchronized void initPortForwarding() {
        portForwardingStatus.setText("Idle");
        if (portForwardingFuture != null) {
            portForwardingFuture.cancel(true);
        }
        portForwardingFuture = executor.scheduleWithFixedDelay(portForwardingTask, 0L, PORT_FORWARDING_DELAY, TimeUnit.SECONDS);
    }

    /**
     * Configures URL redirection.
     *
     * @param test Whether to test that the redirection works.
     */
    public synchronized void initUrlRedirection(boolean test) {
        urlRedirectionStatus.setText("Idle");
        if (urlRedirectionFuture != null) {
            urlRedirectionFuture.cancel(true);
        }
        testUrlRedirection = test;
        urlRedirectionFuture = executor.scheduleWithFixedDelay(urlRedirectionTask, 0L, URL_REDIRECTION_DELAY, TimeUnit.SECONDS);
    }

    public Status getPortForwardingStatus() {
        return portForwardingStatus;
    }

    public Status getURLRedirecionStatus() {
        return urlRedirectionStatus;
    }

    public static String getBackendUrl() {
        return "true".equals(System.getProperty("subsonic.test")) ? "http://localhost:8181" : "http://subsonic.org";
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    private class PortForwardingTask extends Task {

        @Override
        protected void execute() {

            boolean enabled = settingsService.isPortForwardingEnabled();
            portForwardingStatus.setText("Looking for router...");
            Router router = findRouter();
            if (router == null) {
                LOG.warn("No UPnP router found.");
                portForwardingStatus.setText("No router found.");
            } else {

                portForwardingStatus.setText("Router found.");

                int port = settingsService.getPort();
                int httpsPort = settingsService.getHttpsPort();

                // Create new NAT entry.
                if (enabled) {
                    try {
                        router.addPortMapping(port, port, 0);
                        String message = "Successfully forwarding port " + port;

                        if (httpsPort != 0 && httpsPort != port) {
                            router.addPortMapping(httpsPort, httpsPort, 0);
                            message += " and port " + httpsPort;
                        }
                        message += ".";

                        LOG.info(message);
                        portForwardingStatus.setText(message);
                    } catch (Throwable x) {
                        String message = "Failed to create port forwarding.";
                        LOG.warn(message, x);
                        portForwardingStatus.setText(message + " See log for details.");
                    }
                }

                // Delete NAT entry.
                else {
                    try {
                        router.deletePortMapping(port, port);
                        LOG.info("Deleted port mapping for port " + port);
                        if (httpsPort != 0 && httpsPort != port) {
                            router.deletePortMapping(httpsPort, httpsPort);
                            LOG.info("Deleted port mapping for port " + httpsPort);
                        }
                    } catch (Throwable x) {
                        LOG.warn("Failed to delete port mapping.", x);
                    }
                    portForwardingStatus.setText("Port forwarding disabled.");
                }
            }

            //  Don't do it again if disabled.
            if (!enabled && portForwardingFuture != null) {
                portForwardingFuture.cancel(false);
            }
        }

        private Router findRouter() {
            try {
                Router router = SBBIRouter.findRouter();
                if (router != null) {
                    return router;
                }
            } catch (Throwable x) {
                LOG.warn("Failed to find UPnP router using SBBI library.", x);
            }

            try {
                Router router = WeUPnPRouter.findRouter();
                if (router != null) {
                    return router;
                }
            } catch (Throwable x) {
                LOG.warn("Failed to find UPnP router using WeUPnP library.", x);
            }

            try {
                Router router = NATPMPRouter.findRouter();
                if (router != null) {
                    return router;
                }
            } catch (Throwable x) {
                LOG.warn("Failed to find NAT-PMP router.", x);
            }

            return null;
        }
    }

    private class URLRedirectionTask extends Task {

        @Override
        protected void execute() {

            boolean enable = settingsService.isUrlRedirectionEnabled();
            HttpPost request = new HttpPost(enable ? URL_REDIRECTION_REGISTER_URL : URL_REDIRECTION_UNREGISTER_URL);

            int port = settingsService.getPort();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("serverId", settingsService.getServerId()));
            params.add(new BasicNameValuePair("redirectFrom", settingsService.getUrlRedirectFrom()));
            params.add(new BasicNameValuePair("port", String.valueOf(port)));
            params.add(new BasicNameValuePair("localIp", Util.getLocalIpAddress()));
            params.add(new BasicNameValuePair("localPort", String.valueOf(port)));
            params.add(new BasicNameValuePair("contextPath", settingsService.getUrlRedirectContextPath()));
            params.add(new BasicNameValuePair("licenseHolder", settingsService.getLicenseEmail()));

            HttpClient client = new DefaultHttpClient();

            try {
                urlRedirectionStatus.setText(enable ? "Registering web address..." : "Unregistering web address...");
                request.setEntity(new UrlEncodedFormEntity(params, StringUtil.ENCODING_UTF8));

                HttpResponse response = client.execute(request);
                StatusLine status = response.getStatusLine();

                switch (status.getStatusCode()) {
                    case HttpStatus.SC_BAD_REQUEST:
                        urlRedirectionStatus.setText(EntityUtils.toString(response.getEntity()));
                        break;
                    case HttpStatus.SC_OK:
                        urlRedirectionStatus.setText(enable ? "Successfully registered web address." : "Web address disabled.");
                        break;
                    default:
                        throw new IOException(status.getStatusCode() + " " + status.getReasonPhrase());
                }

            } catch (Throwable x) {
                LOG.warn(enable ? "Failed to register web address." : "Failed to unregister web address.", x);
                urlRedirectionStatus.setText(enable ? ("Failed to register web address. " + x.getMessage() +
                        " (" + x.getClass().getSimpleName() + ")") : "Web address disabled.");
            } finally {
                client.getConnectionManager().shutdown();
            }

            // Test redirection, but only once.
            if (testUrlRedirection) {
                testUrlRedirection = false;
                testUrlRedirection();
            }

            //  Don't do it again if disabled.
            if (!enable && urlRedirectionFuture != null) {
                urlRedirectionFuture.cancel(false);
            }
        }

        private void testUrlRedirection() {

            HttpGet request = new HttpGet(URL_REDIRECTION_TEST_URL + "?redirectFrom=" + settingsService.getUrlRedirectFrom());
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 30000);

            try {
                urlRedirectionStatus.setText("Testing web address " + settingsService.getUrlRedirectFrom() + ".subsonic.org. Please wait...");
                String response = client.execute(request, new BasicResponseHandler());
                urlRedirectionStatus.setText(response);

            } catch (Throwable x) {
                LOG.warn("Failed to test web address.", x);
                urlRedirectionStatus.setText("Failed to test web address. " + x.getMessage() + " (" + x.getClass().getSimpleName() + ")");
            } finally {
                client.getConnectionManager().shutdown();
            }
        }
    }

    private abstract class Task implements Runnable {
        public void run() {
            String name = getClass().getSimpleName();
            try {
                execute();
            } catch (Throwable x) {
                LOG.error("Error executing " + name + ": " + x.getMessage(), x);
            }
        }

        protected abstract void execute();
    }

    public static class Status {

        private String text;
        private Date date;

        public void setText(String text) {
            this.text = text;
            date = new Date();
        }

        public String getText() {
            return text;
        }

        public Date getDate() {
            return date;
        }
    }
}
