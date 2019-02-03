package pl.inz.costshare.mobile.data;

public class UserAndPassword {
    private String userNanme;
    private String password;

    public UserAndPassword(String userNanme, String password) {
        this.userNanme = userNanme;
        this.password = password;
    }

    public String getUserNanme() {
        return userNanme;
    }

    public void setUserNanme(String userNanme) {
        this.userNanme = userNanme;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
