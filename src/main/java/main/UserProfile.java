package main;


public class UserProfile {
    private Integer id;
    private String login;
    private String password;
    private String email;
    private boolean isDeleted;

    public UserProfile(Integer id,String login, String password, String email) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
        this.isDeleted = false;
    }

    public void updateProfile(String log, String pass, String mail){
        this.login = log;
        this.password = pass;
        this.email = mail;
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

    public void setDeleted(){
        this.isDeleted = true;
    }

    public Integer getId() {
        return this.id;
    }
}
