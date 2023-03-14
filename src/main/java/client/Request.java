package client;

import enums.HttpMethod;
import lombok.SneakyThrows;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.URI;
import java.util.Base64;


public class Request {
    private final HttpRequest request;
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();

    private Request(String url, HttpMethod method) {
        switch (method) {
            case GET -> request = new HttpGet(url);
            case PUT -> request = new HttpPut(url);
            case POST -> request = new HttpPost(url);
            case DELETE -> request = new HttpDeleteWithBody(url);
            case PATCH -> request = new HttpPatch(url);
            default -> throw new RuntimeException("Unexpected value: " + method);
        }
    }

    public static Request get(String url) {
        return new Request(url, HttpMethod.GET);
    }

    public static Request post(String url) {
        return new Request(url, HttpMethod.POST);
    }

    public static Request patch(String url) {
        return new Request(url, HttpMethod.PATCH);
    }

    public static Request put(String url) {
        return new Request(url, HttpMethod.PUT);
    }

    public static Request delete(String url) {
        return new Request(url, HttpMethod.DELETE);
    }
    @SneakyThrows
    public Request addParameter(String key, String value) {
        URI uri = new URIBuilder(request.getRequestLine().getUri()).addParameter(key, value).build();
        ((HttpRequestBase) request).setURI(uri);
        return this;
    }

    public Request addHeader(String key, String value) {
        request.addHeader(key, value);
        return this;
    }

    public Request addBasicAuth(String username, String password) {
        request.addHeader("Authorization", "Basic " + Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes()));
        return this;
    }

    public Request addBearerToken(String token) {
        request.addHeader("Authorization", "Bearer " + token);
        return this;
    }

    public Request addJsonBody(String json) {
        ((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        return this;
    }

    @SneakyThrows
    public HttpResponse execute() {
        return (httpClient.execute((HttpUriRequest) request));
    }
}