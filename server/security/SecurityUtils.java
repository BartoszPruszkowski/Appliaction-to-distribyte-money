package pl.inz.costshare.server.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static CostShareUserDetails getUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof CostShareUserDetails) {
            return (CostShareUserDetails) auth.getPrincipal();
        }
        return null;
    }

    public static Long getCurrentUserId(){
        return getUserDetails().getUserId();
    }

}
