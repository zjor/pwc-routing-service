package com.github.zjor.routing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class RouteFinderServiceTest {

    private RouteFinderService service;

    @Before
    public void setUp() {
        service = new RouteFinderService();
        service.afterPropertiesSet();
    }

    @Test
    public void shouldFindShortRoute() {
        var route = service.findRoute("CZE", "DEU");
        Assert.assertTrue(route.isPresent());
        Assert.assertEquals(List.of("CZE", "DEU"), route.get());
    }

    @Test
    public void shouldFindLongRoute() {
        var route = service.findRoute("CZE", "PRT");
        Assert.assertTrue(route.isPresent());
        Assert.assertEquals(List.of("CZE", "DEU", "FRA", "ESP", "PRT"), route.get());
    }

    @Test
    public void shouldNotFindRoute() {
        var route = service.findRoute("CZE", "USA");
        Assert.assertTrue(route.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNotExistent() {
        service.findRoute("Lala-land", "Midgard");
    }

}