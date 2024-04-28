package com.person.personservice.service;

import com.person.personservice.model.PersonDTO;
import com.person.personservice.persistence.domain.Person;
import com.person.personservice.persistence.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.person.personservice.utils.Utils.mergeSets;

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

    public Optional<PersonDTO> getById(long id) {
        return personRepository.findById(id).map(this::mapPersonToPersonDTO);
    }

    public PersonDTO savePerson(PersonDTO personDTO) {
        Set<Person> parents = new HashSet<>(personRepository.findAllById(personDTO.parentIds()));
        var person = Person.builder()
                .id(personDTO.id()) //TODO: What to do about that???
                .name(personDTO.name())
                .parents(parents)
                .build();
        return mapPersonToPersonDTO(personRepository.save(person));
    }

    public Optional<PersonDTO> updatePerson(long id, PersonDTO personDTO) {
        return personRepository.findById(id)
                .map(this::mapPersonToPersonDTO)
                .map(person ->
                        new PersonDTO(
                                id,
                                (null != personDTO.name()) ? personDTO.name() : person.name(),
                                mergeSets(person.parentIds(), personDTO.parentIds())
                        ))
                .map(this::savePerson);
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
        return new PersonDTO(person.getId(), person.getName(), parentsIds);
    }

}
