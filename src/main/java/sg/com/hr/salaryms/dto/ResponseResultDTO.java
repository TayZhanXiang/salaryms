package sg.com.hr.salaryms.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResultDTO implements Serializable {

    /*
     * This class is to faciliate the returning of result from API calls
     */

    private static final long serialVersionUID = 1L;

    @JsonUnwrapped
    private Object resultObj;
    private Object results;
    private String message;

    public ResponseResultDTO() {
        super();
    }

    public ResponseResultDTO(Object input) {
        super();
        if (input != null && input instanceof List) {
            results = input;
        } else {
            resultObj = input;
        }
    }

    public ResponseResultDTO(String message) {
        super();
        this.message = message;
    }

    public ResponseResultDTO(Object input, String message) {
        this(input);
        this.message = message;
    }

    public Object getResultObj() {
        return resultObj;
    }

    public void setResultObj(Object resultObj) {
        this.resultObj = resultObj;
    }

    public Object getResults() {
        return results;
    }

    public void setResults(Object results) {
        this.results = results;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((resultObj == null) ? 0 : resultObj.hashCode());
        result = prime * result + ((results == null) ? 0 : results.hashCode());
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
        ResponseResultDTO other = (ResponseResultDTO) obj;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (resultObj == null) {
            if (other.resultObj != null)
                return false;
        } else if (!resultObj.equals(other.resultObj))
            return false;
        if (results == null) {
            if (other.results != null)
                return false;
        } else if (!results.equals(other.results))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ResponseResultDTO [message=" + message + ", resultObj=" + resultObj + ", results=" + results + "]";
    }

}
