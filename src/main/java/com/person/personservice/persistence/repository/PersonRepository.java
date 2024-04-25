package com.person.personservice.persistence.repository;

import com.person.personservice.persistence.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person,Long> {
}
