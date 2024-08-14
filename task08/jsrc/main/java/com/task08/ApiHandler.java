package com.task08;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
		lambdaName = "api_handler",
		roleName = "api_handler-role",
		isPublishVersion = false, // Ensure no Lambda versions or aliases are used
		logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
public class ApiHandler implements RequestHandler<Object, Map<String, Object>> {

	private final OpenMeteoClient openMeteoClient = new OpenMeteoClient();

	@Override
	public Map<String, Object> handleRequest(Object input, Context context) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			String weatherData = openMeteoClient.getWeatherForecast();
			resultMap.put("statusCode", 200);
			resultMap.put("body", weatherData);
		} catch (Exception e) {
			resultMap.put("statusCode", 500);
			resultMap.put("body", "Failed to fetch weather data: " + e.getMessage());
		}
		return resultMap;
	}
}
