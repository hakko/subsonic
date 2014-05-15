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
public class SubscriptionNotification {

    private String id;
    private String subscrId;
    private String payerId;
    private String btnId;
    private String ipnTrackId;
    private String txnType;
    private String email;
    private Date created;

    public SubscriptionNotification(String id, String subscrId, String payerId, String btnId, String ipnTrackId, String txnType, String email, Date created) {
        this.id = id;
        this.subscrId = subscrId;
        this.payerId = payerId;
        this.btnId = btnId;
        this.ipnTrackId = ipnTrackId;
        this.txnType = txnType;
        this.email = email;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubscrId() {
        return subscrId;
    }

    public void setSubscrId(String subscrId) {
        this.subscrId = subscrId;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getBtnId() {
        return btnId;
    }

    public void setBtnId(String btnId) {
        this.btnId = btnId;
    }

    public String getIpnTrackId() {
        return ipnTrackId;
    }

    public void setIpnTrackId(String ipnTrackId) {
        this.ipnTrackId = ipnTrackId;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "SubscriptionNotification{" +
                "subscrId='" + subscrId + '\'' +
                ", payerId='" + payerId + '\'' +
                ", btnId='" + btnId + '\'' +
                ", ipnTrackId='" + ipnTrackId + '\'' +
                ", txnType='" + txnType + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
