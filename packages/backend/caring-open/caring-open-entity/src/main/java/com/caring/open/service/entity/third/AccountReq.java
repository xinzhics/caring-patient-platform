package com.caring.open.service.entity.third;

import lombok.ToString;

import java.io.Serializable;

/**
 * 账号参数
 *
 * @author xinz
 */
public class AccountReq implements Serializable {

    public static final long serialVersionUID = 1L;

    private int accountId;

    private String accountName;

    private String createDate;

    private boolean remove;

    private String serial;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    @Override
    public String toString() {
        return "AccountReq{" +
                "accountId=" + accountId +
                ", accountName='" + accountName + '\'' +
                ", createDate='" + createDate + '\'' +
                ", remove=" + remove +
                ", serial='" + serial + '\'' +
                '}';
    }
}
