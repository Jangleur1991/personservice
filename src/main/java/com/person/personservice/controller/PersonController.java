package com.person.personservice.controller;

import com.person.personservice.model.PersonDTO;
import com.person.personservice.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping("/person")
    public Page<PersonDTO> getAllPerson() {
       return personService.getAllPerson();
    }

}
