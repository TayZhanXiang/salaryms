package sg.com.hr.salaryms.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import sg.com.hr.salaryms.dto.OffsetPageableDTO;
import sg.com.hr.salaryms.dto.UserDTO;
import sg.com.hr.salaryms.entity.UserEntity;
import sg.com.hr.salaryms.exception.InvalidInputException;
import sg.com.hr.salaryms.exception.NoSuchObjException;
import sg.com.hr.salaryms.exception.ObjExistedException;
import sg.com.hr.salaryms.repository.UserRepository;
import sg.com.hr.salaryms.utility.CommonMethod;
import sg.com.hr.salaryms.utility.CommonString;

@Service
public class UserService {

    /*
     * This class is for the business logic and validation of related entities
     */

    private static Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserEntity createUser(UserDTO inputDTO) {
        UserEntity result = null;
        if (inputDTO != null) {
            // Check for multiple validation together
            List<String> errorList = inputValidation(inputDTO, true);
            if (errorList.isEmpty()) {
                if (!userRepository.existsById(inputDTO.getId())) {
                    result = userRepository.save(convertToEntity(inputDTO));
                } else {
                    throw new ObjExistedException(CommonString.ERROR_EMP_ID_EXIST);
                }
            } else {
                throw new InvalidInputException(
                        errorList.stream().map(i -> i.toString()).collect(Collectors.joining(", ")));
            }
        }
        return result;
    }

    @Transactional
    public boolean uploadUserList(MultipartFile inputFile) {
        try {
            List<String> errorFileList = inputFileValidation(inputFile, true);
            if (errorFileList.isEmpty()) {
                List<UserDTO> inputList = csvToUserList(inputFile.getInputStream());
                if (isUnique(inputList)) {
                    boolean creationFlag = false;
                    for (UserDTO inputDTO : inputList) {
                        // Check for multiple validation together
                        List<String> errorList = inputValidation(inputDTO, true);
                        if (errorList.isEmpty()) {
                            // Overwrite record if inputDTO id exist
                            creationFlag = userRepository.save(convertToEntity(inputDTO)) != null;
                        } else {
                            throw new InvalidInputException(
                                    errorList.stream().map(i -> i.toString()).collect(Collectors.joining(", ")));
                        }
                    }
                    return creationFlag;
                } else {
                    throw new InvalidInputException(CommonString.ERROR_DUPLICATE_ID);
                }
            } else {
                throw new InvalidInputException(
                        errorFileList.stream().map(i -> i.toString()).collect(Collectors.joining(", ")));
            }
        } catch (IOException exception) {
            throw new InvalidInputException(CommonString.ERROR_INVALID_FORMAT);
        }
    }

    @Transactional
    public UserEntity updateOverwriteUser(UserDTO inputDTO) {
        UserEntity result = null;
        if (inputDTO != null) {
            // Check for multiple validation together
            List<String> errorList = inputValidation(inputDTO, true);
            if (errorList.isEmpty()) {
                if (userRepository.existsById(inputDTO.getId())) {
                    result = userRepository.save(convertToEntity(inputDTO));
                } else {
                    throw new NoSuchObjException(CommonString.ERROR_EMP_NOTFOUND);
                }
            } else {
                throw new InvalidInputException(
                        errorList.stream().map(i -> i.toString()).collect(Collectors.joining(", ")));
            }
        }
        return result;
    }

    @Transactional
    public UserEntity updatePartialUser(UserDTO inputDTO) {
        UserEntity result = null;
        if (inputDTO != null) {
            // Check for multiple validation together
            List<String> errorList = inputValidation(inputDTO, false);
            if (errorList.isEmpty()) {
                UserEntity userEntity = userRepository.findById(inputDTO.getId())
                        .orElseThrow(() -> new NoSuchObjException(CommonString.ERROR_EMP_NOTFOUND));
                // Merge partial fields into base entity for update as a whole
                mergeObj(convertToEntity(inputDTO), userEntity);
                result = userRepository.save(userEntity);
            } else {
                throw new InvalidInputException(
                        errorList.stream().map(i -> i.toString()).collect(Collectors.joining(", ")));
            }
        }
        return result;
    }

