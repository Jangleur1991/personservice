package com.person.personservice.controller;

import com.person.personservice.model.PersonDTO;
import com.person.personservice.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static com.person.personservice.utils.Utils.getMAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
class PersonControllerTest {

    private static final int PAGE_SIZE = 5;
    private static final String PERSON_NAME = "testperson";

    private PersonDTO personDTO;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    @BeforeEach
    void setup() {
        personDTO = createPersonDto();
    }

    @Test
    void testThatGetReturnsAllPerson() throws Exception {
        //given
        PageRequest pageable = PageRequest.of(0, PAGE_SIZE);

        Page<PersonDTO> personPage = new PageImpl<>(
                List.of(personDTO),
                pageable,
                1
        );
        given(personService.getAllPerson(any(Pageable.class))).willReturn(personPage);

        //when / then
        mockMvc.perform(get("/person"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value(PERSON_NAME))
                .andExpect(jsonPath("$.content[0].parentIds").isEmpty());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(personService).getAllPerson(pageableCaptor.capture());

        PageRequest pageRequest = (PageRequest) pageableCaptor.getValue();
        assertThat(pageRequest.getPageNumber()).isEqualTo(0);
        assertThat(pageRequest.getPageSize()).isEqualTo(20);
        assertThat(pageRequest.getSort()).isEqualTo(Sort.unsorted());
    }

    @Test
    void testThatIfPersonCanBeFoundGetPersonByIdReturnsPerson() throws Exception {
        //given
        given(personService.getById(1)).willReturn(Optional.of(personDTO));

        //when / then
        mockMvc.perform(get("/person/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", aMapWithSize(2)))
                .andExpect(jsonPath("$.name").value(PERSON_NAME))
                .andExpect(jsonPath("$.parentIds").isEmpty());

    }

    @Test
    void testThatIfPersonCandNotBeFoundGetPersonByIdReturnsNotFound() throws Exception {
        //given
        given(personService.getById(1)).willReturn(Optional.empty());

        //when / then
        mockMvc.perform(get("/person/1"))
                .andExpect(status().isNotFound());

    }

    @Test
    void testThatCreatePersonReturnsSavedPerson() throws Exception {
        //given
        given(personService.savePerson(personDTO)).willReturn(personDTO);

        //when/then
        mockMvc.perform(post("/person")
                .content(getMAPPER().writeValueAsString(personDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/person/1"))
                .andExpect(jsonPath("$", aMapWithSize(3)))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(PERSON_NAME))
                .andExpect(jsonPath("$.parentIds").isEmpty());

    }

    private static PersonDTO createPersonDto() {
        return new PersonDTO(1, PERSON_NAME, new LinkedHashSet<>());
    }

}