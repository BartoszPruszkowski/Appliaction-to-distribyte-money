package pl.inz.costshare.mobile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.TextView;
import com.github.kittinunf.fuel.core.FuelError;
import pl.inz.costshare.mobile.dto.ResetPasswordFinishDto;
import pl.inz.costshare.mobile.service.ResponseHandler;
import pl.inz.costshare.mobile.service.UserService;
import pl.inz.costshare.mobile.tools.Tools;

public class ResetPasswordFinishActivity extends Activity {

    private final Activity thisActivity = this;
    private UserService userService = new UserService();

    private String userName;

    private TextInputLayout codeInput;
    private TextInputLayout newPasswordInput;
    private TextInputLayout passwordRepeatInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_finish);

        userName = getIntent().getStringExtra("userName");

        TextView userNameLabel = findViewById(R.id.resetUserName);
        userNameLabel.setText(userName);

        codeInput = findViewById(R.id.resetCode);
        newPasswordInput = findViewById(R.id.newPassword);
        passwordRepeatInput = findViewById(R.id.newPasswordRepeat);

        findViewById(R.id.changePasswordBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!validateInput()) {
                    return;
                }

                ResetPasswordFinishDto resetPasswordFinishDto = new ResetPasswordFinishDto();
                resetPasswordFinishDto.setCode(codeInput.getEditText().getText().toString());
                resetPasswordFinishDto.setNewPassword(newPasswordInput.getEditText().getText().toString());
                resetPasswordFinishDto.setNewPasswordRepeat(passwordRepeatInput.getEditText().getText().toString());
                resetPasswordFinishDto.setUserName(userName);

                userService.resetPasswordFinish(resetPasswordFinishDto, new ResponseHandler<Object>(thisActivity) {
                    @Override
                    public void success(Object value) {
                        thisActivity.setResult(RESULT_OK);
                        thisActivity.finish();
                    }

                    @Override
                    public void failure(FuelError error, String message) {
                        Tools.error(getApplicationContext(), message);
                    }
                });
            }
        });
    }

    private boolean validateInput() {
        boolean valid = true;

        String code = codeInput.getEditText().getText().toString();
        String newPassword = newPasswordInput.getEditText().getText().toString();
        String passwordRepeat = passwordRepeatInput.getEditText().getText().toString();

        if (code.isEmpty()) {
            codeInput.setError("Pole nie może być puste");
            valid = false;
        } else {
            codeInput.setError(null);
        }

        if (newPassword.isEmpty()) {
            newPasswordInput.setError("Pole nie może być puste");
            valid = false;
        } else {
            newPasswordInput.setError(null);
        }

        if (passwordRepeat.isEmpty()) {
            passwordRepeatInput.setError("Pole nie może być puste");
            valid = false;
        } else {
            passwordRepeatInput.setError(null);
        }

        if (newPassword != null && !newPassword.equals(passwordRepeat)) {
            passwordRepeatInput.setError("Hasła muszą być takie same");
            valid = false;
        }

        return valid;
    }
}
