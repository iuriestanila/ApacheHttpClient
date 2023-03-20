package restAssured.client;

import apacheHttpClient.client.Client;
import apacheHttpClient.client.UserClient;
import apacheHttpClient.enums.AccessType;
import apacheHttpClient.pojo.User;
import apacheHttpClient.pojo.UserToUpdate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import restAssured.AuthRAssured;

import java.util.List;

import static io.restassured.RestAssured.given;

public class UserClientAssured {

    public List<String> getUsersListStringAssured(){
        List<String> users = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when()
                .get(Client.BASE_URL_2 + UserClient.USERS_ENDPOINT)
                .then()
                .extract()
                .body()
                .jsonPath()
                .getList("$");
        return users;
    }

    public List<User> getUsersListUserAssured(){
        List<User> users = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when()
                .get(Client.BASE_URL_2 + UserClient.USERS_ENDPOINT)
                .then()
                .extract()
                .body()
                .jsonPath()
                .getList("$", User.class);
        return users;
    }

    @SneakyThrows
    public List<User> deserializeJsonInUser(String responseBody){
        List<User> users = new ObjectMapper().readValue(responseBody, new TypeReference<>() {});
        return users;
    }

    @SneakyThrows
    public String serializeUserInJson(UserToUpdate userToUpdate){
        String requestBodyJSon = new ObjectMapper().writeValueAsString(userToUpdate);
        return requestBodyJSon;
    }

    @SneakyThrows
    public String serializeUserInJson(User user){
        String requestBodyJSon = new ObjectMapper().writeValueAsString(user);
        return requestBodyJSon;
    }
}
