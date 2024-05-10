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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public long savePerson(PersonDTO personDTO) {
        return savePerson(null, personDTO);
    }

    private long savePerson(Long id, PersonDTO personDTO) {
        var personBuilder = Person.builder()
                .name(personDTO.name())
                .parents(retrieveParents(personDTO));

        if (null != id) {
            personBuilder.id(id);
        }

        return personRepository.save(personBuilder.build()).getId();
    }

    public Optional<PersonDTO> updatePerson(long id, PersonDTO updateRequest) {
        return personRepository.findById(id)
                .map(this::mapPersonToPersonDTO)
                .map(personDTO -> updatePersonDTO(personDTO, updateRequest))
                .map(updatedPersonDTO -> {
                    savePerson(id, updatedPersonDTO);
                    return updatedPersonDTO;
                });
    }

    public Optional<PersonDTO> updateParents(long id, List<Long> parentIds) {
        return personRepository.findById(id)
                .map(person -> new PersonDTO(person.getName(), new HashSet<>(parentIds)))
                .map(updatedPerson -> {
                    savePerson(id, updatedPerson);
                    return updatedPerson;
                });
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

    private PersonDTO updatePersonDTO(PersonDTO personDTO, PersonDTO updateRequest) {
        String updatedName = (null != updateRequest.name()) ? updateRequest.name() : personDTO.name();
        Set<Long> updatedParentIds = (null != updateRequest.parentIds())
                ? mergeSets(personDTO.parentIds(), updateRequest.parentIds())
                : personDTO.parentIds();
        return new PersonDTO(updatedName, updatedParentIds);
    }

    private Set<Person> retrieveParents(PersonDTO personDTO) {
        Set<Long> parentIds = personDTO.parentIds();
        Set<Person> parents = new HashSet<>();
        if (null != parentIds) {
            parents.addAll(personRepository.findAllById(personDTO.parentIds()));
        }
        return parents;
    }

}
