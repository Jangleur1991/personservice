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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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

        given(personRepository.findAll(pageable))
                .willReturn(personPage);

        //when
        Page<PersonDTO> result = personService.getAllPerson(pageable);

        //then
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.getTotalElements()).isEqualTo(1);
        List<PersonDTO> content = result.getContent();
        assertThat(content.getFirst().name()).isEqualTo("testperson");
        assertThat(content.getFirst().parentIds()).isEmpty();
    }

    @Test
    void testThatIfPersonExistsInTableGetByIdReturnsPerson() {
        //given
        given(personRepository.findById(ID)).willReturn(Optional.of(person));

        //when
        Optional<PersonDTO> result = personService.getById(ID);

        //then
        assertThat(result.isEmpty()).isFalse();
        PersonDTO personDTO = result.get();
        assertThat(personDTO.name()).isEqualTo(PERSON_NAME);
        assertThat(personDTO.parentIds()).isEmpty();
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
        given(personRepository.save(any(Person.class))).willReturn(person);


        //when
        PersonDTO result = personService.savePerson(personDTO);

        //then
        assertNotNull(result);
        assertThat(result).isEqualTo(personDTO);
    }

    @Test
    void testUpdatePerson() {
        //given
        var updateName = "updatedName";
        var updatedPersonDto = new PersonDTO(personDTO.id(), updateName, personDTO.parentIds());

        //when
        var result = personService.updatePerson(personDTO.id(), updatedPersonDto);

        //then
        assertThat(result).isEqualTo(updatedPersonDto);

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
