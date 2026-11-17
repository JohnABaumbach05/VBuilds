package com.vbuilds.dto;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

class UpdateBuildRequestTest {
    private final JsonMapper mapper = new JsonMapper();

    @Test
    void acceptsValidEditPayload() {
        String json = """
                {"buildYear":2022,"cost":30000,"description":"Updated build",
                 "happiness":5,"horsepower":300,"loudness":5,"reliability":5,
                 "title":"My WRX","trim":"Base","tuner":"dmann"}
                """;
        UpdateBuildRequest request = mapper.readValue(json, UpdateBuildRequest.class);
        assertEquals(2022, request.getBuildYear());
        assertEquals(30000, request.getCost());
        assertEquals(300, request.getHorsepower());
        assertEquals("My WRX", request.getTitle());
        assertEquals("dmann", request.getTuner());
    }

    @Test
    void missingNumericFieldsStayNullInsteadOfFailingDeserialization() {
        UpdateBuildRequest request = mapper.readValue("{\"title\":\"WRX\"}", UpdateBuildRequest.class);
        assertNull(request.getBuildYear());
        assertNull(request.getCost());
        assertNull(request.getHorsepower());
        assertNull(request.getHappiness());
    }

    @Test
    void explicitNullNumericFieldsCanBeValidatedByController() {
        UpdateBuildRequest request = mapper.readValue(
                "{\"buildYear\":null,\"cost\":null,\"horsepower\":null}", UpdateBuildRequest.class);
        assertNull(request.getBuildYear());
        assertNull(request.getCost());
        assertNull(request.getHorsepower());
    }
}
