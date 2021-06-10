package cloud.demo.bean;

import lombok.Data;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name="user")
@Proxy(lazy = false)
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userid;
    private String username;
    private String pwd;
    private String role;

}
