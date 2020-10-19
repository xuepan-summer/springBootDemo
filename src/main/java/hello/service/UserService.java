package hello.service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class UserService implements UserDetailsService {
    private Map<String, String> usernameAndPassword = new ConcurrentHashMap<>();
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Inject
    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        save("张三", "zhangsan");
    }

    public void save(String username, String password) {
        usernameAndPassword.put(username, bCryptPasswordEncoder.encode(password));
    }

    public String getPassword(String username) {
        return usernameAndPassword.get(username);
    }

    public User getUserById(Integer id) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (usernameAndPassword.containsKey(username)) {
            String encodedPassword = getPassword(username);
            return new org.springframework.security.core.userdetails.User(username, encodedPassword, Collections.emptyList());
        }
        throw new UsernameNotFoundException(username + "不存在");
    }
}
