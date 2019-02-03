package pl.inz.costshare.mobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.github.kittinunf.fuel.core.FuelError;
import pl.inz.costshare.mobile.data.AppContext;
import pl.inz.costshare.mobile.data.UserAndPassword;
import pl.inz.costshare.mobile.dto.GroupDto;
import pl.inz.costshare.mobile.dto.UserDto;
import pl.inz.costshare.mobile.service.ResponseHandler;
import pl.inz.costshare.mobile.service.UserService;
import pl.inz.costshare.mobile.tools.Tools;

import java.util.ArrayList;
import java.util.List;

public class GroupsListActivity extends Activity {

    private final Activity thisActivity = this;
    private ArrayAdapter groupListAdapter;
    private UserService userService = new UserService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);

        groupListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        ListView groupListView = findViewById(R.id.groupListView);
        groupListView.setAdapter(groupListAdapter);

        loadGroups();

        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                // pobierz element z listy o indexie w zmiennej index
                GroupDto groupDto = (GroupDto) groupListAdapter.getItem(index);
                //
                Intent intent = new Intent(thisActivity, GroupViewActivity.class);
                //przekaz identyfikator grupy do activity
                intent.putExtra("groupId", groupDto.getId());
                intent.putExtra("groupName", groupDto.getGroupName());
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonCreateGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), GroupCreateActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadGroups();
    }

    private void loadGroups() {
        UserAndPassword userAndPassword;
        AppContext appContext = AppContext.getInstance(thisActivity);
        userAndPassword = appContext.getUserAndPassword();
        UserDto userDto = appContext.getUser();
        Long userId = userDto.getId();

        userService.getGroupsForUser(userId, userAndPassword, new ResponseHandler<List<GroupDto>>(thisActivity) {
            @Override
            public void success(List<GroupDto> value) {
                groupListAdapter.clear();
                groupListAdapter.addAll(value);
            }

            @Override
            public void failure(FuelError error, String message) {
                groupListAdapter.clear();
                Tools.error(getApplicationContext(), message);
            }
        });
    }
}
