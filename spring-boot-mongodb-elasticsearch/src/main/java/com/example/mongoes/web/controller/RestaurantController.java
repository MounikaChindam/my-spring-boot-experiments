package com.example.mongoes.web.controller;

import com.example.mongoes.document.Grades;
import com.example.mongoes.document.Restaurant;
import com.example.mongoes.response.GenericMessage;
import com.example.mongoes.web.service.RestaurantService;
import io.micrometer.core.annotation.Timed;
import java.net.URI;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Timed
@RequestMapping("/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping
    public Mono<ResponseEntity<SearchPage<Restaurant>>> findAllRestaurants(
            @Valid @RequestParam(value = "limit", defaultValue = "10") @Size(max = 999)
                    Integer limit,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset) {
        return restaurantService.findAllRestaurants(offset, limit).map(ResponseEntity::ok);
    }

    @GetMapping("/name/{restaurantName}")
    public Mono<ResponseEntity<Restaurant>> findRestaurantByName(
            @PathVariable("restaurantName") String restaurantName) {
        return restaurantService
                .findByRestaurantName(restaurantName)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{restaurantId}")
    public Mono<ResponseEntity<Restaurant>> findRestaurantById(
            @PathVariable("restaurantId") Long restaurantId) {
        return restaurantService
                .findByRestaurantId(restaurantId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/{restaurantId}/grade")
    public Mono<Restaurant> addGradeToRestaurant(
            @RequestBody Grades request, @PathVariable("restaurantId") Long id) {
        return this.restaurantService.addGrade(request, id);
    }

    @GetMapping("/total")
    public Mono<ResponseEntity<Long>> totalCount() {
        return restaurantService
                .totalCount()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{restaurantId}/grades/")
    public Mono<ResponseEntity<Restaurant>> addNotesToRestaurant(
            @PathVariable("restaurantId") Long restaurantId, @RequestBody Grades grades) {
        return restaurantService.addGrade(grades, restaurantId).map(ResponseEntity::ok);
    }

    @PostMapping("/restaurant")
    public ResponseEntity<Object> createRestaurant(@RequestBody Restaurant restaurant) {
        return ResponseEntity.created(
                        URI.create(
                                String.format(
                                        "/restaurant/%s",
                                        this.restaurantService
                                                .createRestaurant(restaurant)
                                                .map(Restaurant::getName))))
                .body(
                        new GenericMessage(
                                String.format(
                                        "restaurant with name %s created", restaurant.getName())));
    }
}
