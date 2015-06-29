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
package net.sourceforge.subsonic.backend.dao.schema;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Used for creating and evolving the database schema.
 * This class implementes the database schema for Subsonic Backend version 3.0.
 *
 * @author Sindre Mehus
 */
public class Schema30 extends Schema {
    private static final Logger LOG = Logger.getLogger(Schema30.class);

    public void execute(JdbcTemplate template) {

        if (!tableExists(template, "subscription")) {
            LOG.info("Database table 'subscription' not found.  Creating it.");
            template.execute("create cached table subscription (" +
                    "id identity," +
                    "subscr_id varchar," +  // PayPal subscription ID
                    "payer_id varchar," +  // PayPal payer ID
                    "btn_id varchar," +  // PayPal button ID
                    "email varchar_ignorecase," +
                    "first_name varchar," +
                    "last_name varchar," +
                    "country varchar," +
                    "valid_from datetime," +
                    "valid_to datetime," +
                    "processing_status varchar not null," +
                    "created datetime not null," +
                    "updated datetime not null)");
            template.execute("create index idx_subscription_subscr_id on subscription(subscr_id)");
            template.execute("create index idx_subscription_payer_id on subscription(payer_id)");
            template.execute("create index idx_subscription_created on subscription(created)");
            template.execute("create index idx_subscription_processing_status on subscription(processing_status)");
            template.execute("create index idx_subscription_email on subscription(email)");

            LOG.info("Database table 'subscription' was created successfully.");
        }


        if (!tableExists(template, "subscription_payment")) {
            LOG.info("Database table 'subscription_payment' not found.  Creating it.");
            template.execute("create cached table subscription_payment (" +
                    "id identity," +
                    "subscr_id varchar," +  // PayPal subscription ID
                    "payer_id varchar," +  // PayPal payer ID
                    "btn_id varchar," +  // PayPal button ID
                    "ipn_track_id varchar," +  // PayPal IPN track ID
                    "txn_id varchar," +  // PayPal IPN track ID
                    "email varchar_ignorecase," +
                    "amount double," +
                    "fee double," +
                    "currency varchar," +
                    "created datetime not null)");
            template.execute("create index idx_subscription_payment_email on subscription_payment(email)");

            LOG.info("Database table 'subscription_payment' was created successfully.");
        }

        if (!tableExists(template, "subscription_notification")) {
            LOG.info("Database table 'subscription_notification' not found.  Creating it.");
            template.execute("create cached table subscription_notification (" +
                    "id identity," +
                    "subscr_id varchar," +  // PayPal subscription ID
                    "payer_id varchar," +  // PayPal payer ID
                    "btn_id varchar," +  // PayPal button ID
                    "ipn_track_id varchar," +  // PayPal IPN track ID
                    "txn_type varchar," +
                    "email varchar_ignorecase," +
                    "created datetime not null)");
            template.execute("create index idx_subscription_notification_email on subscription_notification(email)");

            LOG.info("Database table 'subscription_notification' was created successfully.");
        }

        if (!columnExists(template, "valid_to", "whitelist")) {
            LOG.info("Database column 'whitelist.valid_to' not found.  Creating it.");
            template.execute("alter table whitelist add valid_to datetime null");
            LOG.info("Database column 'whitelist.valid_to' was added successfully.");
        }

        if (!columnExists(template, "valid_to", "payment")) {
            LOG.info("Database column 'payment.valid_to' not found.  Creating it.");
            template.execute("alter table payment add valid_to datetime null");
            LOG.info("Database column 'payment.valid_to' was added successfully.");
        }

        if (!tableExists(template, "currency_conversion")) {
            LOG.info("Database table 'currency_conversion' not found.  Creating it.");
            template.execute("create table currency_conversion (" +
                    "id identity," +
                    "source varchar not null," +
                    "target varchar not null," +
                    "rate double not null)");

            template.execute("insert into currency_conversion values(null, 'EUR', 'EUR', 1.0)");
            template.execute("insert into currency_conversion values(null, 'EUR', 'USD', 0.77)");
            template.execute("insert into currency_conversion values(null, 'EUR', 'NOK', 0.13)");
            template.execute("insert into currency_conversion values(null, 'EUR', 'SEK', 0.12)");

            LOG.info("Database table 'currency_conversion' was created successfully.");
        }

        if (!rowExists(template, "table_name='SUBSCRIPTION' and column_name='VALID_TO' and ordinal_position=1",
                "information_schema.system_indexinfo")) {
            template.execute("create index idx_subscription_valid_to on subscription(valid_to)");
            LOG.info("Database index idx_subscription_valid_to created successfully.");
        }

        if (!rowExists(template, "table_name='PAYMENT' and column_name='VALID_TO' and ordinal_position=1",
                "information_schema.system_indexinfo")) {
            template.execute("create index idx_payment_valid_to on payment(valid_to)");
            LOG.info("Database index idx_payment_valid_to created successfully.");
        }

        if (!rowExists(template, "table_name='PAYMENT' and column_name='PROCESSING_STATUS' and ordinal_position=1",
                "information_schema.system_indexinfo")) {
            template.execute("create index idx_payment_processing_status on payment(processing_status)");
            LOG.info("Database index idx_payment_processing_status created successfully.");
        }
    }
}