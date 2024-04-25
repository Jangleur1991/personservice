package com.person.personservice.model;

import java.util.Set;

public record PersonDTO(String name, Set<Long> parentIds) { }
