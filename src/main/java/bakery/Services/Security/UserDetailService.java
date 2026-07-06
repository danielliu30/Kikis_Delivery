package bakery.Services.Security;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import bakery.Models.SingleCustomer;
import bakery.Services.BakedFormation;

@Service
public class UserDetailService implements UserDetailsService {

    private final BakedFormation bakedForm;

    @Autowired
    public UserDetailService(BakedFormation bakedForm) {
        this.bakedForm = bakedForm;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SingleCustomer customer = new SingleCustomer();
        customer.setEmail(username);

        if (!bakedForm.checkExisitingUser(customer)) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Authorities list is empty — role-based access can be layered on here later
        return new User(username, "", new ArrayList<>());
    }
}
