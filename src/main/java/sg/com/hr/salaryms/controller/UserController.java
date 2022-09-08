package sg.com.hr.salaryms.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import sg.com.hr.salaryms.dto.ResponseResultDTO;
import sg.com.hr.salaryms.dto.UserDTO;
import sg.com.hr.salaryms.entity.UserEntity;
import sg.com.hr.salaryms.service.UserService;
import sg.com.hr.salaryms.utility.CommonMethod;
import sg.com.hr.salaryms.utility.CommonString;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    /*
     * User api calls for upload, search, and CRUD functionality
     */

    private static Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseResultDTO> createUser(@RequestBody UserDTO inputDTO) {
        log.info("CreateUser Input : UserDTO : " + inputDTO);
        userService.createUser(inputDTO);
        ResponseEntity<ResponseResultDTO> response = CommonMethod.responseCreated(CommonString.SUCCESS_CREATED);
        log.info("CreateUser Response : " + response);
        return response;
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseResultDTO> uploadUserList(@RequestParam("file") MultipartFile file) {
        log.info("UploadUserList Input : File");
        boolean creationFlag = userService.uploadUserList(file);
        ResponseEntity<ResponseResultDTO> response;
        if (creationFlag) {
            response = CommonMethod.responseCreated(CommonString.SUCCESS_FILE_CHANGED);
        } else {
            response = CommonMethod.responseOk(CommonString.SUCCESS_FILE_NOCHANGE);
        }
        log.info("UploadUserList Response : " + response);
        return response;
    }

    @PutMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseResultDTO> updateOverwriteUser(@RequestBody UserDTO inputDTO) {
        log.info("UpdateOverwriteUser Input : UserDTO : " + inputDTO);
        userService.updateOverwriteUser(inputDTO);
        ResponseEntity<ResponseResultDTO> response = CommonMethod.responseOk(CommonString.SUCCESS_UPDATED);
        log.info("UpdateOverwriteUser Response : " + response);
        return response;
    }

    @PatchMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseResultDTO> updatePartialUser(@RequestBody UserDTO inputDTO) {
        log.info("UpdatePartialUser Input : UserDTO : " + inputDTO);
        userService.updatePartialUser(inputDTO);
        ResponseEntity<ResponseResultDTO> response = CommonMethod.responseOk(CommonString.SUCCESS_UPDATED);
        log.info("UpdatePartialUser Response : " + response);
        return response;
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseResultDTO> deleteUser(@PathVariable String id) {
        log.info("DeleteUser Input : Id : " + id);
        userService.deleteUser(id);
        ResponseEntity<ResponseResultDTO> response = CommonMethod.responseOk(CommonString.SUCCESS_DELETED);
        log.info("DeleteUser Response : " + response);
        return response;
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseResultDTO> getUserObj(@PathVariable String id) {
        log.info("GetUserObj Input : Id : " + id);
        UserEntity user = userService.getUserObj(id);
        ResponseEntity<ResponseResultDTO> response = CommonMethod.responseOk(user);
        log.info("GetUserObj Response : " + response);
        return response;
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseResultDTO> searchUserList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minSalary", required = false, defaultValue = "0") Double minSalary,
            @RequestParam(value = "maxSalary", required = false, defaultValue = "4000") Double maxSalary,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "0") int limit,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order) {
        log.info("SearchUserList Input : Name : " + name + " : MinSalary : " + minSalary + " : MaxSalary : " + maxSalary
                + " : Offset : " + offset + " : Limit : " + limit + " : Sort : " + sort + " : Order : " + order);
        List<UserEntity> outputList = userService.searchUserList(name, minSalary, maxSalary, offset, limit, sort,
                order);
        ResponseEntity<ResponseResultDTO> response = CommonMethod.responseOk(outputList);
        log.info("SearchUserList Response : " + response);
        return response;
    }

}
