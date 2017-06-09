package securitybasic.modules.security.service;

/**
 * Created by CARL on 09/06/2017.
 */
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="jwt")
public class JwtProperties {
    private String header;
    private String secret;
    private Long expiration;
    private Long longExpiration;

    public String getSecret() {
        return this.secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getExpiration() {
        return this.expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public Long getLongExpiration() {
        return this.longExpiration;
    }

    public void setLongExpiration(Long longExpiration) {
        this.longExpiration = longExpiration;
    }

    public String getHeader() {
        return this.header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}

