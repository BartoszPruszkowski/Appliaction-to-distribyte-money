package pl.inz.costshare.mobile.service;

import android.util.Log;
import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import pl.inz.costshare.mobile.data.UserAndPassword;
import pl.inz.costshare.mobile.dto.*;

import java.util.List;

public class UserService {

    public void getAllUsers(UserAndPassword userAndPassword, final ResponseHandler<List<UserDto>> handler) {
        Log.d("UserService", "getAllUsers start");
        Fuel.get(prepareUrl("/users")).authenticate(userAndPassword.getUserNanme(), userAndPassword.getPassword()).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                handler.handleResponse(null, response, error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                handler.handleResponse(Mapper.<UserDto>mapDataToObjectList(response.getData(), UserDto.class), response, null);
            }
        });
        Log.d("UserService", "getAllUsers end");
    }

    public void getMe(String userName, String password, final ResponseHandler<UserDto> handler) {
        Log.d("UserService", "getMe start");
        Fuel.get(prepareUrl("/users/me")).authenticate(userName, password).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                handler.handleResponse(null, response, error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                handler.handleResponse(Mapper.mapDataToObject(response.getData(), UserDto.class), response, null);
            }
        });
        Log.d("UserService", "getMe end");
    }

    public void getUser(Long id, UserAndPassword userAndPassword, final ResponseHandler<UserDto> handler) {
        Log.d("UserService", "getUser start");
        Fuel.get(prepareUrl("/users/" + id)).authenticate(userAndPassword.getUserNanme(), userAndPassword.getPassword()).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                handler.handleResponse(null, response, error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                handler.handleResponse(Mapper.mapDataToObject(response.getData(), UserDto.class), response, null);
            }
        });
        Log.d("UserService", "getUser end");
    }

    public void getGroupsForUser(Long userId, UserAndPassword userAndPassword, final ResponseHandler<List<GroupDto>> handler) {
        Log.d("UserService", "getGroupsForUser start");
        Fuel.get(prepareUrl("/users/" + userId + "/groups")).authenticate(userAndPassword.getUserNanme(), userAndPassword.getPassword()).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                handler.handleResponse(null, response, error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                handler.handleResponse(Mapper.<GroupDto>mapDataToObjectList(response.getData(), GroupDto.class), response, null);
            }
        });
        Log.d("UserService", "getGroupsForUser end");
    }


    public void createUser(CreateUserDto createUserDto, final ResponseHandler<UserDto> handler) {
        Log.d("UserService", "createUser start");
        byte[] body = Mapper.mapObjectToData(createUserDto);
        Fuel.post(prepareUrl("/users")).body(body).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                handler.handleResponse(null, response, error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                handler.handleResponse(Mapper.mapDataToObject(response.getData(), UserDto.class), response, null);
            }
        });
        Log.d("UserService", "createUser end");
    }

    public void findUserByName(String userName, UserAndPassword userAndPassword, final ResponseHandler<UserDto> handler) {
        Log.d("UserService", "getUser start");
        Fuel.get(prepareUrl("/users/search?userName=" + userName)).authenticate(userAndPassword.getUserNanme(), userAndPassword.getPassword()).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                handler.handleResponse(null, response, error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                handler.handleResponse(Mapper.mapDataToObject(response.getData(), UserDto.class), response, null);
            }
        });
        Log.d("UserService", "getUser end");
    }

    public void addUserToGroup(Long userId, Long groupId, Boolean ifAdmin, UserAndPassword userAndPassword, final ResponseHandler<Object> handler) {
        Log.d("GroupService", "addUserToGroup start");
        AddUserToGroupDto addUserToGroupDto = new AddUserToGroupDto();
        addUserToGroupDto.setAdmin(ifAdmin);
        addUserToGroupDto.setId(groupId);
        byte[] body = Mapper.mapObjectToData(addUserToGroupDto);
        Fuel.post(prepareUrl("/users/" + userId + "/groups")).authenticate(userAndPassword.getUserNanme(), userAndPassword.getPassword())
            .body(body).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                handler.handleResponse(null, response, error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                handler.handleResponse(null, response, null);
            }
        });
        Log.d("UserService", "addUserToGroup end");
    }

    public void resetPasswordStart(String userName, final ResponseHandler<Object> handler) {
        Log.d("UserService", "resetPasswordStart start");
        ResetPasswordStartDto resetPasswordStartDto = new ResetPasswordStartDto();
        resetPasswordStartDto.setUserName(userName);
        byte[] body = Mapper.mapObjectToData(resetPasswordStartDto);

        Fuel.post(prepareUrl("/users/reset-password-start")).body(body).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                handler.handleResponse(null, response, error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                handler.handleResponse(data, response, null);
            }
        });
        Log.d("UserService", "resetPasswordStart end");
    }

    public void resetPasswordFinish(ResetPasswordFinishDto resetPasswordFinishDto, final ResponseHandler<Object> handler) {
        Log.d("UserService", "resetPasswordFinish start");
        byte[] body = Mapper.mapObjectToData(resetPasswordFinishDto);

        Fuel.post(prepareUrl("/users/reset-password-finish")).body(body).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                handler.handleResponse(null, response, error);
            }

            @Override
            public void success(Request request, Response response, String data) {
                handler.handleResponse(data, response, null);
            }
        });
        Log.d("UserService", "resetPasswordFinish end");
    }

    private String prepareUrl(String part) {
        return ServiceConfig.getBaseUrl() + part;
    }

}
