package net.sourceforge.subsonic.backend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.money.Money;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import net.sourceforge.subsonic.backend.domain.ProcessingStatus;
import net.sourceforge.subsonic.backend.domain.Subscription;
import net.sourceforge.subsonic.backend.domain.SubscriptionNotification;
import net.sourceforge.subsonic.backend.domain.SubscriptionPayment;

/**
 * Provides database services for PayPal subscriptions.
 *
 * @author Sindre Mehus
 */
public class SubscriptionDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(SubscriptionDao.class);

    private static final String SUBSCRIPTION_COLUMNS = "id, subscr_id, payer_id, btn_id, email, first_name, " +
            "last_name, country, valid_from, valid_to, processing_status, created, updated";

    private static final String SUBSCRIPTION_PAYMENT_COLUMNS = "id, subscr_id, payer_id, btn_id, ipn_track_id, " +
            "txn_id, email, amount, fee, currency, created";

    private static final String SUBSCRIPTION_NOTIFICATION_COLUMNS = "id, subscr_id, payer_id, btn_id, ipn_track_id, " +
            "txn_type, email, created";

    private RowMapper subscriptionRowMapper = new SubscriptionRowMapper();
    private RowMapper moneyRowMapper = new MoneyRowMapper();

    /**
     * Returns the subscription with the given email.
     *
     * @param email The email.
     * @return The subscription or <code>null</code> if not found.
     */
    public Subscription getSubscriptionByEmail(String email) {
        if (email == null) {
            return null;
        }
        String sql = "select " + SUBSCRIPTION_COLUMNS + " from subscription where email=?";
        return queryOne(sql, subscriptionRowMapper, email.toLowerCase());
    }

    /**
     * Returns all subscriptions with the given processing status.
     *
     * @param status The status.
     * @return List of subscriptions.
     */
    public List<Subscription> getSubscriptionsByProcessingStatus(ProcessingStatus status) {
        return query("select " + SUBSCRIPTION_COLUMNS + " from subscription where processing_status=?", subscriptionRowMapper, status.name());
    }

    public List<Subscription> getSubscriptionsByExpirationDate(Date from, Date to) {
        return query("select " + SUBSCRIPTION_COLUMNS + " from subscription where valid_to between ? and ?", subscriptionRowMapper, from, to);
    }

    /**
     * Creates a new subscription.
     */
    public void createSubscription(Subscription s) {
        String sql = "insert into subscription (" + SUBSCRIPTION_COLUMNS + ") values (" + questionMarks(SUBSCRIPTION_COLUMNS) + ")";
        update(sql, null, s.getSubscrId(), s.getPayerId(), s.getBtnId(), StringUtils.lowerCase(s.getEmail()),
                s.getFirstName(), s.getLastName(), s.getCountry(), s.getValidFrom(), s.getValidTo(),
                s.getProcessingStatus().name(), s.getCreated(), s.getUpdated());
        LOG.info("Created " + s);
    }

    /**
     * Updates the given subscription.
     */
    public void updateSubscription(Subscription s) {
        String sql = "update subscription set subscr_id=?, payer_id=?, btn_id=?, email=?, " +
                "first_name=?, last_name=?, country=?, valid_from=?, " +
                "valid_to=?, processing_status=?, created=?, updated=? where id=?";
        update(sql, s.getSubscrId(), s.getPayerId(), s.getBtnId(), s.getEmail(), s.getFirstName(), s.getLastName(),
                s.getCountry(), s.getValidFrom(), s.getValidTo(),
                s.getProcessingStatus().name(), s.getCreated(), s.getUpdated(), s.getId());
        LOG.info("Updated " + s);
    }

    /**
     * Creates a new subscription payment.
     */
    public void createSubscriptionPayment(SubscriptionPayment s) {
        String sql = "insert into subscription_payment (" + SUBSCRIPTION_PAYMENT_COLUMNS + ") values (" +
                questionMarks(SUBSCRIPTION_PAYMENT_COLUMNS) + ")";
        update(sql, null, s.getSubscrId(), s.getPayerId(), s.getBtnId(), s.getIpnTrackId(), s.getTxnId(),
                StringUtils.lowerCase(s.getEmail()), s.getAmount(), s.getFee(), s.getCurrency(), s.getCreated());
        LOG.info("Created " + s);
    }

    /**
     * Creates a new subscription notification.
     */
    public void createSubscriptionNotification(SubscriptionNotification s) {
        String sql = "insert into subscription_notification (" + SUBSCRIPTION_NOTIFICATION_COLUMNS + ") values (" +
                questionMarks(SUBSCRIPTION_NOTIFICATION_COLUMNS) + ")";
        update(sql, null, s.getSubscrId(), s.getPayerId(), s.getBtnId(), s.getIpnTrackId(), s.getTxnType(),
                StringUtils.lowerCase(s.getEmail()), s.getCreated());
        LOG.info("Created " + s);
    }

    public List<Money> getMoneyForPeriod(Date from, Date to) {
        return query("select amount, currency from subscription_payment where created between ? and ?", moneyRowMapper, from, to);
    }

    private static class SubscriptionRowMapper implements ParameterizedRowMapper<Subscription> {
        public Subscription mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Subscription(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                    rs.getString(6), rs.getString(7), rs.getString(8), rs.getTimestamp(9), rs.getTimestamp(10),
                    ProcessingStatus.valueOf(rs.getString(11)), rs.getTimestamp(12), rs.getTimestamp(13));
        }
    }
}