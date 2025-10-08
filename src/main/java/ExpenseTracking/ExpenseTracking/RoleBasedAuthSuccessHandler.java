package ExpenseTracking.ExpenseTracking;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;                 // correct Authentication
import org.springframework.security.core.GrantedAuthority;            // GrantedAuthority type
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RoleBasedAuthSuccessHandler implements AuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException {
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    String targetUrl = "/home";
    if (hasRole(authorities, "ROLE_ADMIN")) {
      targetUrl = "/admin/home";
    } else if (hasRole(authorities, "ROLE_MANAGER")) {
      targetUrl = "/managerhome";
    } else if (hasRole(authorities, "ROLE_EMPLOYEE") || hasRole(authorities, "ROLE_USER")) {
      targetUrl = "/expenses";
    }
    response.sendRedirect(targetUrl);
  }

  private boolean hasRole(Collection<? extends GrantedAuthority> auths, String role) {
    return auths.stream().anyMatch(a -> role.equals(a.getAuthority()));
  }
}
