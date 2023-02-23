package client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import pojo.ResponseEntity;
import pojo.User;

import java.util.List;

public class UserClient {
    private static final String POST_USERS_ENDPOINT = "/users";
    private static final String GET_USERS_ENDPOINT = "/users?";
    private final ObjectMapper objectMapper;

    public UserClient() {
        objectMapper = new ObjectMapper();
    }

    @SneakyThrows
    public int postUser(User user) {
        String requestBodyJSon = objectMapper.writeValueAsString(user);
        HttpResponse httpResponse = Client.doPost(POST_USERS_ENDPOINT, requestBodyJSon);
        return httpResponse.getStatusLine().getStatusCode();
    }

    @SneakyThrows
    public ResponseEntity<List<User>> getUsers(String key, String value) {
        final HttpResponse httpResponse = Client.doGet(GET_USERS_ENDPOINT, key, value);
        ResponseEntity<List<User>> response = new ResponseEntity<>();
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        List<User> appUsers = new ObjectMapper().readValue(httpResponse.getEntity().getContent(), new TypeReference<>() {
        });
        response.setBody(appUsers);
        return response;
    }
}
