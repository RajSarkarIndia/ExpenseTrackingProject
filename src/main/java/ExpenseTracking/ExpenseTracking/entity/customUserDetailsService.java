package ExpenseTracking.ExpenseTracking.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class customUserDetailsService implements UserDetailsService{
	@Autowired
	private UserRepository UserRepository;

	@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return UserRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

}
