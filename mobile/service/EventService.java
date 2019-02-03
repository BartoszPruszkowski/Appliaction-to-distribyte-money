package pl.inz.costshare.mobile.service;

import android.util.Log;
import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.*;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import pl.inz.costshare.mobile.data.UserAndPassword;
import pl.inz.costshare.mobile.dto.EventDto;
import pl.inz.costshare.mobile.dto.ReceiptDto;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class EventService {


    private String prepareUrl(String part) {
        return ServiceConfig.getBaseUrl() + part;
    }

    public void createEvent(EventDto eventDto, UserAndPassword userAndPassword, final ResponseHandler<EventDto> handler) {
        Log.d("EventService", "createEvent start");
        byte[] body = Mapper.mapObjectToData(eventDto);
        Fuel.post(prepareUrl("/events")).authenticate(userAndPassword.getUserNanme(), userAndPassword.getPassword())
            .body(body).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                handler.handleResponse(null, response, error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                handler.handleResponse(Mapper.mapDataToObject(response.getData(), EventDto.class), response, null);
            }
        });
        Log.d("EventService", "createEvent end");
    }

    public void getEventsInGroup(Long groupId, UserAndPassword userAndPassword, final ResponseHandler<List<EventDto>> handler) {
        Log.d("EventService", "getEventsInGroup start");
        Fuel.get(prepareUrl("/groups/" + groupId + "/events")).authenticate(userAndPassword.getUserNanme(), userAndPassword.getPassword()).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                handler.handleResponse(null, response, error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                handler.handleResponse(Mapper.<EventDto>mapDataToObjectList(response.getData(), EventDto.class), response, null);
            }
        });
        Log.d("EventService", "getEventsInGroup end");
    }

    public void getEvent(Long eventId, UserAndPassword userAndPassword, final ResponseHandler<EventDto> handler) {
        Log.d("EventService", "getEvent start");
        Fuel.get(prepareUrl("/events/" + eventId)).authenticate(userAndPassword.getUserNanme(), userAndPassword.getPassword()).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                handler.handleResponse(null, response, error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                handler.handleResponse(Mapper.mapDataToObject(response.getData(), EventDto.class), response, null);
            }
        });
        Log.d("EventService", "getEvent end");
    }

    public void uploadReceipt(Long eventId, final String fileName, final byte[] data, UserAndPassword userAndPassword, final ResponseHandler<ReceiptDto> handler) {
        Log.d("EventService", "uploadReceipt start");
        Fuel.upload(prepareUrl("/events/" + eventId + "/receipts"))
            .blob(new Function2<Request, URL, Blob>() {
                @Override
                public Blob invoke(Request request, URL url) {
                    request.setName("file");
                    return new Blob(fileName, data.length, new Function0<InputStream>() {
                        @Override
                        public InputStream invoke() {
                            return new ByteArrayInputStream(data);
                        }
                    });
                }
            })
            .authenticate(userAndPassword.getUserNanme(), userAndPassword.getPassword()).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                handler.handleResponse(null, response, error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                handler.handleResponse(Mapper.mapDataToObject(response.getData(), ReceiptDto.class), response, null);
            }
        });
        Log.d("EventService", "uploadReceipt end");
    }

    public void downloadReceipt(Long receiptId, UserAndPassword userAndPassword, final ResponseHandler<byte[]> handler) {
        Log.d("EventService", "uploadReceipt start");
        Fuel.get(prepareUrl("/receipts/" + receiptId + "/download"))
            .authenticate(userAndPassword.getUserNanme(), userAndPassword.getPassword()).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                handler.handleResponse(null, response, error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                handler.handleResponse(response.getData(), response, null);
            }
        });
        Log.d("EventService", "uploadReceipt end");
    }

    public void getEventReceipts(Long eventId, UserAndPassword userAndPassword, final ResponseHandler<List<ReceiptDto>> handler) {
        Log.d("EventService", "getEvent start");
        Fuel.get(prepareUrl("/events/" + eventId + "/receipts")).authenticate(userAndPassword.getUserNanme(), userAndPassword.getPassword()).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                handler.handleResponse(null, response, error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                handler.handleResponse(Mapper.<ReceiptDto>mapDataToObjectList(response.getData(), ReceiptDto.class), response, null);
            }
        });
        Log.d("EventService", "getEvent end");
    }

    public void settleUpEventUser(Long eventId, Long userId, UserAndPassword userAndPassword, final ResponseHandler<EventDto> handler) {
        Log.d("EventService", "settleUpEventUser start");
        Fuel.post(prepareUrl("/events/" + eventId + "/event-users/" + userId + "/settled"))
            .body("true".getBytes())
            .authenticate(userAndPassword.getUserNanme(), userAndPassword.getPassword()).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                handler.handleResponse(null, response, error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                handler.handleResponse(Mapper.mapDataToObject(response.getData(), EventDto.class), response, null);
            }
        });
        Log.d("EventService", "settleUpEventUser end");
    }

}
