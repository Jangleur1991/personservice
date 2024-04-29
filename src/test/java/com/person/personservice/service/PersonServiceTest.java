package com.person.personservice.service;

import com.person.personservice.model.PersonDTO;
import com.person.personservice.persistence.domain.Person;
import com.person.personservice.persistence.repository.PersonRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith( MockitoExtension.class )
class PersonServiceTest {

    private static final int PAGE_SIZE = 5;
    private static final long ID = 1L;
    private static final String PERSON_NAME = "testperson";

    private Person person;

    private PersonDTO personDTO;

    @InjectMocks
    private PersonService personService;

    @Mock
    private PersonRepository personRepository;

    @BeforeEach
    void setup() {
        person = createPerson();
        personDTO = mapPersonToPersonDTO(person);
    }


    @Test
    void testThatGetAllPersonReturnsAllPerson() {
        //given
        PageRequest pageable = PageRequest.of(0, PAGE_SIZE);

        Page<Person> personPage = new PageImpl<>(
                List.of(person),
                pageable,
                1
        );

        given(personRepository.findAll(pageable)).willReturn(personPage);
        Set<Long> expectedParentIds = person.getParents().stream()
                .map(Person::getId)
                .collect(Collectors.toSet());

        //when
        Page<PersonDTO> result = personService.getAllPerson(pageable);

        //then
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.getTotalElements()).isEqualTo(1);
        List<PersonDTO> content = result.getContent();
        assertThat(content.getFirst().name()).isEqualTo("testperson");
        assertFalse(content.getFirst().parentIds().isEmpty());

        Set<Long> parentIds = content.getFirst().parentIds();
        assertThat(parentIds).isEqualTo(expectedParentIds);

    }

    @Test
    void testThatIfPersonExistsInTableGetByIdReturnsPerson() {
        //given
        given(personRepository.findById(ID)).willReturn(Optional.of(person));
        Set<Long> expectedParentIds = person.getParents().stream()
                .map(Person::getId)
                .collect(Collectors.toSet());

        //when
        Optional<PersonDTO> result = personService.getById(ID);

        //then
        assertThat(result.isEmpty()).isFalse();
        PersonDTO personDTO = result.get();
        assertThat(personDTO.name()).isEqualTo(PERSON_NAME);
        assertFalse(personDTO.parentIds().isEmpty());

        Set<Long> parentIds = personDTO.parentIds();
        assertThat(parentIds).isEqualTo(expectedParentIds);
    }

    @Test
    void testThatIfPersonNotExistsInTableGetByIdReturnsOptionalEmpty() {
        given(personRepository.findById(ID)).willReturn(Optional.empty());

        Optional<PersonDTO> result = personService.getById(ID);

        assertThat(result).isEmpty();

    }

    @Test
    void testSavePerson() {
        //given
        given(personRepository.findAllById(personDTO.parentIds())).willReturn(person.getParents().stream().toList());

        given(personRepository.save(argThat(p ->
                p.getName().equals(person.getName()) &&
                p.getId() == p.getId() &&
                p.getParents().equals(person.getParents()))))
                .willReturn(person);

        //when
        PersonDTO result = personService.savePerson(personDTO);

        //then
        assertNotNull(result);
        assertThat(result).isEqualTo(personDTO);
    }

    @Test
    void testUpdateNameOfAPerson() {
        //given
        long personId = personDTO.id();
        Set<Long> parentIds = personDTO.parentIds();
        var updateName = "updatedName";
        var updatedPerson = new Person(personId, updateName, person.getParents());
        var updatedPersonDto = new PersonDTO(personId, updateName, parentIds);


        given(personRepository.findById(personId)).willReturn(Optional.of(person));
        given(personRepository.findAllById(parentIds)).
                willReturn(updatedPerson.getParents().stream().toList());

        given(personRepository.save(argThat(updatedP ->
                updatedP.getName().equals(updatedPerson.getName()) &&
                updatedP.getId() == updatedPerson.getId() &&
                updatedP.getParents().equals(updatedPerson.getParents()))))
                .willReturn(updatedPerson);


        //when
        var result = personService.updatePerson(personId, updatedPersonDto);

        //then
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get()).isEqualTo(updatedPersonDto);

    }

    @NotNull
    private static Person createPerson() {
        return Person.builder()
                .id(3)
                .name(PERSON_NAME)
                .parents(createParents())
                .build();
    }

    private static Set<Person> createParents() {
        return Stream.of(
                Person.builder().id(1).name("TestParent1").build(),
                Person.builder().id(2).name("TestParent2").build()
        ).collect(Collectors.toSet());
    }

    private PersonDTO mapPersonToPersonDTO(Person person) {
        Set<Long> parentsIds = person.getParents()
                .stream()
                .map(Person::getId)
                .collect(Collectors.toSet());
        return new PersonDTO(person.getId(), person.getName(), parentsIds);
    }

}
