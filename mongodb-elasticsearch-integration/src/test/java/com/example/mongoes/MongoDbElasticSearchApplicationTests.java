package com.example.mongoes;

import com.example.mongoes.mongodb.repository.RestaurantRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class MongoDbElasticSearchApplicationTests {

  @Container
  private static final MongoDBContainer MONGO_DB_CONTAINER =
      new MongoDBContainer("mongo:latest").withExposedPorts(27017, 27018, 27019);

  @Autowired private RestaurantRepository restaurantRepository;

  @BeforeAll
  static void setUpAll() {
    MONGO_DB_CONTAINER.start();
  }

  @AfterAll
  static void tearDownAll() {
    if (!MONGO_DB_CONTAINER.isShouldBeReused()) {
      MONGO_DB_CONTAINER.stop();
    }
  }

  @DynamicPropertySource
  static void setMongoDbContainerURI(DynamicPropertyRegistry propertyRegistry) {
    propertyRegistry.add("spring.data.mongodb.uri", MONGO_DB_CONTAINER::getReplicaSetUrl);
  }

  @AfterEach
  void tearDown() {
    restaurantRepository.deleteAll();
  }

  @Test
  void contextLoads() throws InterruptedException {
    TimeUnit.SECONDS.sleep(5);
    assertThat(MONGO_DB_CONTAINER.isRunning()).isTrue();
    assertThat(restaurantRepository.count()).isEqualTo(1);
  }
}
