package net.tust.mecs.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.tust.mecs.entity.addDraftVo.AddDraftRequest;
import net.tust.mecs.entity.submitVo.SubmitVo;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class SerchPeopleController {

    private String APPID="wx6817a00647ce7d43";
    private String SECRET="70ce449469e357cecdf4cbbe817beb6d";
    private String accessToken;
    private String mediaId;

    public String getAccessToken() throws Exception {
        if (accessToken == null) {
            accessToken = fetchAccessToken();
        }
        return accessToken;
    }
    private String fetchAccessToken() throws Exception {
        try {
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + APPID + "&secret=" + SECRET;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if (response.getStatusCodeValue() == 200) {
                String responseBody = response.getBody();
                String accessToken = extractAccessToken(responseBody);
                return accessToken;
            } else {
                System.err.println("Error: " + response.getStatusCodeValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
    @PostMapping("/addDraftAndSubmit")
    public String addDraftAndSubmit(@RequestBody AddDraftRequest addDraftRequest) throws Exception {
        String url = "https://api.weixin.qq.com/cgi-bin/draft/add?access_token=" + getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AddDraftRequest> entity = new HttpEntity<>(addDraftRequest, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        String responseBody = response.getBody();
        System.out.println(responseBody);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String mediaId = jsonNode.get("media_id").asText();
        System.out.println(mediaId);

        if (mediaId == null) {
            return "MediaId not available";
        }

        SubmitVo submitVo = new SubmitVo();
        submitVo.setMediaId(mediaId);
        url = "https://api.weixin.qq.com/cgi-bin/freepublish/submit?access_token=" + getAccessToken();
        String submitResponse = restTemplate.postForObject(url, submitVo, String.class);
        return submitResponse;
    }
    @PostMapping("/upload")
    public String upload(MultipartFile image) throws Exception {
        String url="https://api.weixin.qq.com/cgi-bin/media/upload?access_token="+getAccessToken()+"&type=image";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // Handle the response here
            String response = responseEntity.getBody();
            mediaId = parseMediaIdFromResponse(response);
//            if (!StringUtils.isEmpty(mediaId))
//            System.out.println(mediaId);
//            return response;
            url="https://api.weixin.qq.com/cgi-bin/media/get?access_token="+getAccessToken()+"&media_id="+mediaId;
            restTemplate = new RestTemplate();
            ResponseEntity<byte[]> responseEntity1 = restTemplate.getForEntity(url, byte[].class);
            byte[] responseBody = responseEntity1.getBody();
            String fileName = "media_file.jpg";
            try (OutputStream outputStream = new FileOutputStream(fileName)) {
                outputStream.write(responseBody);
            }
            String base64Encoded = Base64.getEncoder().encodeToString(responseBody);
            return base64Encoded;
        } else {
            throw new Exception("Failed to upload file");
        }
    }
    private String parseMediaIdFromResponse(String responseBody) {
        // Parse the response body to extract the media ID
        // You can use a JSON parsing library here to make it easier
        // For simplicity, let's assume the response is in the following format:
        // {"media_id": "YOUR_MEDIA_ID"}
        // Here, we'll use a simple string manipulation to extract the media ID
        int startIndex = responseBody.indexOf("\"media_id\":") + 12;
        int endIndex = responseBody.indexOf("\"", startIndex);
        return responseBody.substring(startIndex, endIndex);
    }
//    @PostMapping("/uploadImage")
//    public String uploadImage(String imageUrl) throws Exception {
//        String accessToken = getAccessToken();
//        String type = "image";
//        try {
//            return uploadImage(accessToken, type, imageUrl);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//    public String uploadImage(String accessToken, String type, String imageUrl) throws IOException {
//        HttpClient httpClient = HttpClientBuilder.create().build();
//        HttpPost httpPost = new HttpPost("https://api.weixin.qq.com/cgi-bin/media/upload?access_token=" + accessToken + "&type=" + type);
//        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
//        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//        File imageFile = new File(imageUrl);
//        FileBody fileBody = new FileBody(imageFile, ContentType.DEFAULT_BINARY);
//        entityBuilder.addPart("media", fileBody);
//        HttpEntity entity = (HttpEntity) entityBuilder.build();
//        httpPost.setEntity((org.apache.http.HttpEntity) entity);
//        HttpResponse response = httpClient.execute(httpPost);
//        return EntityUtils.toString(response.getEntity());
//    }

}

