package pl.inz.costshare.mobile.service;

import android.util.Log;
import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import pl.inz.costshare.mobile.data.UserAndPassword;
import pl.inz.costshare.mobile.dto.GroupDto;
import pl.inz.costshare.mobile.dto.UserDto;

import java.util.List;

public class GroupService {


    private String prepareUrl(String part) {
        return ServiceConfig.getBaseUrl() + part;
    }


    public void createGroup(GroupDto groupDto, UserAndPassword userAndPassword, final ResponseHandler<GroupDto> handler) {
        Log.d("GroupService", "createGroup start");

        byte[] body = Mapper.mapObjectToData(groupDto);
        Fuel.post(prepareUrl("/groups")).authenticate(userAndPassword.getUserNanme(), userAndPassword.getPassword())
            .body(body).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                handler.handleResponse(null, response, error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                handler.handleResponse(Mapper.mapDataToObject(response.getData(), GroupDto.class), response, null);
            }
        });
        Log.d("GroupService", "createGroup end");
    }

    public void getUsersInGroup(Long groupId, UserAndPassword userAndPassword, final ResponseHandler<List<UserDto>> handler) {
        Log.d("GroupService", "getUsersInGroup start");
        Fuel.get(prepareUrl("/groups/" + groupId + "/users"))
                .authenticate(userAndPassword.getUserNanme(), userAndPassword.getPassword()).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                handler.handleResponse(null, response, error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                handler.handleResponse(Mapper.<UserDto>mapDataToObjectList(response.getData(), UserDto.class), response, null);
            }
        });
        Log.d("GroupService", "getUsersInGroup end");
    }

}
