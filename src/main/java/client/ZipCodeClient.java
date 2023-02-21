package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import pojo.ResponseEntity;
import java.util.Arrays;
import java.util.List;

public class ZipCodeClient {
    private static final String GET_ZIPCODES_ENDPOINT = "/zip-codes";
    private static final String POST_ZIPCODES_ENDPOINT = "/zip-codes/expand";
    private final ObjectMapper objectMapper;

    public ZipCodeClient() {
        this.objectMapper = new ObjectMapper();
    }

    @SneakyThrows
    public ResponseEntity<List<String>> getZipCodes() {
        final HttpResponse httpResponse = Client.doGet(GET_ZIPCODES_ENDPOINT);

        ResponseEntity<List<String>> response = new ResponseEntity<>();
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());

        final String[] zipcodes = objectMapper.readValue(httpResponse.getEntity().getContent(), String[].class);
        response.setBody(Arrays.asList(zipcodes));
        return response;
    }

    @SneakyThrows
    public ResponseEntity<List<String>> postZipCodes(List<String> zipcodesPassed) {
        final String requestBody = objectMapper.writeValueAsString(zipcodesPassed);
        final HttpResponse httpResponse = Client.doPost(POST_ZIPCODES_ENDPOINT, requestBody);

        ResponseEntity<List<String>> response = new ResponseEntity<>();
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());

        final String[] zipcodes = objectMapper.readValue(httpResponse.getEntity().getContent(), String[].class);
        response.setBody(Arrays.asList(zipcodes));
        return response;
    }
}