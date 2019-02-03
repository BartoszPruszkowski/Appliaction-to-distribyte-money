package pl.inz.costshare.mobile.service;

public class ServiceConfig {

     private static final String BASE_URL = "http://192.168.100.34:4000/api"; // base URL for emulator only - dom
    // private static final String BASE_URL = "http://212.182.18.163:4000/api";
    //private static final String BASE_URL = "http://212.182.18.172:4000/api";
    //private static final String BASE_URL = "http://212.182.18.162:4000/api"; // base URL for emulator only - uczelnia

    public static String getBaseUrl() {
        return BASE_URL;
    }

}
