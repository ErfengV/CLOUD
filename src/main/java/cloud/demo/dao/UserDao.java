package cloud.demo.dao;

import cloud.demo.bean.User;

import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @author 帅气的二峰
 */
public interface UserDao extends JpaRepository<User, Integer> {
}
