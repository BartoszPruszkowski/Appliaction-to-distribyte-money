package pl.inz.costshare.mobile.data;

import android.app.Application;
import android.content.ContextWrapper;

import pl.inz.costshare.mobile.dto.UserDto;

public class AppContext extends Application {

    private UserDto user;
    private String password;

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserAndPassword getUserAndPassword() {
        return new UserAndPassword(user.getUserName(), password);
    }

    public static AppContext getInstance(ContextWrapper contextWrapper) {
        return (AppContext) contextWrapper.getApplicationContext();
    }

}