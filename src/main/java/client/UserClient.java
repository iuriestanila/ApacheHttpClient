package client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.HttpResponse;
import pojo.ResponseEntity;
import pojo.User;
import pojo.UserToUpdate;

import java.util.List;

public class UserClient {
    private static final String POST_USERS_ENDPOINT = "/users";
    private static final String GET_USERS_ENDPOINT_PARAM = "/users?";
    private static final String GET_USERS_ENDPOINT = "/users";
    private static final String PATCH_USER_ENDPOINT = "/users";
    private static final String PUT_USER_ENDPOINT = "/users";
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

    public ResponseEntity<List<User>> getUser() {
        final HttpResponse httpResponse = Client.doGet(GET_USERS_ENDPOINT);
        return handleResponse(httpResponse);
    }


    public ResponseEntity<List<User>> getUsersWithParam(String key, String value) {
        final HttpResponse httpResponse = Client.doGet(GET_USERS_ENDPOINT_PARAM, key, value);
        return handleResponse(httpResponse);
    }

    @SneakyThrows
    public int patchUser(UserToUpdate userToUpdate) {
        String requestBodyJSon = objectMapper.writeValueAsString(userToUpdate);
        HttpResponse httpResponse = Client.doPatch(PATCH_USER_ENDPOINT, requestBodyJSon);
        return httpResponse.getStatusLine().getStatusCode();
    }

    @SneakyThrows
    public int putUser(UserToUpdate userToUpdate) {
        String requestBodyJSon = objectMapper.writeValueAsString(userToUpdate);
        HttpResponse httpResponse = Client.doPut(PUT_USER_ENDPOINT, requestBodyJSon);
        return httpResponse.getStatusLine().getStatusCode();
    }

    @SneakyThrows
    private ResponseEntity<List<User>> handleResponse(HttpResponse httpResponse){
        ResponseEntity<List<User>> response = new ResponseEntity<>();
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        List<User> appUsers = new ObjectMapper().readValue(httpResponse.getEntity().getContent(), new TypeReference<>() {
        });
        response.setBody(appUsers);
        return response;
    }

    public User createAvailableUser(String zipcode) {
        User user = new User(RandomUtils.nextInt(0, 100),
                RandomStringUtils.randomAlphabetic(10), "FEMALE", zipcode);
        int status = postUser(user);
        if (status == 201) {
            return user;
        } else {
            throw new RuntimeException("Failed to create available user.");
        }
    }

    public User createRandomUser(String zipcode) {
        return new User(RandomUtils.nextInt(0, 100),
                RandomStringUtils.randomAlphabetic(10), "FEMALE", zipcode);
    }
}
