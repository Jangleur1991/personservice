package com.person.personservice.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Utils {

    @Getter
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private Utils(){}

    public static <T> Set<T> mergeSets(Set<T> personSet1, Set<T> personSet2) {
        return Stream.concat(personSet1.stream(), personSet2.stream()).collect(Collectors.toSet());
    }
}
