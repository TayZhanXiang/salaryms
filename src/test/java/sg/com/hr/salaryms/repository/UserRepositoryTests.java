package sg.com.hr.salaryms.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import sg.com.hr.salaryms.entity.UserEntity;

@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void existsByLoginAndIdNot_loginUnique_returnFalse() {
        entityManager.persist(new UserEntity("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.flush();

        assertEquals(false, userRepository.existsByLoginAndIdNot("lJoy", "tc002"));
    }

    @Test
    public void existsByLoginAndIdNot_loginUniqueFromId_returnFalse() {
        entityManager.persist(new UserEntity("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.flush();

        assertEquals(false, userRepository.existsByLoginAndIdNot("lJoe", "tc001"));
    }

    @Test
    public void existsByLoginAndIdNot_loginNotUnique_returnTrue() {
        entityManager.persist(new UserEntity("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.flush();

        assertEquals(true, userRepository.existsByLoginAndIdNot("lJoe", "tc002"));
    }

    @Test
    public void searchUser_nameMaxSalary_returnTwo() {
        entityManager.persist(new UserEntity("tc001", "Joel", "lJoel", 100.25, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.persist(new UserEntity("tc002", "Doep", "lDoep", 500.85, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.persist(new UserEntity("tc003", "Zoet", "lZoet", 1500.85, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.persist(new UserEntity("tc004", "Zet", "lZet", 900.85, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.flush();

        List<UserEntity> outputList = userRepository.searchUser("oE", 0.0, 1000.05, null);

        assertEquals(2, outputList.size());
    }

    @Test
    public void searchUser_nameMinMaxSalary_returnTwo() {
        entityManager.persist(new UserEntity("tc001", "Joel", "lJoel", 100.25, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.persist(new UserEntity("tc002", "Doep", "lDoep", 500.85, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.persist(new UserEntity("tc003", "Zoet", "lZoet", 1500.85, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.persist(new UserEntity("tc004", "Zet", "lZet", 900.85, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.flush();

        List<UserEntity> outputList = userRepository.searchUser("oE", 100.25, 1500.85, null);

        assertEquals(2, outputList.size());
    }

    @Test
    public void searchUser_maxSalary_returnThree() {
        entityManager.persist(new UserEntity("tc001", "Joel", "lJoel", 100.25, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.persist(new UserEntity("tc002", "Doep", "lDoep", 500.85, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.persist(new UserEntity("tc003", "Zoet", "lZoet", 1500.85, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.persist(new UserEntity("tc004", "Zet", "lZet", 900.85, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.flush();

        List<UserEntity> outputList = userRepository.searchUser(0.0, 1000.05, null);

        assertEquals(3, outputList.size());
    }

    @Test
    public void searchUser_minMaxSalary_returnThree() {
        entityManager.persist(new UserEntity("tc001", "Joel", "lJoel", 100.25, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.persist(new UserEntity("tc002", "Doep", "lDoep", 500.85, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.persist(new UserEntity("tc003", "Zoet", "lZoet", 1500.85, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.persist(new UserEntity("tc004", "Zet", "lZet", 900.85, LocalDate.of(2022, Month.JANUARY, 15)));
        entityManager.flush();

        List<UserEntity> outputList = userRepository.searchUser(100.25, 1500.85, null);

        assertEquals(3, outputList.size());
    }

}
