package pl.inz.costshare.mobile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import com.github.kittinunf.fuel.core.FuelError;
import pl.inz.costshare.mobile.data.AppContext;
import pl.inz.costshare.mobile.data.UserAndPassword;
import pl.inz.costshare.mobile.dto.GroupDto;
import pl.inz.costshare.mobile.service.GroupService;
import pl.inz.costshare.mobile.service.ResponseHandler;
import pl.inz.costshare.mobile.tools.Tools;

public class GroupCreateActivity extends Activity {

    private TextInputLayout editTextGroupName;
    private final Activity thisActivity = this;
    GroupService groupService = new GroupService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        editTextGroupName = findViewById(R.id.editTextGroupName);

        findViewById(R.id.buttonCreateGroup1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (!validateInput()) {
                    return;
                }

                final String groupName = editTextGroupName.getEditText().getText().toString().trim();
                GroupDto groupDto = new GroupDto();
                groupDto.setGroupName(groupName);
                UserAndPassword userAndPassword;
                AppContext appContext = AppContext.getInstance(thisActivity);
                userAndPassword = appContext.getUserAndPassword();
                groupService.createGroup(groupDto, userAndPassword, new ResponseHandler<GroupDto>(thisActivity) {
                    @Override
                    public void success(GroupDto value) {
                        finish();
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
        String loginInput = editTextGroupName.getEditText().getText().toString().trim();
        if (loginInput.isEmpty()) {
            editTextGroupName.setError("Pole nie może być puste");
            return false;
        } else {
            editTextGroupName.setError(null);
            return true;
        }
    }
}

