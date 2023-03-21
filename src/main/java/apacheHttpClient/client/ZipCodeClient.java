package apacheHttpClient.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import apacheHttpClient.pojo.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZipCodeClient {
    public static final String GET_ZIPCODES_ENDPOINT = "/zip-codes";
    public static final String POST_ZIPCODES_ENDPOINT = "/zip-codes/expand";
    private final ObjectMapper objectMapper;

    public ZipCodeClient() {
        this.objectMapper = new ObjectMapper();
    }


    @Step
    @SneakyThrows
    public ResponseEntity<List<String>> getZipCodes() {
        final HttpResponse httpResponse = Client.doGet(GET_ZIPCODES_ENDPOINT);

        ResponseEntity<List<String>> response = new ResponseEntity<>();
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());

        final String[] zipcodes = objectMapper.readValue(httpResponse.getEntity().getContent(), String[].class);
        response.setBody(Arrays.asList(zipcodes));
        return response;
    }

    @Step
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

    public String createAvailableZipcode() {
        String randomZipCode = RandomStringUtils.randomNumeric(5);
        List<String> zipCode = new ArrayList<>();
        zipCode.add(randomZipCode);

        final ResponseEntity<List<String>> response = postZipCodes(zipCode);
        if (response.getStatusCode() == 201) {
            return randomZipCode.toString();
        } else {
            throw new RuntimeException("Zip code was not added");
        }
    }
}