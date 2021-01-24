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
    private Map<String, User> users = new ConcurrentHashMap<>();
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Inject
    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        save("张三", "zhangsan");
    }

    public void save(String username, String password) {
        users.put(username, new User(1, username, bCryptPasswordEncoder.encode(password)));
    }

    public User getUserByUserName(String username) {
        return users.get(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (users.containsKey(username)) {
            User user = users.get(username);
            return new org.springframework.security.core.userdetails.User(username, user.getEncryptedPassword(), Collections.emptyList());
        }
        throw new UsernameNotFoundException(username + "不存在");
    }
}
