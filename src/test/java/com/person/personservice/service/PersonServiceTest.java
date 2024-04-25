package com.person.personservice.service;

import com.person.personservice.model.PersonDTO;
import com.person.personservice.persistence.domain.Person;
import com.person.personservice.persistence.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.LinkedHashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith( MockitoExtension.class )
class PersonServiceTest {

    private static final int PAGE_SIZE = 5;
    @InjectMocks
    private PersonService personService;

    @Mock
    private PersonRepository personRepository;

    @Test
    void testThatGetAllPersonReturnsAllPerson() {
        //given
        Person person = new Person();
        person.setId(1);
        person.setName("testperson");
        person.setParents(new LinkedHashSet<>());

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
        assertThat(content.get(0).name()).isEqualTo("testperson");
        assertThat(content.get(0).parentIds()).isEmpty();
    }

}