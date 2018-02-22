package com.game.lambda;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.game.CustomAuthPrincipalFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

public class LambdaHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

    private static SpringLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
    private static Logger logger = LoggerFactory.getLogger(LambdaHandler.class);

    static{
        try {
            handler = SpringLambdaContainerHandler.getAwsProxyHandler(LambdaHandlerConfiguration.class);

            handler.onStartup(servletContext -> {

                servletContext.addFilter(
                    AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME, new CustomAuthPrincipalFilter()).
                    addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
            });

            handler.activateSpringProfiles("lambda");

        } catch (ContainerInitializationException e) {
            logger.error("Error while initiating lambda", e);
        }
    }

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest awsProxyRequest, Context context) {
        return handler.proxy(awsProxyRequest, context);
    }

}
