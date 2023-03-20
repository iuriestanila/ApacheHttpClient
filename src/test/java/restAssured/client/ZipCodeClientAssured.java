package restAssured.client;

import apacheHttpClient.client.Client;
import apacheHttpClient.client.ZipCodeClient;
import apacheHttpClient.enums.AccessType;
import restAssured.AuthRAssured;

import java.util.List;

import static io.restassured.RestAssured.given;

public class ZipCodeClientAssured {
    public List<String> getZipCodesAssured() {
        List<String> zipCodes = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when()
                .get(Client.BASE_URL_2 + ZipCodeClient.GET_ZIPCODES_ENDPOINT)
                .then()
                .extract()
                .body()
                .jsonPath()
                .getList("$");
        return zipCodes;
    }
}
