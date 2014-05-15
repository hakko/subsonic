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
package net.sourceforge.subsonic.backend.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import net.sourceforge.subsonic.backend.Util;
import net.sourceforge.subsonic.backend.dao.DaoHelper;
import net.sourceforge.subsonic.backend.dao.PaymentDao;
import net.sourceforge.subsonic.backend.dao.SubscriptionDao;
import net.sourceforge.subsonic.backend.domain.Payment;
import net.sourceforge.subsonic.backend.domain.Subscription;
import net.sourceforge.subsonic.backend.service.EmailSession;
import net.sourceforge.subsonic.backend.service.LicenseGenerator;
import net.sourceforge.subsonic.backend.service.WhitelistGenerator;

/**
 * Multi-controller used for simple pages.
 *
 * @author Sindre Mehus
 */
public class MultiController extends MultiActionController {

    private static final Logger LOG = Logger.getLogger(RedirectionController.class);

    private static final String SUBSONIC_VERSION = "4.9";
    private static final String SUBSONIC_BETA_VERSION = "4.9.beta4";

    private static final Date LICENSE_DATE_THRESHOLD;

    private DaoHelper daoHelper;

    private PaymentDao paymentDao;
    private SubscriptionDao subscriptionDao;
    private WhitelistGenerator whitelistGenerator;
    private LicenseGenerator licenseGenerator;

