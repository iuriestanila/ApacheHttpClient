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
    public ResponseEntity<List<User>> getUsers(int olderThan, String sex, int youngerThan) {
        String endpoint = "/users?";
        if(olderThan > 0 && youngerThan > 0){
            throw new RuntimeException("Parameters youngerThan and olderThan cannot be specified together");
        } else if(olderThan > 0){
            endpoint += "olderThan=" +olderThan;
        } else if (youngerThan > 0) {
            endpoint += "youngerThan=" +youngerThan;
        }

        if(sex != null) {
            switch (sex) {
                case "MALE" -> endpoint += "&sex=MALE";
                case "FEMALE" -> endpoint += "&sex=FEMALE";
                default -> {
                }
            }
        }

        final HttpResponse httpResponse = Client.doGet(endpoint);

        ResponseEntity<List<User>> response = new ResponseEntity<>();
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        List<User> appUsers = new ObjectMapper().readValue(httpResponse.getEntity().getContent(), new TypeReference<>() {
        });
        response.setBody(appUsers);
        return response;
    }
}
