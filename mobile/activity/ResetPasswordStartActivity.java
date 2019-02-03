package pl.inz.costshare.mobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import com.github.kittinunf.fuel.core.FuelError;
import pl.inz.costshare.mobile.service.ResponseHandler;
import pl.inz.costshare.mobile.service.UserService;
import pl.inz.costshare.mobile.tools.Tools;

public class ResetPasswordStartActivity extends Activity {

    private static final int CHANGE_PASSWORD_REQUEST_CODE = 106;

    private final Activity thisActivity = this;
    private UserService userService = new UserService();

    private TextInputLayout userNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_start);
        userNameInput = findViewById(R.id.username);

        findViewById(R.id.resetStartBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!validateInput()) {
                    return;
                }

                final String userName = userNameInput.getEditText().getText().toString().trim();

                userService.resetPasswordStart(userName, new ResponseHandler<Object>(thisActivity) {
                    @Override
                    public void success(Object value) {
                        Intent intent = new Intent(thisActivity, ResetPasswordFinishActivity.class);
                        intent.putExtra("userName", userName);
                        startActivityForResult(intent, CHANGE_PASSWORD_REQUEST_CODE);
                    }

                    @Override
                    public void failure(FuelError error, String message) {
                        Tools.error(getApplicationContext(), message);
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == CHANGE_PASSWORD_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Tools.info(getApplicationContext(), "Hasło zmienione");
            finish();
        }
    }

    private boolean validateInput() {
        String loginInput = userNameInput.getEditText().getText().toString().trim();
        if (loginInput.isEmpty()) {
            userNameInput.setError("Pole nie może być puste");
            return false;
        } else {
            userNameInput.setError(null);
            return true;
        }
    }
}
