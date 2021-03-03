package bakery.Services.Security;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import bakery.Services.BakedFormation;

@Service
public class UserDetailService implements UserDetailsService {

    private static Gson gson = new Gson();

    @Autowired
    BakedFormation bakedForm;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(true){
            return new User("username","password", new ArrayList<>());
        }

        return null;
	}
}
