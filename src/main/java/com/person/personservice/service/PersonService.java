package com.person.personservice.service;

import com.person.personservice.model.PersonDTO;
import com.person.personservice.persistence.domain.Person;
import com.person.personservice.persistence.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {

    private static final int PAGE_SIZE = 5;

    private final PersonRepository personRepository;

    public Page<PersonDTO> getAllPerson(Pageable pageable) {
        Pageable paging = processPageable(pageable);
        Page<Person> person = personRepository.findAll(paging);
        return person.map(this::mapPersonToPersonDTO);
    }

    private Pageable processPageable(Pageable pageable) {
        return (pageable.getPageSize() > PAGE_SIZE)
                ? PageRequest.of(pageable.getPageNumber(), PAGE_SIZE, pageable.getSort())
                : pageable;
    }

    private PersonDTO mapPersonToPersonDTO(Person person) {
        Set<Long> parentsIds = person.getParents()
                .stream()
                .map(Person::getId)
                .collect(Collectors.toSet());
        return new PersonDTO(person.getName(), parentsIds);
    }
}
