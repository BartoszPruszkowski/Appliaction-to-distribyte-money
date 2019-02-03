package pl.inz.costshare.mobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.github.kittinunf.fuel.core.FuelError;
import pl.inz.costshare.mobile.data.AppContext;
import pl.inz.costshare.mobile.data.UserAndPassword;
import pl.inz.costshare.mobile.dto.EventDto;
import pl.inz.costshare.mobile.dto.UserDto;
import pl.inz.costshare.mobile.service.EventService;
import pl.inz.costshare.mobile.service.GroupService;
import pl.inz.costshare.mobile.service.ResponseHandler;
import pl.inz.costshare.mobile.tools.Tools;

import java.util.ArrayList;
import java.util.List;

public class GroupViewActivity extends Activity {

    private final Activity thisActivity = this;
    private GroupService groupService = new GroupService();
    private EventService eventService = new EventService();
    private ArrayAdapter userListAdapter;
    private ArrayAdapter eventListAdapter;
    private Long groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);

        userListAdapter = new ArrayAdapter<UserDto>(this, android.R.layout.simple_list_item_1, new ArrayList<UserDto>());
        ListView usersListView = findViewById(R.id.usersListView);
        usersListView.setAdapter(userListAdapter);

        eventListAdapter = new ArrayAdapter<EventDto>(this, android.R.layout.simple_list_item_1, new ArrayList<EventDto>()) {
            public View getView(int position, View convertView, ViewGroup parent) {
                EventDto item = getItem(position);
                View orgView = super.getView(position, convertView, parent);
                if (Boolean.TRUE.equals(item.getSettled())) {
                    orgView.setBackgroundColor(Color.rgb(0, 150, 50));
                }
                return orgView;
            }
        };
        ListView eventListView = findViewById(R.id.eventListView);
        eventListView.setAdapter(eventListAdapter);

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                EventDto eventDto = (EventDto) eventListAdapter.getItem(index);
                Intent intent = new Intent(thisActivity, EventViewActivity.class);
                intent.putExtra("eventId", eventDto.getId());
                intent.putExtra("groupId", groupId);
                startActivity(intent);
            }
        });

        groupId = getIntent().getLongExtra("groupId", -1);
        String groupName = getIntent().getStringExtra("groupName");
        TextView textViewGroupName = (TextView) findViewById(R.id.textViewGroupName);
        textViewGroupName.setText(groupName);
        loadUsers();
        loadEvents();
        findViewById(R.id.buttonAdMember).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(thisActivity, AddUserToGroupActivity.class);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonCreateEvent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(thisActivity, EventCreateActivity.class);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUsers();
        loadEvents();
    }

    private void loadUsers() {
        UserAndPassword userAndPassword;
        AppContext appContext = AppContext.getInstance(thisActivity);
        userAndPassword = appContext.getUserAndPassword();


        groupService.getUsersInGroup(groupId, userAndPassword, new ResponseHandler<List<UserDto>>(thisActivity) {
            @Override
            public void success(List<UserDto> value) {
                userListAdapter.clear();
                userListAdapter.addAll(value);
            }

            @Override
            public void failure(FuelError error, String message) {
                userListAdapter.clear();
                Tools.error(getApplicationContext(), message);
            }
        });
    }

    private void loadEvents() {
        UserAndPassword userAndPassword;
        AppContext appContext = AppContext.getInstance(thisActivity);
        userAndPassword = appContext.getUserAndPassword();

        eventService.getEventsInGroup(groupId, userAndPassword, new ResponseHandler<List<EventDto>>(thisActivity) {
            @Override
            public void success(List<EventDto> value) {
                eventListAdapter.clear();
                eventListAdapter.addAll(value);
            }

            @Override
            public void failure(FuelError error, String message) {
                eventListAdapter.clear();
                Tools.error(getApplicationContext(), message);
            }
        });
    }

}
