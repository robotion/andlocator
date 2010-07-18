package com.jaeckel.locator.user;

/**
 * User: biafra
 * Date: Jun 16, 2010
 * Time: 11:11:03 PM
 */
public class Account {

    private String name;
    private String email;
    private String password;
    private String pubKeyId;
    private String pubKey;

    public Account(String name, String email, String password, String pubKeyId, String pubKey) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.pubKeyId = pubKeyId;
        this.pubKey = pubKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPubKeyId() {
        return pubKeyId;
    }

    public void setPubKeyId(String pubKeyId) {
        this.pubKeyId = pubKeyId;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }
}
