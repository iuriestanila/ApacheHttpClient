package utils;

import lombok.SneakyThrows;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class BasicAuth {
    @SneakyThrows
    public static CloseableHttpClient getAuthClient() {
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(FileReader.read("username"), FileReader.read("password")));

          return HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();

    }
}
