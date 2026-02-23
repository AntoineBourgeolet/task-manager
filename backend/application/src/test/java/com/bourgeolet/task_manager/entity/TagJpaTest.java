package com.bourgeolet.task_manager.entity;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class TagJpaTest {

    @Autowired
    private TestEntityManager em;

    private Validator validator;

    @BeforeEach
    void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void persist_shouldGenerateId_whenValid() {
        Tag tag = new Tag();
        tag.setName("backend");

        Tag saved = em.persistFlushFind(tag);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("backend");
    }

    @Test
    void validation_shouldFail_whenNameBlank() {
        Tag tag = new Tag();
        tag.setName("   ");

        var violations = validator.validate(tag);
        assertThat(violations)
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .contains("name");
    }

    @Test
    void uniqueConstraint_shouldThrow_onDuplicateName() {
        Tag t1 = new Tag(); t1.setName("api");
        Tag t2 = new Tag(); t2.setName("api");

        em.persistAndFlush(t1);
        assertThatThrownBy(() -> em.persistAndFlush(t2))
                .isInstanceOf(PersistenceException.class);
    }
}