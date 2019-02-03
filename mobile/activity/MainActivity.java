package pl.inz.costshare.mobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.github.kittinunf.fuel.core.FuelError;
import pl.inz.costshare.mobile.data.AppContext;
import pl.inz.costshare.mobile.dto.UserDto;
import pl.inz.costshare.mobile.service.ResponseHandler;
import pl.inz.costshare.mobile.service.UserService;
import pl.inz.costshare.mobile.tools.Tools;

public class MainActivity extends Activity {

    private final Activity thisActivity = this;

    private UserService userService = new UserService();


    private Button registerButton;
    private Button remindPasswordButton;

    private TextInputLayout textInputLogin;
    private TextInputLayout textInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInputLogin = findViewById(R.id.text_input_login);
        textInputPassword = findViewById(R.id.text_input_password);

        textInputLogin.getEditText().setText("admin@test.pl");
        textInputPassword.getEditText().setText("admin");

        remindPasswordButton = findViewById(R.id.remindPasswordButton);
        remindPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPassword();
            }
        });

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegister();
            }
        });

        findViewById(R.id.logInButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (!confirmInput()) {
                    return;
                }

                final String userName = textInputLogin.getEditText().getText().toString().trim();
                final String password = textInputPassword.getEditText().getText().toString().trim();

                userService.getMe(userName, password, new ResponseHandler<UserDto>(thisActivity) {
                    @Override
                    public void success(UserDto value) {
                        AppContext appContext = AppContext.getInstance(thisActivity);
                        appContext.setUser(value);
                        appContext.setPassword(password);

                        Tools.info(getApplicationContext(), "Zalogowano pomyślnie!");

                        Intent intent = new Intent(v.getContext(), GroupsListActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void failure(FuelError error, String message) {
                        Tools.error(getApplicationContext(), "Zły login lub hasło!");
                    }
                });
            }
        });

    }

    private boolean validateLogin() {
        String loginInput = textInputLogin.getEditText().getText().toString().trim();
        if (loginInput.isEmpty()) {
            textInputLogin.setError("Pole nie może być puste");
            return false;
        } else {
            textInputLogin.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String hasloInput = textInputPassword.getEditText().getText().toString().trim();
        if (hasloInput.isEmpty()) {
            textInputPassword.setError("Pole nie może być puste");
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }

    private boolean confirmInput() {
        return validateLogin() && validatePassword();
    }

    private void openPassword() {
        Intent intent = new Intent(this, ResetPasswordStartActivity.class);
        startActivity(intent);
    }

    private void openRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}