    static {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2010);
        calendar.set(Calendar.MONTH, Calendar.JUNE);
        calendar.set(Calendar.DAY_OF_MONTH, 19);
        LICENSE_DATE_THRESHOLD = calendar.getTime();
    }

    public ModelAndView version(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String localVersion = request.getParameter("v");
        LOG.info(request.getRemoteAddr() + " asked for latest version. Local version: " + localVersion);

        PrintWriter writer = response.getWriter();

        writer.println("SUBSONIC_VERSION_BEGIN" + SUBSONIC_VERSION + "SUBSONIC_VERSION_END");
        writer.println("SUBSONIC_FULL_VERSION_BEGIN" + SUBSONIC_VERSION + "SUBSONIC_FULL_VERSION_END");
        writer.println("SUBSONIC_BETA_VERSION_BEGIN" + SUBSONIC_BETA_VERSION + "SUBSONIC_BETA_VERSION_END");

        return null;
    }

    public ModelAndView validateLicense(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String email = request.getParameter("email");
        Long date = ServletRequestUtils.getLongParameter(request, "date");

        boolean valid = isLicenseValid(email, date);
        Date expirationDate = getLicenseExpirationDate(email);
        LOG.info(request.getRemoteAddr() + " asked to validate license for " + email + ". Result: " +
                valid + ", expires: " + expirationDate);

        PrintWriter writer = response.getWriter();
        writer.println(valid);
        if (expirationDate != null) {
            writer.println(expirationDate.getTime());
        }

        return null;
    }

    public ModelAndView sendMail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String subject = request.getParameter("subject");
        String text = request.getParameter("text");

        EmailSession session = new EmailSession();
        session.sendMessage(from, Arrays.asList(to), null, null, null, subject, text);

        LOG.info("Sent email on behalf of " + request.getRemoteAddr() + " to " + to + " with subject '" + subject + "'");

        return null;
    }

    public ModelAndView db(HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (!authenticate(request, response)) {
            return null;
        }

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("p", request.getParameter("p"));
        String query = request.getParameter("query");
        if (query != null) {
            map.put("query", query);

            try {
                List<?> result = daoHelper.getJdbcTemplate().query(query, new ColumnMapRowMapper());
                map.put("result", result);
            } catch (DataAccessException x) {
                map.put("error", ExceptionUtils.getRootCause(x).getMessage());
            }
        }

        return new ModelAndView("backend/db", "model", map);
    }

    public ModelAndView payment(HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (!authenticate(request, response)) {
            return null;
        }

        int days = ServletRequestUtils.getIntParameter(request, "days", 30);

        SortedMap<Date, Money> payments = new TreeMap<Date, Money>();

        Calendar startOfDay = Calendar.getInstance();
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        startOfDay.set(Calendar.MILLISECOND, 0);

        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTime(startOfDay.getTime());
        endOfDay.add(Calendar.DATE, 1);

        for (int i = 0; i < days; i++) {
            payments.put(startOfDay.getTime(), getAmountForPeriod(CurrencyUnit.EUR, startOfDay.getTime(), endOfDay.getTime()));
            startOfDay.add(Calendar.DATE, -1);
            endOfDay.add(Calendar.DATE, -1);
        }

        Money sum = computeSum(payments.values());
        Money average = sum.dividedBy(days, RoundingMode.HALF_UP);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("payments", payments);
        map.put("sum", sum);
        map.put("average", average);

        return new ModelAndView("backend/payment", "model", map);
    }

    private Money computeSum(Collection<Money> moneys) {
        Money sum = Money.zero(CurrencyUnit.EUR);
        for (Money money : moneys) {
            sum = sum.plus(money);
        }
        return sum;
    }

    private Money getAmountForPeriod(CurrencyUnit currency, Date from, Date to) {
        List<Money> payments = new ArrayList<Money>();
        payments.addAll(paymentDao.getMoneyForPeriod(from, to));
        payments.addAll(subscriptionDao.getMoneyForPeriod(from, to));

        Map<CurrencyUnit, BigDecimal> currencyConversions = paymentDao.getCurrencyConversionsFor(currency);

        Money sum = Money.zero(currency);
        for (Money payment : payments) {
            if (currency.equals(payment.getCurrencyUnit())) {
                sum = sum.plus(payment);
            } else {
                BigDecimal conversionRate = currencyConversions.get(payment.getCurrencyUnit());
                if (conversionRate == null) {
                    LOG.warn("No conversion rate found for " + currency + " to " + payment.getCurrencyUnit() + ". Skipping it.");
                } else {
                    sum = sum.plus(payment.convertedTo(currency, conversionRate, RoundingMode.HALF_UP));
                }
            }
        }
        return sum;
    }

    public ModelAndView requestLicense(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String email = request.getParameter("email");
        boolean valid = email != null && isLicenseValid(email, System.currentTimeMillis());
        if (valid) {
            EmailSession session = new EmailSession();
            licenseGenerator.sendLicenseTo(email, session);
        }

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("email", email);
        map.put("valid", valid);

        return new ModelAndView("backend/requestLicense", "model", map);
    }

    public ModelAndView whitelist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!authenticate(request, response)) {
            return null;
        }

        Date newerThan = MultiController.LICENSE_DATE_THRESHOLD;
        Integer days = ServletRequestUtils.getIntParameter(request, "days");
        if (days != null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -days);
            newerThan = cal.getTime();
        }
        whitelistGenerator.generate(newerThan);
        return null;
    }

    private boolean authenticate(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException, IOException {
        String password = ServletRequestUtils.getRequiredStringParameter(request, "p");
        if (!password.equals(Util.getPassword("backendpwd.txt"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }

    private boolean isLicenseValid(String email, Long date) {
        if (email == null || date == null) {
            return false;
        }

        if (paymentDao.isBlacklisted(email)) {
            return false;
        }

        // Always accept licenses that are older than 2010-06-19.
        if (date < LICENSE_DATE_THRESHOLD.getTime()) {
            return true;
        }

        return hasValidSubscription(email) || hasValidPayment(email) || paymentDao.isWhitelisted(email);
    }

    private Date getLicenseExpirationDate(String email) {
        if (email == null) {
            return null;
        }

        if (paymentDao.isBlacklisted(email) || paymentDao.isWhitelisted(email)) {
            return null;
        }

        Subscription subscription = subscriptionDao.getSubscriptionByEmail(email);
        Payment payment = paymentDao.getPaymentByEmail(email);

        Date subscriptionExpirationDate = subscription == null ? null : subscription.getValidTo();
        Date paymentExpirationDate = payment == null ? null : payment.getValidTo();

        return Util.latest(subscriptionExpirationDate, paymentExpirationDate);
    }

    private boolean hasValidPayment(String email) {
        Payment payment = paymentDao.getPaymentByEmail(email);
        if (payment == null) {
            return false;
        }
        Date now = new Date();
        return payment.getValidTo() == null || payment.getValidTo().after(now);
    }

    private boolean hasValidSubscription(String email) {
        Subscription subscription = subscriptionDao.getSubscriptionByEmail(email);
        if (subscription == null) {
            return false;
        }
        Date now = new Date();
        return subscription.getValidTo() == null || subscription.getValidTo().after(now);
    }

    public void setDaoHelper(DaoHelper daoHelper) {
        this.daoHelper = daoHelper;
    }

    public void setPaymentDao(PaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }

    public void setWhitelistGenerator(WhitelistGenerator whitelistGenerator) {
        this.whitelistGenerator = whitelistGenerator;
    }

    public void setLicenseGenerator(LicenseGenerator licenseGenerator) {
        this.licenseGenerator = licenseGenerator;
    }

    public void setSubscriptionDao(SubscriptionDao subscriptionDao) {
        this.subscriptionDao = subscriptionDao;
    }
}
