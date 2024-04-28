package com.person.personservice.controller;

import com.person.personservice.model.PersonDTO;
import com.person.personservice.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping("/person")
    public Page<PersonDTO> getAllPerson(Pageable pagination) {
       return personService.getAllPerson(pagination);
    }

    @GetMapping("/person/{id}")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable final long id) {
        return personService.getById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/person")
    public ResponseEntity<PersonDTO> createPerson(@RequestBody PersonDTO personDTO) {
        var savedPersonDto = personService.savePerson(personDTO);
        var uri = UriComponentsBuilder.fromUriString("/api")
                .pathSegment("person", String.valueOf(savedPersonDto.id()))
                .build()
                .toUri();
        return ResponseEntity.created(uri).body(savedPersonDto);
    }

}