    @Transactional
    public void deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new NoSuchObjException(CommonString.ERROR_EMP_NOTFOUND);
        }
    }

    public UserEntity getUserObj(String id) {
        Optional<UserEntity> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new NoSuchObjException(CommonString.ERROR_EMP_NOTFOUND);
        }
    }

    public List<UserEntity> searchUserList(String name, Double minSalary, Double maxSalary, int offset, int limit,
            String sort, String order) {
        List<UserEntity> outputList = new ArrayList<UserEntity>();
        if (limit == 0) {
            limit = Integer.MAX_VALUE;
        }
        Pageable pageable = new OffsetPageableDTO(offset, limit, Sort.by(orderMapping(order), sortMapping(sort)));
        if (name != null && !name.isEmpty()) {
            outputList = userRepository.searchUser(name, minSalary, maxSalary, pageable);
        } else {
            outputList = userRepository.searchUser(minSalary, maxSalary, pageable);
        }
        return outputList;
    }

    private List<UserDTO> csvToUserList(InputStream inputStream) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.builder().setHeader()
                        .setSkipHeaderRecord(true).setIgnoreHeaderCase(true).setCommentMarker('#').setTrim(true)
                        .build())) {
            List<UserDTO> outputList = new ArrayList<UserDTO>();
            Iterable<CSVRecord> csvRecordList = csvParser.getRecords();
            // To support two date format
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .appendOptional(DateTimeFormatter.ofPattern("dd-MMM-yy"))
                    .toFormatter();
            for (CSVRecord csvRecord : csvRecordList) {
                UserDTO user = new UserDTO(csvRecord.get("id"), csvRecord.get("name"),
                        csvRecord.get("login"), CommonMethod.stringToDouble(csvRecord.get("salary")).orElse(null),
                        CommonMethod.stringToLocalDate(csvRecord.get("startDate"), formatter).orElse(null));
                outputList.add(user);
            }
            log.debug("CsvToUserList Record Size : " + outputList.size() + " with " + outputList);
            return outputList;
        } catch (IOException | NumberFormatException exception) {
            throw new InvalidInputException(CommonString.ERROR_INVALID_FORMAT);
        }
    }

    private List<String> inputValidation(UserDTO inputDTO, boolean requiredFlag) {
        List<String> errorList = new ArrayList<String>();
        if (inputDTO != null) {
            if (requiredFlag && (inputDTO.getId() == null || inputDTO.getId().isEmpty())) {
                errorList.add(CommonString.ERROR_MISSING_ID);
            }
            if (requiredFlag && (inputDTO.getName() == null || inputDTO.getName().isEmpty())) {
                errorList.add(CommonString.ERROR_MISSING_NAME);
            }
            if (inputDTO.getLogin() != null
                    && userRepository.existsByLoginAndIdNot(inputDTO.getLogin(), inputDTO.getId())) {
                errorList.add(CommonString.ERROR_EMP_LOGIN_EXIST);
            } else if (requiredFlag && (inputDTO.getLogin() == null || inputDTO.getLogin().isEmpty())) {
                errorList.add(CommonString.ERROR_MISSING_LOGIN);
            }
            if (inputDTO.getSalary() != null && inputDTO.getSalary() < 0) {
                errorList.add(CommonString.ERROR_INVALID_SALARY);
            } else if (requiredFlag && inputDTO.getSalary() == null) {
                errorList.add(CommonString.ERROR_MISSING_SALARY);
            }
            LocalDate baseDate = LocalDate.of(1920, 1, 1);
            if (inputDTO.getStartDate() != null && baseDate.isAfter(inputDTO.getStartDate())) {
                errorList.add(CommonString.ERROR_INVALID_DATE);
            } else if (requiredFlag && inputDTO.getStartDate() == null) {
                errorList.add(CommonString.ERROR_MISSING_DATE);
            }
        }
        return errorList;
    }

    private List<String> inputFileValidation(MultipartFile inputFile, boolean requiredFlag) {
        List<String> errorList = new ArrayList<String>();
        if (requiredFlag && inputFile.isEmpty()) {
            errorList.add(CommonString.ERROR_MISSING_FILE);
        }
        if (!inputFile.isEmpty() && !"text/csv".equals(inputFile.getContentType())) {
            errorList.add(CommonString.ERROR_INVALID_FILE_FORMAT);
        }
        return errorList;
    }

    private void mergeObj(UserEntity inputEntity, UserEntity baseEntity) {
        if (inputEntity != null && baseEntity != null) {
            if (inputEntity.getName() != null) {
                baseEntity.setName(inputEntity.getName());
            }
            if (inputEntity.getLogin() != null) {
                baseEntity.setLogin(inputEntity.getLogin());
            }
            if (inputEntity.getSalary() != null) {
                baseEntity.setSalary(inputEntity.getSalary());
            }
            if (inputEntity.getStartDate() != null) {
                baseEntity.setStartDate(inputEntity.getStartDate());
            }
        }
    }

    private UserEntity convertToEntity(UserDTO inputDTO) {
        if (inputDTO != null) {
            return new UserEntity(inputDTO.getId(), inputDTO.getName(), inputDTO.getLogin(), inputDTO.getSalary(),
                    inputDTO.getStartDate());
        } else {
            return null;
        }
    }

    private boolean isUnique(List<UserDTO> inputList) {
        return inputList.stream().map(UserDTO::getId).distinct().count() == inputList.size();
    }

    private String sortMapping(String input) {
        switch (input.toLowerCase()) {
            case "id":
                return "id";
            case "name":
                return "name";
            case "login":
                return "login";
            case "salary":
                return "salary";
            case "startdate":
                return "startDate";
            default:
                return "id";
        }
    }

    private Direction orderMapping(String input) {
        switch (input.toLowerCase()) {
            case "asc":
                return Sort.Direction.ASC;
            case "desc":
                return Sort.Direction.DESC;
            default:
                return Sort.Direction.ASC;
        }
    }

}
