package client;

import enums.AccessType;
import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import pojo.HttpDeleteWithBody;

public class Client {
    public final static String BASE_URL = "http://localhost:49000";

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

    @SneakyThrows
    public static HttpResponse doDeleteWithBody(String endpoint, String body) {
        HttpDeleteWithBody request = new HttpDeleteWithBody(BASE_URL + endpoint);
        request.addHeader("Authorization", "Bearer " + AuthClient.getToken(AccessType.WRITE));
        request.addHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(request);
        return response;
    }
}
