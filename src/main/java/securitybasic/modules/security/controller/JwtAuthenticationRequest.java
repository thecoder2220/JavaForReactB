package securitybasic.modules.security.controller;

import java.io.Serializable;

/**
 * Created by CARL on 09/06/2017.
 */
public class JwtAuthenticationRequest
        implements Serializable {
    private static final long serialVersionUID = -8445943548965154778L;
    private String username;
    private String password;
    private boolean rememberMe;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return this.rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
