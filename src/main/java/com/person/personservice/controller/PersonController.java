package com.person.personservice.controller;

import com.person.personservice.model.PersonDTO;
import com.person.personservice.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

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
    public ResponseEntity<?> createPerson(@RequestBody PersonDTO personDTO) {
        var personId = personService.savePerson(personDTO);
        var uri = UriComponentsBuilder.fromUriString("/api")
                .pathSegment("person", String.valueOf(personId))
                .build()
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/person/{id}")
    public ResponseEntity<PersonDTO> updatePerson(@PathVariable long id, @RequestBody PersonDTO personDTO) throws Exception {
        return personService.updatePerson(id, personDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new Exception("Something went terrible wrong :("));
    }

    @PutMapping("/person/{id}/parents")
    public ResponseEntity<PersonDTO> updateParents(@PathVariable long id, @RequestBody List<Long> parentIds) throws Exception {
        return personService.updateParents(id, parentIds)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new Exception("Something went terrible wrong :("));
    }

}
