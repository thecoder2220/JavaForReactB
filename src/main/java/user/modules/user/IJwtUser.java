package user.modules.user;

import java.util.Locale;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by CARL on 09/06/2017.
 */
public interface IJwtUser
        extends UserDetails {
    public Long getId();

    public String getEmail();

    public String getFirstname();

    public String getLastname();

    public Locale getLocale();
}
