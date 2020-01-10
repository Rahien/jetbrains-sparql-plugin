import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;

public class SPARQLApi {
    public static String api;
    private static boolean rememberSettings;

    public static boolean remembersSettings(){
        return rememberSettings;
    }

    private static HttpClient getHttpClient() {
        if(api.indexOf("https://localhost") == 0){
            return HttpClients
                    .custom()
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();
        }else{
            return HttpClients.createDefault();
        }
    }
    public static void login(String chosenApi, boolean remember) {
        try {
            api = chosenApi;
            rememberSettings = remember;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static HttpResponse performVirtuosoSparql(HttpUriRequest request) throws Exception{
        HttpClient client = getHttpClient();
        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode < 200 || statusCode > 299) {
            throw new IllegalStateException("Failed to run SPARQL query, status: " + statusCode);
        }
        return response;
    }

    private static String getText(HttpResponse response) throws Exception {
        HttpEntity entity = response.getEntity();

        InputStream instream = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(instream), 2048);

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static File ensureFile(String path, String content) throws Exception {
        File file = new File(path);
        file.getParentFile().mkdirs();
        boolean created = file.createNewFile();

        if(content == null){
            return null;
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();
        return file;
    }

    public static String postSparqlToServer(String sparql) throws Exception {
        String urlEncoded = URLEncoder.encode(sparql, StandardCharsets.UTF_8.toString());
        String format = "text%2Fhtml";
        if(sparql.toLowerCase().contains("construct")){
            format = "text%2Fturtle";
        }

        HttpGet get = new HttpGet(api + "sparql/?default-graph-uri=&format="+format+"&timeout=0&debug=on&run=+Run+Query+&query=" + urlEncoded);
        HttpResponse response = performVirtuosoSparql(get);
        return getText(response);
    }
}
