package sg.com.hr.salaryms.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import sg.com.hr.salaryms.dto.OffsetPageableDTO;
import sg.com.hr.salaryms.dto.ResponseResultDTO;
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

    public ResponseEntity<ResponseResultDTO> createUserResult(UserDTO inputDTO) {
        createUser(inputDTO);
        ResponseEntity<ResponseResultDTO> response = CommonMethod.responseCreated(CommonString.SUCCESS_CREATED);
        log.info("CreateUserResult Response : " + response);
        return response;
    }

    public ResponseEntity<ResponseResultDTO> uploadUserListResult(MultipartFile file) {
        boolean creationFlag = uploadUserList(file);
        ResponseEntity<ResponseResultDTO> response;
        if (creationFlag) {
            response = CommonMethod.responseCreated(CommonString.SUCCESS_FILE_CHANGED);
        } else {
            response = CommonMethod.responseOk(CommonString.SUCCESS_FILE_NOCHANGE);
        }
        log.info("UploadUserListResult Response : " + response);
        return response;
    }

    public ResponseEntity<ResponseResultDTO> updateOverwriteUserResult(UserDTO inputDTO) {
        updateOverwriteUser(inputDTO);
        ResponseEntity<ResponseResultDTO> response = CommonMethod.responseOk(CommonString.SUCCESS_UPDATED);
        log.info("UpdateOverwriteUserResult Response : " + response);
        return response;
    }

    public ResponseEntity<ResponseResultDTO> updatePartialUserResult(UserDTO inputDTO) {
        updatePartialUser(inputDTO);
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
    public boolean uploadUserList(MultipartFile file) {
        return true;
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

    private List<String> inputValidation(UserDTO inputDTO, boolean requiredFlag) {
        List<String> errorList = new ArrayList<String>();
        if (inputDTO != null) {
            if (requiredFlag && inputDTO.getId() == null) {
                errorList.add(CommonString.ERROR_MISSING_ID);
            }
            if (requiredFlag && inputDTO.getName() == null) {
                errorList.add(CommonString.ERROR_MISSING_NAME);
            }
            if (inputDTO.getLogin() != null
                    && userRepository.existsByLoginAndIdNot(inputDTO.getLogin(), inputDTO.getId())) {
                errorList.add(CommonString.ERROR_EMP_LOGIN_EXIST);
            } else if (requiredFlag && inputDTO.getLogin() == null) {
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

    private UserEntity convertToEntity(UserDTO inputDTO) {
        if (inputDTO != null) {
            return new UserEntity(inputDTO.getId(), inputDTO.getName(), inputDTO.getLogin(), inputDTO.getSalary(),
                    inputDTO.getStartDate());
        } else {
            return null;
        }
    }

}
