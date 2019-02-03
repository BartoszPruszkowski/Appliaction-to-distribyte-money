package pl.inz.costshare.mobile.service;

import com.github.kittinunf.fuel.core.FuelManager;
import com.github.kittinunf.fuel.core.Request;
import kotlin.jvm.functions.Function1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestInit {

    static public void initDefaultContentType() {
        FuelManager.Companion.getInstance().addRequestInterceptor(new Function1<Function1<? super Request, Request>, Function1<? super Request, Request>>() {
            @Override
            public Function1<? super Request, Request> invoke(final Function1<? super Request, Request> requestFunction1) {
                return new Function1<Request, Request>() {
                    @Override
                    public Request invoke(Request request) {
                        List<String> jsonMethods = new ArrayList<>(Arrays.asList("PUT", "POST"));
                        if (request.getType().equals(Request.Type.UPLOAD)) {
                            // set nothing let the library handle this
                        } else if (jsonMethods.contains(request.getMethod().getValue())) {
                            request.getHeaders().put("Content-Type", "application/json");
                        }
                        return requestFunction1.invoke(request);
                    }
                };
            }
        });
    }

}
