package client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import pojo.ResponseEntity;
import pojo.User;
import pojo.UserToUpdate;

import java.io.File;
import java.util.List;

public class UserClient {
    private static final String USERS_ENDPOINT = "/users";
    private static final String USERS_UPLOAD_ENDPOINT = "/users/upload";
    private final ObjectMapper objectMapper;

    public UserClient() {
        objectMapper = new ObjectMapper();
    }

    @SneakyThrows
    @Step
    public int postUser(User user) {
        String requestBodyJSon = objectMapper.writeValueAsString(user);
        HttpResponse httpResponse = Client.doPost(USERS_ENDPOINT, requestBodyJSon);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        EntityUtils.consumeQuietly(httpResponse.getEntity());
        return statusCode;
    }

    @SneakyThrows
    @Step
    public ResponseEntity<String> postUsers(File file) {
        ResponseEntity<String> responseEntity = new ResponseEntity<>();
        HttpResponse httpResponse = Client.doPost(USERS_UPLOAD_ENDPOINT, file);
        responseEntity.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        responseEntity.setBody(EntityUtils.toString(httpResponse.getEntity()));
        return responseEntity;
    }

    @SneakyThrows
    @Step
    public int deleteUser(User userToDelete) {
        String requestBodyJSon = objectMapper.writeValueAsString(userToDelete);
        HttpResponse httpResponse = Client.doDelete(USERS_ENDPOINT, requestBodyJSon);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        EntityUtils.consumeQuietly(httpResponse.getEntity());
        return statusCode;
    }

    @Step
    public ResponseEntity<List<User>> getUsers() {
        final HttpResponse httpResponse = Client.doGet(USERS_ENDPOINT);
        return handleResponse(httpResponse);
    }


    @Step
    public ResponseEntity<List<User>> getUsers(String key, String value) {
        final HttpResponse httpResponse = Client.doGet(USERS_ENDPOINT, key, value);
        return handleResponse(httpResponse);
    }

    @Step
    @SneakyThrows
    public int patchUser(UserToUpdate userToUpdate) {
        String requestBodyJSon = objectMapper.writeValueAsString(userToUpdate);
        HttpResponse httpResponse = Client.doPatch(USERS_ENDPOINT, requestBodyJSon);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        EntityUtils.consumeQuietly(httpResponse.getEntity());
        return statusCode;
    }

    @Step
    @SneakyThrows
    public int putUser(UserToUpdate userToUpdate) {
        String requestBodyJSon = objectMapper.writeValueAsString(userToUpdate);
        HttpResponse httpResponse = Client.doPut(USERS_ENDPOINT, requestBodyJSon);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        EntityUtils.consumeQuietly(httpResponse.getEntity());
        return statusCode;
    }

    @SneakyThrows
    private ResponseEntity<List<User>> handleResponse(HttpResponse httpResponse){
        ResponseEntity<List<User>> response = new ResponseEntity<>();
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        List<User> appUsers = new ObjectMapper().readValue(httpResponse.getEntity().getContent(), new TypeReference<>() {});
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
}
