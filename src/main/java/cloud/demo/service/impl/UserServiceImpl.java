package cloud.demo.service.impl;

import cloud.demo.bean.User;
import cloud.demo.dao.UserDao;
import cloud.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;


/**
 * @author 帅气的二峰
 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserDao dao;
    PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public User insert(User u) {
        User user = new User();
        user.setUsername(u.getUsername());
        user.setPwd(encoder.encode(u.getPwd()));
        user.setRole("ROLE_ADMIN");
        user = dao.save(user);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String uname) throws UsernameNotFoundException {
        User user = new User();
        user.setUsername(uname);
        Example<User> example = Example.of(user);
        Optional<User> one = dao.findOne(example);
        user = one.orElseGet(() -> {
            return null;
        });
        if (user == null) {
            return new org.springframework.security.core.userdetails.User("null", "null", new ArrayList<>());
        }
        Collection<GrantedAuthority> collection = new ArrayList<GrantedAuthority>();
        collection.add(new SimpleGrantedAuthority(user.getRole()));
        org.springframework.security.core.userdetails.User user1 = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPwd(), collection);
        return user1;
    }
}
