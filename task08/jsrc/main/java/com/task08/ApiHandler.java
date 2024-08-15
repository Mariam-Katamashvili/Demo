package com.task08;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.Architecture;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.Map;

@LambdaHandler(
        lambdaName = "api_handler",
        roleName = "api_handler-role",
        layers = {"weather-layer"}
)
@LambdaLayer(
        layerName = "weather-layer",
        libraries = {"lib/task08-1.0.0.jar"},
        runtime = DeploymentRuntime.JAVA11,
        architectures = {Architecture.ARM64},
        artifactExtension = ArtifactExtension.ZIP
)
@LambdaUrlConfig(
        authType = AuthType.NONE,
        invokeMode = InvokeMode.BUFFERED
)
public class ApiHandler implements RequestHandler<Map<String, Object>, String> {
    public String handleRequest(Map<String, Object> request, Context context) {
        LambdaLogger logger = context.getLogger();

        logger.log("Received request: " + request.toString());
        OpenMeteo openMeteo = new OpenMeteo();
        try {
            String forecast = openMeteo.getWeather();
            logger.log("Weather forecast: " + forecast);
            return forecast;
        } catch (Exception e) {
            logger.log("Error occurred: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
