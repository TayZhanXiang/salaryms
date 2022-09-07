package sg.com.hr.salaryms.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class UserDTO implements Serializable {

    /*
     * This class is use for object DTO
     */

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String login;
    private Double salary;
    private LocalDate startDate;

    public UserDTO() {
        super();
    }

    public UserDTO(String id, String name, String login, Double salary, LocalDate startDate) {
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
        UserDTO other = (UserDTO) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "UserDTO [id=" + id + ", login=" + login + ", name=" + name + ", salary=" + salary + ", startDate="
                + startDate + "]";
    }

}
