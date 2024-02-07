package net.tust.mecs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BackendWxSerchpeopleApplicationTests {
    private String APPID="wx6817a00647ce7d43";
    private String SECRET="70ce449469e357cecdf4cbbe817beb6d";
    @Test
    void contextLoads() throws Exception {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + APPID + "&secret=" + SECRET;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        if (response.getStatusCodeValue() == 200) {
            String responseBody = response.getBody();
            System.out.println(responseBody);
            String accessToken = extractAccessToken(responseBody);
            System.out.println(accessToken);;
        } else {
            System.err.println("Error: " + response.getStatusCodeValue());
        }
    }
    private String extractAccessToken(String responseBody) {
        String accessTokenRegex = "\"access_token\":\"(.*?)\"";
        Pattern pattern = Pattern.compile(accessTokenRegex);
        Matcher matcher = pattern.matcher(responseBody);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
