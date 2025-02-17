package com.example.mongoes.common;

import static com.example.mongoes.utils.AppConstants.PROFILE_TEST;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@ActiveProfiles({PROFILE_TEST})
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
public abstract class AbstractIntegrationTest extends ElasticsearchContainerSetUp {

    @Autowired protected WebTestClient webTestClient;
    @Autowired protected ObjectMapper objectMapper;
}
