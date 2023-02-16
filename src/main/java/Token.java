import lombok.SneakyThrows;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import utils.BasicAuth;
import utils.FileReader;

import java.util.*;

public class Token {
      private static Token instance;
      private Token() {
      }
      public static Token getInstance() {
            if(instance == null) {
                instance = new Token();
            }
            return instance;
      }

      @SneakyThrows
      public String getBearerToken(String scope) {
            String responseString;
            try (CloseableHttpClient client = BasicAuth.getAuthClient();
                 CloseableHttpResponse response = client.execute(buildHttpPost(scope))) {
                  responseString = EntityUtils.toString(response.getEntity());
            }
            return responseString;
      }

      @SneakyThrows
      private HttpPost buildHttpPost(String scope) {
            HttpPost httpPost = new HttpPost(FileReader.read("urlToken"));
            List<BasicNameValuePair> parameters = new ArrayList<>();
            parameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
            parameters.add(new BasicNameValuePair("scope", scope));
            httpPost.setEntity(new UrlEncodedFormEntity(parameters));
            return httpPost;
      }
}