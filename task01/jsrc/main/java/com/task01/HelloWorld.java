package com.task01;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(lambdaName = "hello_world",
	roleName = "hello_world-role",
	isPublishVersion = false,
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
public class HelloWorld implements RequestHandler<Object, Map<String, Object>> {

	public Map<String, Object> handleRequest(Object request, Context context) {
		System.out.println("Hello from lambda");

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("statusCode", 200);

		Map<String, String> bodyMap = new HashMap<>();
		bodyMap.put("message", "Hello from Lambda");

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String body = objectMapper.writeValueAsString(bodyMap);
			resultMap.put("body", body);
		} catch (Exception e) {
			resultMap.put("statusCode", 500);
			resultMap.put("body", "{\"message\":\"Internal Server Error\"}");
		}

		return resultMap;
	}
}
