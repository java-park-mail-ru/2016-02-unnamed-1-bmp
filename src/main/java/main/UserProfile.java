package main;


public class UserProfile {
    private String login;
    private String password;
    private String email;
    private boolean isDeleted;

    public UserProfile(String login, String password, String email) {
        this.login = login;
        this.password = password;
        this.email = email;
        this.isDeleted = false;
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
}
