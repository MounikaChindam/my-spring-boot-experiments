# spring-boot-choas-monkey

Adds Gatline performance tests to demonstrate the adding assualts will cause delay in response and choas monkey is working as expected .

Using Springframework 6.0 `ProblemDetails` and spring boot micrometer Observability in controller

### Run tests
`$ ./mvnw clean verify`

### Run Gatling 
`./mvnw gatling:test`

### Run locally
```shell
$ docker-compose -f docker/docker-compose.yml up -d
$ ./mvnw spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.arguments="--chaos.monkey.enabled=true"
$ ./mvnw gatling:test
```

By Default choas Monkey is enabled, lets disabled and run the tests again. It should improve the response times

```shell
$ ./mvnw spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.arguments="--chaos.monkey.enabled=false"
$ ./mvnw gatling:test
```
Results of gatling are stored in target folder for each run

### Useful Links
* Swagger UI: http://localhost:8080/swagger-ui.html
* Actuator Endpoint: http://localhost:8080/actuator
* PGAdmin : http://localhost:5050
* Obervability metrics : http://localhost:8080/actuator/metrics

### Technologies used
* Spring Boot
* Gatling (Performance Tests)
* Choas Monkey (Choas Engineering Principles)

#### Sample Test Results 

we can observe that mean requests/sec is more when choas monkey is enabled

##### With Choas Monkey enabled

================================================================================
---- Global Information --------------------------------------------------------
>  request count                                       6000 (OK=6000   KO=0     )
>  min response time                                      1 (OK=1      KO=-     )
>  max response time                                   2635 (OK=2635   KO=-     )
>  mean response time                                   145 (OK=145    KO=-     )
>  std deviation                                        337 (OK=337    KO=-     )
>  response time 50th percentile                         15 (OK=15     KO=-     )
>  response time 75th percentile                         98 (OK=98     KO=-     )
>  response time 95th percentile                        773 (OK=773    KO=-     )
>  response time 99th percentile                       1952 (OK=1952   KO=-     )
>  mean requests/sec                                193.548 (OK=193.548 KO=-     )
---- Response Time Distribution ------------------------------------------------
>  t < 800 ms                                          5733 ( 96%)
>  800 ms <= t < 1200 ms                                131 (  2%)
>  t ≥ 1200 ms                                          136 (  2%)
>  failed                                                 0 (  0%)
================================================================================

##### With Choas Monkey disabled

================================================================================
---- Global Information --------------------------------------------------------
>  request count                                       6000 (OK=6000   KO=0     )
>  min response time                                      4 (OK=4      KO=-     )
>  max response time                                  13608 (OK=13608  KO=-     )
>  mean response time                                  6289 (OK=6289   KO=-     )
>  std deviation                                       2579 (OK=2579   KO=-     )
>  response time 50th percentile                       6396 (OK=6396   KO=-     )
>  response time 75th percentile                       8292 (OK=8292   KO=-     )
>  response time 95th percentile                      10342 (OK=10342  KO=-     )
>  response time 99th percentile                      11604 (OK=11604  KO=-     )
>  mean requests/sec                                176.471 (OK=176.471 KO=-     )
---- Response Time Distribution ------------------------------------------------
>  t < 800 ms                                            65 (  1%)
>  800 ms <= t < 1200 ms                                 54 (  1%)
>  t ≥ 1200 ms                                         5881 ( 98%)
>  failed                                                 0 (  0%)
================================================================================