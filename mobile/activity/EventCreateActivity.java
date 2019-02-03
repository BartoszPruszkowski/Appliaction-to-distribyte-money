package pl.inz.costshare.mobile.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.github.kittinunf.fuel.core.FuelError;
import pl.inz.costshare.mobile.data.AppContext;
import pl.inz.costshare.mobile.data.UserAndPassword;
import pl.inz.costshare.mobile.dto.EventDto;
import pl.inz.costshare.mobile.dto.EventUserDto;
import pl.inz.costshare.mobile.dto.UserDto;
import pl.inz.costshare.mobile.service.EventService;
import pl.inz.costshare.mobile.service.GroupService;
import pl.inz.costshare.mobile.service.ResponseHandler;
import pl.inz.costshare.mobile.tools.Tools;

import java.util.ArrayList;
import java.util.List;

public class EventCreateActivity extends Activity {
    EventService eventService = new EventService();
    GroupService groupService = new GroupService();

    private ArrayAdapter eventUserListAdapter;
    private final Activity thisActivity = this;
    private Long groupId;

    private List<UserDto> usersInGroup = new ArrayList<>();
    private List<EventUserDto> eventUsers = new ArrayList<>();

    private TextInputLayout editTextEventName;
    private TextView totalCostTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

        groupId = getIntent().getLongExtra("groupId", -1);

        editTextEventName = findViewById(R.id.editTextEventName);
        totalCostTextView = findViewById(R.id.totalcost);

        eventUserListAdapter = new EventUserAdapter(this, android.R.layout.simple_list_item_1, eventUsers);
        ListView usersEventListView = findViewById(R.id.ListViewEventUsers);
        usersEventListView.setAdapter(eventUserListAdapter);

        loadUsers();

        findViewById(R.id.buttonCreateEvent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                createEvent();
            }
        });
    }

    private void loadUsers() {
        UserAndPassword userAndPassword;
        AppContext appContext = AppContext.getInstance(thisActivity);
        userAndPassword = appContext.getUserAndPassword();

        groupService.getUsersInGroup(groupId, userAndPassword, new ResponseHandler<List<UserDto>>(thisActivity) {
            @Override
            public void success(List<UserDto> value) {
                usersInGroup.clear();
                usersInGroup.addAll(value);
                resfreshList();
            }

            @Override
            public void failure(FuelError error, String message) {
                usersInGroup.clear();
                resfreshList();
                Tools.error(getApplicationContext(), message);
            }
        });
    }

    private void resfreshList() {
        eventUserListAdapter.clear();
        for (UserDto userDto : usersInGroup) {
            EventUserDto eventUserDto = new EventUserDto();
            eventUserDto.setCost(0.0);
            eventUserDto.setUserId(userDto.getId());
            eventUserListAdapter.add(eventUserDto);
        }
    }

    private void createEvent() {
        if (!validate()) {
            return;
        }

        String eventName = editTextEventName.getEditText().getText().toString().trim();

        EventDto eventDto = new EventDto();
        eventDto.setGroupId(groupId);
        eventDto.setName(eventName);

        List<EventUserDto> newEventUsers = new ArrayList<>();
        for (EventUserDto eventUserDto : eventUsers) {
            if (eventUserDto.getCost() > 0.0) {
                newEventUsers.add(eventUserDto);
            }
        }

        eventDto.setEventUsers(newEventUsers);

        final AppContext appContext = AppContext.getInstance(thisActivity);
        UserAndPassword userAndPassword = appContext.getUserAndPassword();

        eventService.createEvent(eventDto, userAndPassword, new ResponseHandler<EventDto>(thisActivity) {
            @Override
            public void success(EventDto value) {
                finish();
            }

            @Override
            public void failure(FuelError error, String message) {
                Tools.error(getApplicationContext(), message);
            }
        });
    }

    class EventUserAdapter extends ArrayAdapter<EventUserDto> {

        public EventUserAdapter(@NonNull Context context, int resource, @NonNull List<EventUserDto> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ListViewHolder holder;

            final EventUserDto eventUserDto = this.getItem(position);
            final UserDto userDto = usersInGroup.get(position);

            if (convertView == null) {
                holder = new ListViewHolder();
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.event_user_lv_item, parent, false);
                holder.editText = (EditText) convertView.findViewById(R.id.event_user_cost);
                holder.label = (TextView) convertView.findViewById(R.id.event_user_name);
                convertView.setTag(holder);

                holder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            Double cost = 0.0;
                            try {
                                cost = Double.parseDouble(holder.editText.getText().toString());
                                if (cost < 0.0) {
                                    cost = 0.0;
                                    holder.editText.setText(cost.toString());
                                }
                            } catch (Exception e) {
                                cost = 0.0;
                                holder.editText.setText(cost.toString());
                            }
                            eventUserDto.setCost(cost);

                            Double sum = 0.0;
                            for (EventUserDto eventUserDto : eventUsers) {
                                sum += eventUserDto.getCost();
                            }
                            totalCostTextView.setText("Suma:" + sum.toString());
                        }
                    }
                });

                holder.label.setText(userDto.getFirstName() + " " + userDto.getLastName());
                holder.editText.setText(eventUserDto.getCost().toString());
            }

            return convertView;
        }

        private class ListViewHolder {
            TextView label;
            EditText editText;
        }
    }

    private boolean validate() {
        boolean valid = true;

        double sum = 0.0;
        for (EventUserDto eventUserDto : eventUsers) {
            sum += eventUserDto.getCost();
        }
        if (sum <= 0.0) {
            valid = false;
            Tools.error(getApplicationContext(), "Suma nie może być równa zero");
        }

        String name = editTextEventName.getEditText().getText().toString().trim();
        if (name.isEmpty()) {
            editTextEventName.setError("Pole nie może być puste");
            valid = false;
        } else {
            editTextEventName.setError(null);
        }
        return valid;
    }

}
