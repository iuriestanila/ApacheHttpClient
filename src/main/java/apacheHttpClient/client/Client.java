package apacheHttpClient.client;

import apacheHttpClient.enums.AccessType;
import org.apache.http.HttpResponse;

import java.io.File;

public class Client {
    public final static String BASE_URL = System.getProperty("baseUrl", "http://localhost:49900");

    public static HttpResponse doGet(String endpoint) {
        return Request.get(BASE_URL + endpoint)
                .addBearerToken(AuthClient.getToken(AccessType.READ))
                .execute();
    }


    public static HttpResponse doGet(String endpoint, String key, String value) {
        return Request.get(BASE_URL + endpoint)
                .addParameter(key, value)
                .addBearerToken(AuthClient.getToken(AccessType.READ))
                .execute();
    }

    public static HttpResponse doPost(String endpoint, String body) {
        return Request.post(BASE_URL + endpoint)
                .addBearerToken(AuthClient.getToken(AccessType.WRITE))
                .addHeader("Content-Type", "application/json")
                .addJsonBody(body)
                .execute();
    }

    public static HttpResponse doPost(String endpoint, File file) {
        return Request.post(BASE_URL + endpoint)
                .addBearerToken(AuthClient.getToken(AccessType.WRITE))
                .attachFileToBody(file)
                .execute();
    }

    public static HttpResponse doPatch(String endpoint, String body) {
        return Request.patch(BASE_URL + endpoint)
                .addBearerToken(AuthClient.getToken(AccessType.WRITE))
                .addHeader("Content-Type", "application/json")
                .addJsonBody(body)
                .execute();
    }

    public static HttpResponse doPut(String endpoint, String body) {
        return Request.put(BASE_URL + endpoint)
                .addBearerToken(AuthClient.getToken(AccessType.WRITE))
                .addHeader("Content-Type", "application/json")
                .addJsonBody(body)
                .execute();
    }

    public static HttpResponse doDelete(String endpoint, String body){
        return Request.delete(BASE_URL + endpoint)
                .addBearerToken(AuthClient.getToken(AccessType.WRITE))
                .addHeader("Content-Type", "application/json")
                .addJsonBody(body)
                .execute();
    }
}
