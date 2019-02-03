package pl.inz.costshare.mobile.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Mapper {

    static {
        RestInit.initDefaultContentType();
    }

    public static <T> List<T> mapDataToObjectList(byte[] data, Class<T> klass) {
        String str = new String(data);
        Type listType = TypeToken.getParameterized(List.class, klass).getType();
        return new Gson().fromJson(str, listType);
    }

    public static <T> T mapDataToObject(byte[] data, Class<T> klass) {
        return new Gson().fromJson(new String(data), klass);
    }

    public static byte[] mapObjectToData(Object anyObject) {
        return new Gson().toJson(anyObject).getBytes();
    }

}
