package com.github.zjor.routing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RouteFinderService implements InitializingBean {

    private final static String COUNTRIES_FILENAME = "countries.json";

    private final static ObjectMapper MAPPER = new ObjectMapper();

    private Map<String, List<String>> countriesGraph;

    private Map<String, List<String>> loadCountries() {
        var in = RouteFinderService.class.getClassLoader().getResourceAsStream(COUNTRIES_FILENAME);
        if (in == null) {
            throw new RuntimeException(COUNTRIES_FILENAME + " not found on classpath");
        }
        try {
            var countries = MAPPER.readValue(in, new TypeReference<List<Country>>() {
            });
            var mapOfCountries = new HashMap<String, List<String>>();
            countries.forEach(c -> mapOfCountries.put(c.cca3, c.borders));
            return mapOfCountries;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + COUNTRIES_FILENAME, e);
        }
    }

    public Optional<List<String>> findRoute(String from, String to) {
        if (countriesGraph == null || countriesGraph.isEmpty()) {
            throw new IllegalStateException("Not initialized");
        }

        if (!(countriesGraph.containsKey(from) && countriesGraph.containsKey(to))) {
            throw new IllegalArgumentException("Whether " + from + " or " + to + " does not exist");
        }
        var visited = new HashSet<String>();
        var frontier = new LinkedList<Pair<String, List<String>>>();

        visited.add(from);
        countriesGraph.get(from).forEach(c -> frontier.add(Pair.of(c, List.of(from))));
        while (!frontier.isEmpty()) {
            var first = frontier.pollFirst();
            var country = first.getLeft();
            if (country.equals(to)) {
                var path = new LinkedList<>(first.getRight());
                path.add(to);
                return Optional.of(path);
            } else {
                visited.add(country);
                var currentPath = new LinkedList<>(first.getRight());
                currentPath.add(country);

                for (var next: countriesGraph.get(country)) {
                    if (!visited.contains(next)) {
                        frontier.add(Pair.of(next, currentPath));
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void afterPropertiesSet() {
        countriesGraph = loadCountries();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Country {
        private String cca3;
        private List<String> borders;
    }

    public static void main(String[] args) {
        var svc = new RouteFinderService();
        svc.afterPropertiesSet();
        var route = svc.findRoute("CZE", "USA");
        System.out.println(route);
    }


}
