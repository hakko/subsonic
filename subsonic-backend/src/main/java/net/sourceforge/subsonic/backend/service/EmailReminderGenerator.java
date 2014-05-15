/*
 * This file is part of Subsonic.
 *
 * Subsonic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Subsonic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2013 (C) Sindre Mehus
 */

package net.sourceforge.subsonic.backend.service;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import net.sourceforge.subsonic.backend.dao.PaymentDao;
import net.sourceforge.subsonic.backend.dao.SubscriptionDao;
import net.sourceforge.subsonic.backend.domain.Payment;
import net.sourceforge.subsonic.backend.domain.Subscription;

/**
 * Runs a daily task sending reminder emails for subscriptions that are about to expire.
 *
 * @author Sindre Mehus
 * @version $Id$
 */
public class EmailReminderGenerator {

    private static final Logger LOG = Logger.getLogger(EmailReminderGenerator.class);
    private static final long DELAY_HOURS = 24;

    private PaymentDao paymentDao;
    private SubscriptionDao subscriptionDao;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);

    public void init() {
        Runnable task = new Runnable() {
            public void run() {
                try {
                    LOG.info("Starting email reminder generator.");
                    EmailSession emailSession = new EmailSession();
                    Calendar cal = Calendar.getInstance();

                    cal.add(Calendar.DATE, 7);
                    Date in7Days = cal.getTime();
                    cal.add(Calendar.DATE, 1);
                    Date in8Days = cal.getTime();
                    cal.add(Calendar.DATE, 6);
                    Date in14Days = cal.getTime();
                    cal.add(Calendar.DATE, 1);
                    Date in15Days = cal.getTime();

                    LOG.info("In 7 days: " + in7Days);
                    LOG.info("In 8 days: " + in8Days);
                    LOG.info("In 14 days: " + in14Days);
                    LOG.info("In 15 days: " + in15Days);

                    processPayments(emailSession, in7Days, in8Days);
                    processPayments(emailSession, in14Days, in15Days);
                    processSubscriptions(emailSession, in7Days, in8Days);
                    processSubscriptions(emailSession, in14Days, in15Days);

                    LOG.info("Completed email reminder generator.");
                } catch (Throwable x) {
                    LOG.error("Failed to process reminder emails.", x);
                }
            }
        };

        executor.scheduleWithFixedDelay(task, DELAY_HOURS, DELAY_HOURS, TimeUnit.HOURS);
        LOG.info("Scheduled email reminder generator to run every " + DELAY_HOURS + " hours.");
    }

    private void processPayments(EmailSession emailSession, Date from, Date to) throws Exception {
        List<Payment> payments = paymentDao.getPaymentsByExpirationDate(from, to);
        LOG.info(payments.size() + " payment(s) expiring between " + from + " and " + to);
        if (payments.isEmpty()) {
            return;
        }

        for (Payment payment : payments) {
            try {
                processPayment(payment, emailSession);
            } catch (Throwable x) {
                LOG.error("Failed to process " + payment, x);
            }
        }
    }

    private void processSubscriptions(EmailSession emailSession, Date from, Date to) throws Exception {
        List<Subscription> subscriptions = subscriptionDao.getSubscriptionsByExpirationDate(from, to);
        LOG.info(subscriptions.size() + " subscription(s) expiring between " + from + " and " + to);
        if (subscriptions.isEmpty()) {
            return;
        }

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
            sendReminder(payment.getValidTo(), payment.getPayerFirstName(), email, emailSession);

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
            sendReminder(subscription.getValidTo(), subscription.getFirstName(), email, emailSession);

        } catch (Throwable x) {
            LOG.error("Failed to process " + subscription, x);
        }
    }

    public void sendReminder(Date licenseExpires, String firstName, String to, EmailSession emailSession) throws MessagingException {
        emailSession.sendMessage("license@subsonic.org",
                Arrays.asList(to),
                null,
                Arrays.asList("license@subsonic.org", "sindre@activeobjects.no"),
                Arrays.asList("license@subsonic.org"),
                "Your Subsonic Premium subscription is about to expire",
                createEmailContent(licenseExpires, firstName));
        LOG.info("Sent email reminder to " + to);
    }

    private String createEmailContent(Date licenseExpires, String firstName) {

        if (StringUtils.isBlank(firstName)) {
            firstName = "Subsonic Premium user";
        }
        return "Dear " + firstName + ",\n" +
                "\n" +
                "This is a friendly reminder that your Subsonic Premium subscription expires on " + DATE_FORMAT.format(licenseExpires) + ".\n" +
                "\n" +
                "We hope that you enjoy the service and that you choose to renew your subscription.\n" +
                "\n" +
                "Please visit http://premium.subsonic.org for more details and purchase options!\n" +
                "\n" +
                "Note: If you previously selected the automatic renewal option you will be charged automatically, and no action is required on your part.\n" +
                "\n" +
                "Best regards,\n" +
                "The Subsonic team\n" +
                "\n" +
                "(This email is sent 14 and 7 days before the license expires)";
    }

    public void setPaymentDao(PaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }

    public void setSubscriptionDao(SubscriptionDao subscriptionDao) {
        this.subscriptionDao = subscriptionDao;
    }
}
