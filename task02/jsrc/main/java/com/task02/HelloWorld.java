package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(lambdaName = "hello_world",
        roleName = "hello_world-role",
        isPublishVersion = false,
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(
        authType = AuthType.NONE
)
public class HelloWorld implements RequestHandler<Map<String,Object>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String handleRequest(Map<String, Object> request, Context context) {
        LambdaLogger lambdaLogger = context.getLogger();
        lambdaLogger.log(request.toString());

        String path = (String) request.get("rawPath");
        String method = (String) request.get("httpMethod");

        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            if ("/hello".equals(path) && "GET".equals(method)) {
                resultMap.put("statusCode", 200);
                Map<String, String> body = new HashMap<>();
                body.put("statusCode", "200");
                body.put("message", "Hello from Lambda");
                resultMap.put("body", objectMapper.writeValueAsString(body));
            } else {
                resultMap.put("statusCode", 400);
                Map<String, String> body = new HashMap<>();
                body.put("statusCode", "400");
                body.put("message", "Bad request syntax or unsupported method. Request path: " + path + ". HTTP method: " + method);
                resultMap.put("body", objectMapper.writeValueAsString(body));
            }
        } catch (Exception e) {
            lambdaLogger.log("Error: " + e.getMessage());
            resultMap.put("statusCode", 500);
            Map<String, String> body = new HashMap<>();
            body.put("statusCode", "500");
            body.put("message", "Internal server error");
            try {
                resultMap.put("body", objectMapper.writeValueAsString(body));
            } catch (Exception ex) {
                lambdaLogger.log("Error: " + ex.getMessage());
            }
        }

        try {
            return objectMapper.writeValueAsString(resultMap);
        } catch (Exception e) {
            lambdaLogger.log("Error: " + e.getMessage());
            return "{\"statusCode\": 500, \"body\": \"Internal server error\"}";
        }
    }
}