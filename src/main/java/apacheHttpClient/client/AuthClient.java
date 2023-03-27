package apacheHttpClient.client;

import apacheHttpClient.enums.AccessType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import apacheHttpClient.pojo.Token;

import java.util.Locale;

public class AuthClient {
    private static String writeToken;
    private static String readToken;
    private static final String LOGIN = "0oa157tvtugfFXEhU4x7";
    private static final String PASSWORD = "X7eBCXqlFC7x-mjxG5H91IRv_Bqe1oq7ZwXNA8aq";
    private static final String TOKEN_ENDPOINT = "/oauth/token";

    static {
        writeToken = retrieveToken(AccessType.WRITE);
        readToken = retrieveToken(AccessType.READ);
    }

    private static String retrieveToken(AccessType scope) {
        final HttpResponse response = Request.post(Client.BASE_URL + TOKEN_ENDPOINT)
                .addBasicAuth(LOGIN, PASSWORD)
                .addParameter("grant_type", "client_credentials")
                .addParameter("scope", scope.name().toLowerCase(Locale.ROOT))
                .execute();
        try {
            return new ObjectMapper().readValue(response.getEntity().getContent(), Token.class).getAccessToken();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getToken(AccessType accessType) {
        return accessType == AccessType.WRITE ? writeToken : readToken;
    }
}
