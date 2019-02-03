package pl.inz.costshare.mobile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import com.github.kittinunf.fuel.core.FuelError;
import pl.inz.costshare.mobile.data.AppContext;
import pl.inz.costshare.mobile.data.UserAndPassword;
import pl.inz.costshare.mobile.dto.UserDto;
import pl.inz.costshare.mobile.service.ResponseHandler;
import pl.inz.costshare.mobile.service.UserService;
import pl.inz.costshare.mobile.tools.Tools;

public class AddUserToGroupActivity extends Activity {

    private final Activity thisActivity = this;
    private UserService userService = new UserService();
    private Long groupId;

    private TextInputLayout editTextUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_to_group);
        editTextUserName = findViewById(R.id.editTextUserName);

        groupId = getIntent().getLongExtra("groupId", -1);

        findViewById(R.id.buttonAdToGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!validateInput()) {
                    return;
                }

                final String userName = editTextUserName.getEditText().getText().toString().trim();
                final UserAndPassword userAndPassword;
                final AppContext appContext = AppContext.getInstance(thisActivity);
                userAndPassword = appContext.getUserAndPassword();

                userService.findUserByName(userName, userAndPassword, new ResponseHandler<UserDto>(thisActivity) {
                    @Override
                    public void success(UserDto value) {
                        userService.addUserToGroup(value.getId(), groupId, false, userAndPassword, new ResponseHandler<Object>(thisActivity) {
                            @Override
                            public void success(Object value) {
                                finish();
                            }

                            @Override
                            public void failure(FuelError error, String message) {
                                Tools.error(getApplicationContext(), message);
                            }
                        });
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
        String loginInput = editTextUserName.getEditText().getText().toString().trim();
        if (loginInput.isEmpty()) {
            editTextUserName.setError("Pole nie może być puste");
            return false;
        } else {
            editTextUserName.setError(null);
            return true;
        }
    }
}
