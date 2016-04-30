package base.datasets;

import javax.persistence.*;
import java.io.Serializable;

import org.hibernate.annotations.Type;

@Entity
@Table(name= "users", indexes = {
        @Index(name="deleted_idx", columnList = "user_is_del"),
        @Index(name="login_idx", columnList = "user_login," + "user_is_del," + "user_is_anon"),
        @Index(name="email_idx", columnList = "user_email," + "user_is_del," + "user_is_anon")
})
public class UserDataSet implements Serializable {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_login")
    private String login;

    @Column(name = "user_pass")
    private String password;

    @Column(name = "user_email", unique = true)
    private String email;

    @Column(name = "user_is_del")
    @Type(type="yes_no")
    private Boolean isDeleted;

    @Column(name = "user_score")
    private Integer score;

    @Column(name = "user_is_anon")
    @Type(type="yes_no")
    private Boolean isAnonymous = false;

    public UserDataSet() {

    }

    public UserDataSet (String login, String password, String email) {
        this.id = -1L;
        this.login = login;
        this.password = password;
        this.email = email;
        this.isDeleted = false;
        this.score = 0;
        this.isAnonymous = false;
    }

    public UserDataSet (String login) {
        this.id = -1L;
        this.login = login;
        this.password = null;
        this.email = null;
        this.isDeleted = false;
        this.score = 0;
        this.isAnonymous = true;
    }


    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return this.id;
    }

    public Integer getScore() {
        return this.score;
    }
}
