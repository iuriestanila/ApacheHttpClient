package restAssured;

import apacheHttpClient.client.Client;
import apacheHttpClient.enums.AccessType;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class AuthRAssured {
    private static String writeToken;
    private static String readToken;
    private static final String LOGIN = "0oa157tvtugfFXEhU4x7";
    private static final String PASSWORD = "X7eBCXqlFC7x-mjxG5H91IRv_Bqe1oq7ZwXNA8aq";
    private static final String TOKEN_ENDPOINT = "/oauth/token";

    static {
        writeToken = retrieveToken(AccessType.WRITE);
        readToken = retrieveToken(AccessType.READ);
    }

    private static String retrieveToken(AccessType scope){
            Response response = RestAssured.given()
                    .auth().preemptive().basic(LOGIN, PASSWORD)
                    .param("grant_type", "client_credentials")
                    .param("scope", scope.name().toLowerCase())
                    .post(Client.BASE_URL_2 + TOKEN_ENDPOINT);

            return response.jsonPath().getString("access_token");
        }

    public static String getToken(AccessType accessType){
        return accessType == AccessType.WRITE ? writeToken : readToken;
    }
}
