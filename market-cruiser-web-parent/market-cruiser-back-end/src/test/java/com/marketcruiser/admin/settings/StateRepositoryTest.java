package com.marketcruiser.admin.settings;

import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.State;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class StateRepositoryTest {

    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private TestEntityManager entityManager;


    @Test
    public void testCreateStatesInAmerica() {
        Long countryId = 2L;
        Country country = entityManager.find(Country.class, countryId);

        State florida = stateRepository.save(new State("Florida", country));
        State california = stateRepository.save(new State("California", country));

        assertThat(florida).isNotNull();
        assertThat(california.getStateId()).isGreaterThan(1);
    }
}
