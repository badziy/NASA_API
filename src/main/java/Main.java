import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class Main {
    public static final String NASA_URL = "https://api.nasa.gov/planetary/apod?api_key=IUU3Gi6yHEBiJuVImRnLuoETdtYapkHEkBB6BLNV";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(NASA_URL);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        CloseableHttpResponse response = httpClient.execute(request);
        Arrays.stream(response.getAllHeaders()).forEach(System.out::println);

        NasaRequest posts = mapper.readValue(response.getEntity().getContent(), new TypeReference<>() {
        });
        System.out.println(posts);
        String url = posts.getUrl();
        String[] mySplit = url.split("/");
        request = new HttpGet(url);
        String fileName=null;
        if (posts.getMedia_type().equals("video")){
            fileName = posts.getTitle()+"_video.html";
        } else if (posts.getMedia_type().equals("image")){
            fileName = mySplit[mySplit.length-1];
            request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        }
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fos = new FileOutputStream(fileName)) {
            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fos.write(data, 0, count);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }
}
