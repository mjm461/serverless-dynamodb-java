package com.game.lambda;

import com.game.configuration.GameConfiguration;
import org.springframework.context.annotation.*;


@Profile("lambda")
@PropertySource("classpath:/application-lambda.properties")
public class LambdaHandlerConfiguration extends GameConfiguration {
}
