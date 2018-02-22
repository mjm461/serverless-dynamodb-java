package com.game;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.function.Function;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ControllerConfiguration.class )
public class ControllerTest {

	private static DynamoDBProxyServer dynamoDBProxyServer;
	private static ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private WebApplicationContext context;
	public void setContext(WebApplicationContext context) {
		this.context = context;
	}

	@Value("${com.game.dynamodbport}") private String dynamodbPort;

	private MockMvc mvc;
	protected MockMvc getMvc() {
		return mvc;
	}

	@Before
	public void setup(){
        try {
            final String[] localArgs = { "-inMemory", "-port", dynamodbPort, "-sharedDb"  };
            dynamoDBProxyServer = ServerRunner.createServerFromCommandLineArgs(localArgs);
            dynamoDBProxyServer.start();
        } catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
			return;
		}

		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.addFilter(new CustomAuthPrincipalFilter())
				.build();

    }

	@After
	public void shutdownDynamoDB() {

		if(dynamoDBProxyServer != null) {
			try {
				dynamoDBProxyServer.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	protected MockHttpServletRequestBuilder method(String endpoint, String jwtToken, Function<String, MockHttpServletRequestBuilder> f) {
		return f.apply(endpoint).header("Authorization", "Bearer " + jwtToken);
	}

    public ObjectMapper getObjectMapper(){
		return objectMapper;
	}
}

