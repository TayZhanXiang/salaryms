package sg.com.hr.salaryms.utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sg.com.hr.salaryms.dto.ResponseResultDTO;

public class CommonMethod {

    /*
     * This class is for all the common internal methods available for different
     * entities usage
     */

    public static ResponseEntity<ResponseResultDTO> responseOk(Object responseBody) {
        ResponseResultDTO responseResult = new ResponseResultDTO(responseBody);
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }

    public static ResponseEntity<ResponseResultDTO> responseOk(String message) {
        ResponseResultDTO responseResult = new ResponseResultDTO(message);
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }

    public static ResponseEntity<ResponseResultDTO> responseCreated(String message) {
        ResponseResultDTO responseResult = new ResponseResultDTO(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseResult);
    }

    public static Optional<Double> stringToDouble(String value) {
        return value == null || value.isEmpty() ? Optional.empty() : Optional.of(Double.valueOf(value));
    }

    public static Optional<LocalDate> stringToLocalDate(String value, DateTimeFormatter formatter) {
        return value == null || value.isEmpty() ? Optional.empty() : Optional.of(LocalDate.parse(value, formatter));
    }

}
