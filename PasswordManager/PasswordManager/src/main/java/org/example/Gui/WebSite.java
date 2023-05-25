package org.example.Gui;

public class WebSite {
    private String url;
    private String username;
    private String encryptedPassword;

    public WebSite(String url, String username, String encryptedPassword) {
        this.url = url;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }
}
