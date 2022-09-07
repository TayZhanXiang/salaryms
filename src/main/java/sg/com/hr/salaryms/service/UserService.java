package sg.com.hr.salaryms.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import sg.com.hr.salaryms.dto.OffsetPageableDTO;
import sg.com.hr.salaryms.dto.ResponseResultDTO;
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

    public ResponseEntity<ResponseResultDTO> createUserResult(UserEntity inputEntity) {
        createUser(inputEntity);
        ResponseEntity<ResponseResultDTO> response = CommonMethod.responseCreated(CommonString.SUCCESS_CREATED);
        log.info("CreateUserResult Response : " + response);
        return response;
    }

    public ResponseEntity<ResponseResultDTO> updateOverwriteUserResult(UserEntity inputEntity) {
        updateOverwriteUser(inputEntity);
        ResponseEntity<ResponseResultDTO> response = CommonMethod.responseOk(CommonString.SUCCESS_UPDATED);
        log.info("UpdateOverwriteUserResult Response : " + response);
        return response;
    }

    public ResponseEntity<ResponseResultDTO> updatePartialUserResult(UserEntity inputEntity) {
        updatePartialUser(inputEntity);
        ResponseEntity<ResponseResultDTO> response = CommonMethod.responseOk(CommonString.SUCCESS_UPDATED);
        log.info("UpdatePartialUserResult Response : " + response);
        return response;
    }

    public ResponseEntity<ResponseResultDTO> deleteUserResult(String id) {
        deleteUser(id);
        ResponseEntity<ResponseResultDTO> response = CommonMethod.responseOk(CommonString.SUCCESS_DELETED);
        log.info("DeleteUserResult Response : " + response);
        return response;
    }

    public ResponseEntity<ResponseResultDTO> getUserObjResult(String id) {
        UserEntity user = getUserObj(id);
        ResponseEntity<ResponseResultDTO> response = CommonMethod.responseOk(user);
        log.info("GetUserObjResult Response : " + response);
        return response;
    }

    public ResponseEntity<ResponseResultDTO> searchUserListResult(String name, Double minSalary, Double maxSalary,
            int offset, int limit, String sort, String order) {
        List<UserEntity> outputList = searchUserList(name, minSalary, maxSalary, offset, limit, sort, order);
        ResponseEntity<ResponseResultDTO> response = CommonMethod.responseOk(outputList);
        log.info("SearchUserListResult Response : " + response);
        return response;
    }

    public void createUser(UserEntity inputEntity) {
        if (inputEntity != null) {
            if (!userRepository.existsById(inputEntity.getId())) {
                // Check for multiple validation together
                List<String> errorList = inputValidation(inputEntity, true);
                if (errorList.isEmpty()) {
                    userRepository.save(inputEntity);
                } else {
                    throw new InvalidInputException(
                            errorList.stream().map(i -> i.toString()).collect(Collectors.joining(", ")));
                }
            } else {
                throw new ObjExistedException(CommonString.ERROR_EMP_ID_EXIST);
            }
        }
    }

    public void updateOverwriteUser(UserEntity inputEntity) {
        if (inputEntity != null) {
            if (userRepository.existsById(inputEntity.getId())) {
                // Check for multiple validation together
                List<String> errorList = inputValidation(inputEntity, true);
                if (errorList.isEmpty()) {
                    userRepository.save(inputEntity);
                } else {
                    throw new InvalidInputException(
                            errorList.stream().map(i -> i.toString()).collect(Collectors.joining(", ")));
                }
            } else {
                throw new NoSuchObjException(CommonString.ERROR_EMP_NOTFOUND);
            }
        }
    }

    public void updatePartialUser(UserEntity inputEntity) {
        if (inputEntity != null) {
            Optional<UserEntity> userOptional = userRepository.findById(inputEntity.getId());
            if (userOptional.isPresent()) {
                // Check for multiple validation together
                List<String> errorList = inputValidation(inputEntity, false);
                if (errorList.isEmpty()) {
                    // Merge partial fields into base entity for update as a whole
                    mergeObj(inputEntity, userOptional.get());
                    userRepository.save(userOptional.get());
                } else {
                    throw new InvalidInputException(
                            errorList.stream().map(i -> i.toString()).collect(Collectors.joining(", ")));
                }
            } else {
                throw new NoSuchObjException(CommonString.ERROR_EMP_NOTFOUND);
            }
        }
    }

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

    private List<String> inputValidation(UserEntity inputEntity, boolean requiredFlag) {
        List<String> errorList = new ArrayList<String>();
        if (inputEntity != null) {
            if (requiredFlag && inputEntity.getId() == null) {
                errorList.add(CommonString.ERROR_MISSING_ID);
            }
            if (requiredFlag && inputEntity.getName() == null) {
                errorList.add(CommonString.ERROR_MISSING_NAME);
            }
            if (inputEntity.getLogin() != null
                    && userRepository.existsByLoginAndIdNot(inputEntity.getLogin(), inputEntity.getId())) {
                errorList.add(CommonString.ERROR_EMP_LOGIN_EXIST);
            } else if (requiredFlag && inputEntity.getLogin() == null) {
                errorList.add(CommonString.ERROR_MISSING_LOGIN);
            }
            if (inputEntity.getSalary() != null && inputEntity.getSalary() < 0) {
                errorList.add(CommonString.ERROR_INVALID_SALARY);
            } else if (requiredFlag && inputEntity.getSalary() == null) {
                errorList.add(CommonString.ERROR_MISSING_SALARY);
            }
            LocalDate baseDate = LocalDate.of(1920, 1, 1);
            if (inputEntity.getStartDate() != null && baseDate.isAfter(inputEntity.getStartDate())) {
                errorList.add(CommonString.ERROR_INVALID_DATE);
            } else if (requiredFlag && inputEntity.getStartDate() == null) {
                errorList.add(CommonString.ERROR_MISSING_DATE);
            }
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
