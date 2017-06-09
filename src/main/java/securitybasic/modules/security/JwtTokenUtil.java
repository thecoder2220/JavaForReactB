package securitybasic.modules.security;

/**
 * Created by CARL on 09/06/2017.
 */
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import securitybasic.modules.security.service.JwtProperties;
import user.modules.user.IJwtUser;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(value={JwtProperties.class})
public class JwtTokenUtil
        implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);
    private static final long serialVersionUID = -3301605591108950415L;
    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_AUDIENCE = "audience";
    private static final String CLAIM_KEY_CREATED = "created";
    private static final String AUDIENCE_UNKNOWN = "unknown";
    private static final String AUDIENCE_WEB = "web";
    private static final String AUDIENCE_MOBILE = "mobile";
    private static final String AUDIENCE_TABLET = "tablet";
    private static final String HEADER_REMEMBER_ME = "longTerm";
    @Autowired
    private JwtProperties properties;

    public String getUsernameFromToken(String token) {
        String username = null;
        try {
            Claims claims = this.getClaimsFromToken(token);
            if (claims != null) {
                username = claims.getSubject();
            }
        }
        catch (Exception e) {
            LOGGER.warn("Impossible de recuperer le username depuis ce token", (Throwable)e);
        }
        return username;
    }

    public Date getCreatedDateFromToken(String token) {
        Date created = null;
        try {
            Claims claims = this.getClaimsFromToken(token);
            if (claims != null) {
                created = new Date((Long)claims.get((Object)"created"));
            }
        }
        catch (Exception e) {
            LOGGER.warn("Impossible de recuperer la date de cr\u00e9ation de ce token", (Throwable)e);
        }
        return created;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration = null;
        try {
            Claims claims = this.getClaimsFromToken(token);
            if (claims != null) {
                expiration = claims.getExpiration();
            }
        }
        catch (Exception e) {
            LOGGER.warn("Impossible de recuperer la date d'expiration de ce token", (Throwable)e);
        }
        return expiration;
    }

    public String getAudienceFromToken(String token) {
        String audience = null;
        try {
            Claims claims = this.getClaimsFromToken(token);
            if (claims != null) {
                audience = (String)claims.get((Object)"audience");
            }
        }
        catch (Exception e) {
            LOGGER.warn("Impossible de recuperer l'audience de ce token", (Throwable)e);
        }
        return audience;
    }

    private Claims getClaimsFromToken(String token) {
        try {
            return (Claims)this.getWholeClaimsFromToken(token).getBody();
        }
        catch (Exception e) {
            return null;
        }
    }

    private Jws<Claims> getWholeClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(this.properties.getSecret()).parseClaimsJws(token);
    }

    private Date generateExpirationDate(boolean longTerm) {
        Long expiration = longTerm && this.properties.getLongExpiration() != null ? this.properties.getLongExpiration() : this.properties.getExpiration();
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    private Boolean isTokenExpired(String token) {
        Date expiration = this.getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private String generateAudience() {
        return "web";
    }

    private Boolean ignoreTokenExpiration(String token) {
        String audience = this.getAudienceFromToken(token);
        return "tablet".equals(audience) || "mobile".equals(audience);
    }

    public String generateToken(UserDetails userDetails, boolean longTerm) {
        HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put("sub", userDetails.getUsername());
        claims.put("audience", this.generateAudience());
        claims.put("created", new Date());
        return this.generateToken(claims, longTerm);
    }

    private String generateToken(Map<String, Object> claims, boolean longTerm) {
        return Jwts.builder().setClaims(claims).setExpiration(this.generateExpirationDate(longTerm)).signWith(SignatureAlgorithm.HS512, this.properties.getSecret()).setHeaderParam("longTerm", (Object)longTerm).compact();
    }

    public Boolean canTokenBeRefreshed(String token) {
        return this.isTokenExpired(token) == false || this.ignoreTokenExpiration(token) != false;
    }

    public String refreshToken(String token) {
        String refreshedToken;
        try {
            Jws<Claims> jwsClaims = this.getWholeClaimsFromToken(token);
            boolean longTerm = (Boolean)((JwsHeader)jwsClaims.getHeader()).getOrDefault((Object)"longTerm", (Object)false);
            Claims claims = (Claims)jwsClaims.getBody();
            //code originel d'ipsosenso = claims.put((Object)"created", (Object)new Date());
            claims.put("created", (Object)new Date());
            refreshedToken = this.generateToken((Map<String, Object>)claims, longTerm);
        }
        catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        IJwtUser user = (IJwtUser)userDetails;
        String username = this.getUsernameFromToken(token);
        return username.equals(user.getUsername()) && this.isTokenExpired(token) == false;
    }
}


