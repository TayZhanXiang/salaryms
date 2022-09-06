package sg.com.hr.salaryms.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "EMPLOYEE")
public class UserEntity {

    /*
     * This class is use for both object DTO and JPA schema creation
     */

    @Id
    @Column(name = "EMP_ID")
    private String id;

    @NotNull
    @Column(name = "EMP_NAME")
    private String name;

    @NotNull
    @Column(name = "EMP_LOGIN")
    private String login;

    @NotNull
    @Column(name = "EMP_SALARY")
    private Double salary;

    @NotNull
    @Column(name = "EMP_DTE_START")
    private LocalDate startDate;

    public UserEntity() {
        super();
    }

    public UserEntity(String id, String name, String login, Double salary, LocalDate startDate) {
        super();
        this.id = id;
        this.name = name;
        this.login = login;
        this.salary = salary;
        this.startDate = startDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserEntity other = (UserEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "UserEntity [id=" + id + ", login=" + login + ", name=" + name + ", salary=" + salary + ", startDate="
                + startDate + "]";
    }

}
