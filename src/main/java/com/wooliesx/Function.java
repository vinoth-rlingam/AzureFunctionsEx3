package com.wooliesx;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;


import java.io.IOException;

import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
   
    String content;
    @FunctionName("trolleyTotal")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS) final HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        final String itemDetails = request.getBody().get();
        System.out.println(itemDetails);

        CloseableHttpResponse response;

        if (itemDetails == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass itemDetails in the request body").build();
        } else {

            final String trolleyTotal = "http://dev-wooliesx-recruitment.azurewebsites.net/api/resource/trolleyCalculator?token=57612656-ad80-40e4-b4b6-597c72369ca7";
            final CloseableHttpClient newClient = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(trolleyTotal);
            post.setHeader("Accept", "application/json");
            post.setHeader("content-type", "application/json");
            String json = itemDetails;
            //final JSONObject contentJson = new JSONObject();
            //contentJson.put("body", itemDetails);
            try {
                //final StringEntity stringEntity = new StringEntity(itemDetails);
                HttpEntity entity = new ByteArrayEntity(json.getBytes("UTF-8"));
                //System.out.println (stringEntity.toString());
                post.setEntity(entity);
                response = newClient.execute(post);
                
                
                    content = (EntityUtils.toString(response.getEntity()));
                    System.out.println("content" + content);
                
            } catch (final IOException io) {
                io.printStackTrace();
            }
            return request.createResponseBuilder(HttpStatus.OK)
            .header("content-type", "application/json")
            .body(content).build();
        }
    }
}
