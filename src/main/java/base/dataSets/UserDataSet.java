package base.datasets;

import javax.persistence.*;
import java.io.Serializable;

import org.hibernate.annotations.Type;

@Entity
@Table(name= "users", indexes = {
        @Index(name="deleted_idx", columnList = "user_is_del"),
        @Index(name="login_idx", columnList = "user_login," + "user_is_del"),
        @Index(name="email_idx", columnList = "user_email," + "user_is_del")
})
public class UserDataSet implements Serializable {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_login", unique = true)
    private String login;

    @Column(name = "user_pass")
    private String password;

    @Column(name = "user_email", unique = true)
    private String email;

    @Column(name = "user_is_del")
    @Type(type="yes_no")
    private Boolean isDeleted;

    public UserDataSet() {

    }

    public UserDataSet (String login, String password, String email) {
        this.id = -1L;
        this.login = login;
        this.password = password;
        this.email = email;
        this.isDeleted = false;
    }

    public void updateUser(String log, String pass, String mail){
        this.login = log;
        this.password = pass;
        this.email = mail;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String log) {
        this.login = log;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String mail) {
        this.email = mail;
    }

    public void setDeleted() {
        this.isDeleted = true;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
