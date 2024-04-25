package com.person.personservice.controller;

import com.person.personservice.model.PersonDTO;
import com.person.personservice.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashSet;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class PersonControllerTest {

    private static final int PAGE_SIZE = 5;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    @Test
    void testThatGetReturnsAllPerson() throws Exception {
        //given
        PersonDTO person = createPersonDto();

        Page<PersonDTO> personPage = new PageImpl<>(
                List.of(person),
                PageRequest.of(0, PAGE_SIZE),
                1
        );
        given(personService.getAllPerson()).willReturn(personPage);

        //when / then
        mockMvc.perform(get("/person"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("testperson"))
                .andExpect(jsonPath("$.content[0].parentIds").isEmpty());

    }


    private PersonDTO createPersonDto() {
        return new PersonDTO("testperson", new LinkedHashSet<>());
    }
}