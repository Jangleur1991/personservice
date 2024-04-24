package com.person.personservice.model;

import java.util.Set;

public record PersonDTO(long id, String name, Set<Long> parentIds ) {

}
