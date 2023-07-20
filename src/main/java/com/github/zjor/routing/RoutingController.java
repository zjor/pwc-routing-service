package com.github.zjor.routing;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("routing")
public class RoutingController {

    private final RouteFinderService routeFinderService;

    public RoutingController(RouteFinderService routeFinderService) {
        this.routeFinderService = routeFinderService;
    }

    @GetMapping("{from}/{to}")
    public List<String> getRoute(@PathVariable("from") String from, @PathVariable("to") String to) {
        try {
            var route = routeFinderService.findRoute(from.toUpperCase(), to.toUpperCase());
            if (route.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Route not found");
            }
            return route.get();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
