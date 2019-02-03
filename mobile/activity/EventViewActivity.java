package pl.inz.costshare.mobile.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.github.kittinunf.fuel.core.FuelError;
import pl.inz.costshare.mobile.data.AppContext;
import pl.inz.costshare.mobile.data.UserAndPassword;
import pl.inz.costshare.mobile.dto.EventDto;
import pl.inz.costshare.mobile.dto.EventUserDto;
import pl.inz.costshare.mobile.dto.ReceiptDto;
import pl.inz.costshare.mobile.dto.UserDto;
import pl.inz.costshare.mobile.service.EventService;
import pl.inz.costshare.mobile.service.GroupService;
import pl.inz.costshare.mobile.service.ResponseHandler;
import pl.inz.costshare.mobile.tools.Tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventViewActivity extends Activity {

    private static final int READ_REQUEST_CODE = 42;

    private final Activity thisActivity = this;
    private EventService eventService = new EventService();
    private GroupService groupService = new GroupService();
    private Long eventId;
    private Long groupId;

    private ArrayAdapter eventUserListAdapter;

    private EventDto event;
    private List<ReceiptDto> receipts = new ArrayList<>();

    private Map<Long, Bitmap> receiptsBitMaps = new HashMap<>();
    private Map<Long, UserDto> usersInGroup = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);

        eventId = getIntent().getLongExtra("eventId", -1);
        groupId = getIntent().getLongExtra("groupId", -1);

        eventUserListAdapter = new EventUserAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<EventUserDto>());
        ListView usersEventListView = findViewById(R.id.ListViewEventUsersToSettle);
        usersEventListView.setAdapter(eventUserListAdapter);

        loadEvent();
        loadGroupUsers();
        loadReceipts();

        findViewById(R.id.addReceiptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                selectFile();
            }
        });
    }

    private void loadGroupUsers() {
        UserAndPassword userAndPassword;
        AppContext appContext = AppContext.getInstance(thisActivity);
        userAndPassword = appContext.getUserAndPassword();

        groupService.getUsersInGroup(groupId, userAndPassword, new ResponseHandler<List<UserDto>>(thisActivity) {
            @Override
            public void success(List<UserDto> value) {
                usersInGroup.clear();
                for (UserDto userDto : value) {
                    usersInGroup.put(userDto.getId(), userDto);
                }
            }

            @Override
            public void failure(FuelError error, String message) {
                usersInGroup.clear();
                Tools.error(getApplicationContext(), message);
            }
        });
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    private void loadEvent() {
        AppContext appContext = AppContext.getInstance(thisActivity);
        UserAndPassword userAndPassword = appContext.getUserAndPassword();
        eventService.getEvent(eventId, userAndPassword, new ResponseHandler<EventDto>(thisActivity) {
            @Override
            public void success(EventDto value) {
                event = value;
                for (EventUserDto eventUserDto : event.getEventUsers()) {
                    if (!"creditor".equals(eventUserDto.getUserEventType()))
                        eventUserListAdapter.add(eventUserDto);
                }
            }

            @Override
            public void failure(FuelError error, String message) {
                Tools.error(getApplicationContext(), message);
            }
        });
    }

    private void loadReceipts() {
        AppContext appContext = AppContext.getInstance(thisActivity);
        final UserAndPassword userAndPassword = appContext.getUserAndPassword();
        eventService.getEventReceipts(eventId, userAndPassword, new ResponseHandler<List<ReceiptDto>>(thisActivity) {
            @Override
            public void success(List<ReceiptDto> value) {
                receipts = value;

                for (final ReceiptDto receiptDto : receipts) {
                    if (receiptsBitMaps.get(receiptDto.getId()) != null) {
                        continue;
                    }
                    eventService.downloadReceipt(receiptDto.getId(), userAndPassword, new ResponseHandler<byte[]>(thisActivity) {
                        @Override
                        public void success(byte[] bytes) {
                            addBitmap(receiptDto.getId(), bytes);
                            repaintImages();
                        }

                        @Override
                        public void failure(FuelError error, String message) {
                            Tools.error(getApplicationContext(), message);
                            Log.e("DOWNLOAD", Log.getStackTraceString(error.getException()));
                        }
                    });
                }
            }

            @Override
            public void failure(FuelError error, String message) {
                Tools.error(getApplicationContext(), message);
            }
        });
    }

    private void repaintImages() {
        LinearLayout layout = findViewById(R.id.receiptImages);
        layout.removeAllViews();
        for (ReceiptDto receiptDto : receipts) {
            ImageView image = new ImageView(thisActivity);
            Bitmap bitmap = receiptsBitMaps.get(receiptDto.getId());
            if (bitmap == null) {
                continue;
            }
            image.setImageBitmap(bitmap);
            image.setPadding(0, 0, 0, 15);
            image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.addView(image);
        }
        layout.invalidate();
    }

    private void addBitmap(Long receiptId, byte[] bytes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        int orgWidth = options.outWidth;

        int dstWidth = 400;
        options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inSampleSize = 1;
        options.inDensity = orgWidth;
        options.inTargetDensity = dstWidth * options.inSampleSize;

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        receiptsBitMaps.put(receiptId, bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                try {
                    uri = resultData.getData();
                    Log.i("Select file", "Uri: " + uri.toString());
                    byte[] fileBytes = loadFile(uri);
                    String fileName = getFileName(uri);
                    uploadEventReceipt(fileName, fileBytes);
                } catch (Exception e) {
                    Tools.error(getApplicationContext(), e.getMessage());
                    Log.e("UPLOAD", Log.getStackTraceString(e));
                }
            }
        }
    }

    private void uploadEventReceipt(String fileName, final byte[] rawData) {
        AppContext appContext = AppContext.getInstance(thisActivity);
        UserAndPassword userAndPassword = appContext.getUserAndPassword();

        Bitmap originalBitmap = BitmapFactory.decodeByteArray(rawData, 0, rawData.length);
        double maxDimension = 1200.0;
        double ratio;

        if (originalBitmap.getWidth() > originalBitmap.getHeight()) {
            ratio = originalBitmap.getWidth() / maxDimension;
        } else {
            ratio = originalBitmap.getHeight() / maxDimension;
        }

        double newWidth = originalBitmap.getWidth() / ratio;
        double newHeight = originalBitmap.getHeight() / ratio;

        Bitmap scaledData = Bitmap.createScaledBitmap(originalBitmap, (int) newWidth, (int) newHeight, true);
        originalBitmap.recycle();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scaledData.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        final byte[] byteArray = stream.toByteArray();
        scaledData.recycle();

        eventService.uploadReceipt(eventId, fileName, byteArray, userAndPassword, new ResponseHandler<ReceiptDto>(thisActivity) {
            @Override
            public void success(ReceiptDto value) {
                receipts.add(value);
                addBitmap(value.getId(), byteArray);
                repaintImages();
            }

            @Override
            public void failure(FuelError error, String message) {
                Tools.error(getApplicationContext(), message);
            }
        });
    }

    private byte[] loadFile(Uri uri) {
        byte buffer[] = new byte[1024 * 1024];
        InputStream in = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            in = getContentResolver().openInputStream(uri);
            int read = 0;
            while ((read = in.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, read);
            }
        } catch (Exception e) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            Log.e("LOAD_FILE", Log.getStackTraceString(e));
        }
        return baos.toByteArray();
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    //***************************************************//

    private boolean isCurentUserCreditor() {
        AppContext appContext = AppContext.getInstance(thisActivity);
        Long currentUserId = appContext.getUser().getId();
        for (EventUserDto eventUserDto : event.getEventUsers()) {
            if (currentUserId.equals(eventUserDto.getUserId()) && "creditor".equals(eventUserDto.getUserEventType())) {
                return true;
            }
        }
        return false;
    }

    class EventUserAdapter extends ArrayAdapter<EventUserDto> {

        public EventUserAdapter(@NonNull Context context, int resource, @NonNull List<EventUserDto> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ListViewHolder holder;

            final EventUserDto eventUserDto = this.getItem(position);

            if (convertView == null) {
                holder = new ListViewHolder();
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.settle_event_user_lv_item, parent, false);
                holder.cost = convertView.findViewById(R.id.event_user_cost);
                holder.label = convertView.findViewById(R.id.event_user_name);
                holder.progressBar = convertView.findViewById(R.id.settleProgressBar);
                holder.progressBar.setVisibility(View.GONE);

                holder.settledSwitch = convertView.findViewById(R.id.settleBtn);
                holder.settledSwitch.setChecked(Boolean.TRUE.equals(eventUserDto.getSettled()));
                if (!isCurentUserCreditor()) {
                    holder.settledSwitch.setVisibility(View.INVISIBLE);
                }

                convertView.setTag(holder);

                holder.settledSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Boolean.TRUE.equals(eventUserDto.getSettled())) {
                            holder.settledSwitch.setChecked(true);
                            return;
                        }

                        holder.progressBar.setVisibility(View.VISIBLE);
                        AppContext appContext = AppContext.getInstance(thisActivity);
                        final UserAndPassword userAndPassword = appContext.getUserAndPassword();
                        eventService.settleUpEventUser(eventUserDto.getEventId(), eventUserDto.getUserId(), userAndPassword, new ResponseHandler<EventDto>(thisActivity) {
                            @Override
                            public void success(EventDto value) {
                                holder.progressBar.setVisibility(View.GONE);
                                eventUserDto.setSettled(true);
                                Tools.info(getApplicationContext(), "Settled !");
                            }

                            @Override
                            public void failure(FuelError error, String message) {
                                holder.progressBar.setVisibility(View.GONE);
                                eventUserDto.setSettled(false);
                                Tools.error(getApplicationContext(), message);
                            }
                        });
                    }
                });

                UserDto userDto = usersInGroup.get(eventUserDto.getUserId());
                if (userDto != null) {
                    holder.label.setText(userDto.getFirstName() + " " + userDto.getLastName());
                }
                holder.cost.setText(eventUserDto.getCost().toString());
            }

            return convertView;
        }

        private class ListViewHolder {
            TextView label;
            TextView cost;
            ProgressBar progressBar;
            Switch settledSwitch;
        }
    }


}

