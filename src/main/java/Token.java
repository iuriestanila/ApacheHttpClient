import lombok.SneakyThrows;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import utils.BasicAuth;
import utils.ReadFile;

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
          CloseableHttpResponse response = null;
          CloseableHttpClient client = null;
          String responseString;

          try {
                  client = BasicAuth.authentication();
                  HttpPost httpPost = new HttpPost(ReadFile.read("urlToken"));

                  List<BasicNameValuePair> parameters = new ArrayList<>();
                  parameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
                  parameters.add(new BasicNameValuePair("scope", scope));
                  httpPost.setEntity(new UrlEncodedFormEntity(parameters));

                  response = client.execute(httpPost);
                  responseString = EntityUtils.toString(response.getEntity());
          } finally {
              if (response != null) {
                  response.close();
              }
              if (client != null) {
                  client.close();
              }
          }
          return responseString;
      }
}
