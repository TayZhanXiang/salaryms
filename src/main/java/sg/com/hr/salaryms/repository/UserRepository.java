package sg.com.hr.salaryms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sg.com.hr.salaryms.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    /*
     * This class is for the data access (JPA) of related entities
     */

    boolean existsById(String id);

    boolean existsByLoginAndIdNot(String login, String id);

}
