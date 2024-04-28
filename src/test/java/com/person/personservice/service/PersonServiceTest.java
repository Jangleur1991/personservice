package com.person.personservice.service;

import com.person.personservice.model.PersonDTO;
import com.person.personservice.persistence.domain.Person;
import com.person.personservice.persistence.repository.PersonRepository;
import org.jetbrains.annotations.NotNull;
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
    @InjectMocks
    private PersonService personService;

    @Mock
    private PersonRepository personRepository;

    @Test
    void testThatGetAllPersonReturnsAllPerson() {
        //given
        Person person = createPerson();

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
        Person person = createPerson();
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
    @SuppressWarnings("unchecked")
    void testSavePerson() {
        //given
        var person = createPerson();
        given(personRepository.findAllById(any())).willReturn(person.getParents().stream().toList());
        given(personRepository.save(any(Person.class))).willReturn(person);

        var personDTO = new PersonDTO(
                person.getId(),
                person.getName(),
                person.getParents().stream().map(Person::getId).collect(Collectors.toSet())
        );

        //when
        PersonDTO result = personService.savePerson(personDTO);

        //then
        assertNotNull(result);
        assertThat(result).isEqualTo(personDTO);
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

}