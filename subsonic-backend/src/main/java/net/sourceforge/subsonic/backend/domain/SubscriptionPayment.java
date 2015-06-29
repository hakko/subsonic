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

 Copyright 2010 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.backend.domain;

import java.util.Date;

/**
 * @author Sindre Mehus
 */
public class SubscriptionPayment {

    private String id;
    private String subscrId;
    private String payerId;
    private String btnId;
    private String ipnTrackId;
    private String txnId;
    private String email;
    private Double amount;
    private Double fee;
    private String currency;
    private Date created;

    public SubscriptionPayment(String id, String subscrId, String payerId, String btnId, String ipnTrackId,
                               String txnId, String email, Double amount, Double fee, String currency, Date created) {
        this.id = id;
        this.subscrId = subscrId;
        this.payerId = payerId;
        this.btnId = btnId;
        this.ipnTrackId = ipnTrackId;
        this.txnId = txnId;
        this.email = email;
        this.amount = amount;
        this.fee = fee;
        this.currency = currency;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public String getSubscrId() {
        return subscrId;
    }

    public String getPayerId() {
        return payerId;
    }

    public String getBtnId() {
        return btnId;
    }

    public String getIpnTrackId() {
        return ipnTrackId;
    }

    public String getTxnId() {
        return txnId;
    }

    public String getEmail() {
        return email;
    }

    public Double getAmount() {
        return amount;
    }

    public Double getFee() {
        return fee;
    }

    public String getCurrency() {
        return currency;
    }

    public Date getCreated() {
        return created;
    }

    @Override
    public String toString() {
        return "SubscriptionPayment{" +
                "id='" + id + '\'' +
                ", subscrId='" + subscrId + '\'' +
                ", payerId='" + payerId + '\'' +
                ", btnId='" + btnId + '\'' +
                ", ipnTrackId='" + ipnTrackId + '\'' +
                ", txnId='" + txnId + '\'' +
                ", email='" + email + '\'' +
                ", amount=" + amount +
                ", fee=" + fee +
                ", currency='" + currency + '\'' +
                ", created=" + created +
                '}';
    }
}
