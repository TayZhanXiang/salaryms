package sg.com.hr.salaryms.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import sg.com.hr.salaryms.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    /*
     * This class is for the data access (JPA) of related entities
     */

    boolean existsByLoginAndIdNot(String login, String id);

    @Query("select n from UserEntity n where lower(n.name) like lower(concat('%', ?1,'%')) and n.salary >= ?2 and n.salary < ?3")
    List<UserEntity> searchUser(String name, Double minSalary, Double maxSalary, Pageable pageable);

    @Query("select n from UserEntity n where n.salary >= ?1 and n.salary < ?2")
    List<UserEntity> searchUser(Double minSalary, Double maxSalary, Pageable pageable);

}
