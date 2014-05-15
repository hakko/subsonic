package net.sourceforge.subsonic.backend.service;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import net.sourceforge.subsonic.backend.dao.PaymentDao;
import net.sourceforge.subsonic.backend.dao.SubscriptionDao;
import net.sourceforge.subsonic.backend.domain.Payment;
import net.sourceforge.subsonic.backend.domain.ProcessingStatus;
import net.sourceforge.subsonic.backend.domain.Subscription;

/**
 * Runs a task at regular intervals, checking for incoming payments and sending
 * out license keys by email.
 *
 * @author Sindre Mehus
 * @version $Id$
 */
public class LicenseGenerator {

    private static final Logger LOG = Logger.getLogger(LicenseGenerator.class);
    private static final long DELAY = 60; // One minute.

    private PaymentDao paymentDao;
    private SubscriptionDao subscriptionDao;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public void init() {
        Runnable task = new Runnable() {
            public void run() {
                try {
                    LOG.info("Starting license generator.");
                    processPayments();
                    processSubscriptions();
                    LOG.info("Completed license generator.");
                } catch (Throwable x) {
                    LOG.error("Failed to process license emails.", x);
                }
            }
        };
        executor.scheduleWithFixedDelay(task, DELAY, DELAY, TimeUnit.SECONDS);
        LOG.info("Scheduled license generator to run every " + DELAY + " seconds.");
    }

    private void processPayments() throws Exception {
        List<Payment> payments = paymentDao.getPaymentsByProcessingStatus(ProcessingStatus.NEW);
        LOG.info(payments.size() + " new payment(s).");
        if (payments.isEmpty()) {
            return;
        }

        EmailSession emailSession = new EmailSession();
        for (Payment payment : payments) {
            try {
                processPayment(payment, emailSession);
            } catch (Throwable x) {
                LOG.error("Failed to process " + payment, x);
            }
        }
    }

    private void processSubscriptions() throws Exception {
        List<Subscription> subscriptions = subscriptionDao.getSubscriptionsByProcessingStatus(ProcessingStatus.NEW);
        LOG.info(subscriptions.size() + " new subscription(s).");
        if (subscriptions.isEmpty()) {
            return;
        }

        EmailSession emailSession = new EmailSession();
        for (Subscription subscription : subscriptions) {
            try {
                processSubscription(subscription, emailSession);
            } catch (Throwable x) {
                LOG.error("Failed to process " + subscription, x);
            }
        }
    }

    private void processPayment(Payment payment, EmailSession emailSession) {
        try {
            LOG.info("Processing " + payment);
            String email = payment.getPayerEmail();
            if (email == null) {
                throw new Exception("Missing email address.");
            }

            boolean eligible = isEligible(payment);
            boolean ignorable = isIgnorable(payment);
            if (eligible) {
                sendLicenseTo(email, emailSession);
                LOG.info("Sent license key for " + payment);
            } else {
                LOG.info("Payment not eligible for " + payment);
            }

            if (eligible || ignorable) {
                payment.setProcessingStatus(ProcessingStatus.COMPLETED);
                payment.setLastUpdated(new Date());
                paymentDao.updatePayment(payment);
            }

        } catch (Throwable x) {
            LOG.error("Failed to process " + payment, x);
        }
    }

    private void processSubscription(Subscription subscription, EmailSession emailSession) {
        try {
            LOG.info("Processing " + subscription);
            String email = subscription.getEmail();
            if (email == null) {
                throw new Exception("Missing email address.");
            }

            sendLicenseTo(email, emailSession);
            LOG.info("Sent license key for " + subscription);

            subscription.setProcessingStatus(ProcessingStatus.COMPLETED);
            subscription.setUpdated(new Date());
            subscriptionDao.updateSubscription(subscription);

        } catch (Throwable x) {
            LOG.error("Failed to process " + subscription, x);
        }
    }

    private boolean isEligible(Payment payment) {
        String status = payment.getPaymentStatus();
        if ("echeck".equalsIgnoreCase(payment.getPaymentType())) {
            return "Pending".equalsIgnoreCase(status) || "Completed".equalsIgnoreCase(status);
        }
        return "Completed".equalsIgnoreCase(status);
    }

    private boolean isIgnorable(Payment payment) {
        String status = payment.getPaymentStatus();
        return "Denied".equalsIgnoreCase(status) || 
                "Reversed".equalsIgnoreCase(status) ||
                "Refunded".equalsIgnoreCase(status);
    }

    public void sendLicenseTo(String to, EmailSession emailSession) throws MessagingException {
        emailSession.sendMessage("license@subsonic.org",
                                 Arrays.asList(to),
                                 null,
                                 Arrays.asList("license@subsonic.org", "sindre@activeobjects.no"),
                                 Arrays.asList("license@subsonic.org"),
                                 "Subsonic License",
                                 createLicenseContent(to));
        LOG.info("Sent license to " + to);
    }

    private String createLicenseContent(String to) {
        String license = md5Hex(to.toLowerCase());

        return "Dear Subsonic user,\n" +
                "\n" +
                "Many thanks for upgrading to Subsonic Premium!\n" +
                "Please find your license key below.\n" +
                "\n" +
                "Email: " + to + "\n" +
                "License: " + license + " \n" +
                "\n" +
                "To install the license key, click the \"Get Subsonic Premium\" link in the top right corner of the Subsonic web interface.\n" +
                "\n" +
                "More info here: http://subsonic.org/pages/getting-started.jsp#3\n" +
                "\n" +
                "This license is valid for personal, non-commercial of Subsonic. For commercial use, please contact us for licensing options.\n" +
                "\n" +
                "Thanks again for supporting the project!\n" +
                "\n" +
                "Best regards,\n" +
                "The Subsonic team";
    }

    /**
     * Calculates the MD5 digest and returns the value as a 32 character hex string.
     *
     * @param s Data to digest.
     * @return MD5 digest as a hex string.
     */
    private String md5Hex(String s) {
        if (s == null) {
            return null;
        }

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return new String(Hex.encodeHex(md5.digest(s.getBytes("UTF-8"))));
        } catch (Exception x) {
            throw new RuntimeException(x.getMessage(), x);
        }
    }

    public void setPaymentDao(PaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }

    public static void main(String[] args) throws Exception {
        String address = args[0];
        String license = new LicenseGenerator().md5Hex(address.toLowerCase());
        System.out.println("Email: " + address);
        System.out.println("License: " + license);

//        LicenseGenerator generator = new LicenseGenerator();
//        generator.sendLicenseTo(address, new EmailSession());
    }

    public void setSubscriptionDao(SubscriptionDao subscriptionDao) {
        this.subscriptionDao = subscriptionDao;
    }
}
