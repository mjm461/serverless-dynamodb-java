package com.game;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootApplication
@RunWith(SpringRunner.class)
public class GameControllerTest extends ControllerTest {

    @Value("${local.server.port}")
    private int port;

    @Test public void test(){
        // TODO: add tests
    }
}
