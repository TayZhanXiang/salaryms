package sg.com.hr.salaryms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import sg.com.hr.salaryms.dto.ResponseResultDTO;
import sg.com.hr.salaryms.entity.UserEntity;
import sg.com.hr.salaryms.service.UserService;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    /*
     * User api calls for upload, search, and CRUD functionality
     */

    private static Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping(path = "", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResponseResultDTO> createUser(@RequestBody UserEntity inputEntity) {
        log.info("CreateUser Input : Entity : " + inputEntity);
        return userService.createUserResult(inputEntity);
    }

    @PutMapping(path = "", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResponseResultDTO> updateOverwriteUser(@RequestBody UserEntity inputEntity) {
        log.info("UpdateOverwriteUser Input : Entity : " + inputEntity);
        return userService.updateOverwriteUserResult(inputEntity);
    }

    @PatchMapping(path = "", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResponseResultDTO> updatePartialUser(@RequestBody UserEntity inputEntity) {
        log.info("UpdatePartialUser Input : Entity : " + inputEntity);
        return userService.updatePartialUserResult(inputEntity);
    }

    @DeleteMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<ResponseResultDTO> deleteUser(@PathVariable String id) {
        log.info("DeleteUser Input : Id : " + id);
        return userService.deleteUserResult(id);
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<ResponseResultDTO> getUserObj(@PathVariable String id) {
        log.info("GetUserObj Input : Id : " + id);
        return userService.getUserObjResult(id);
    }

    @GetMapping(path = "", produces = "application/json")
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
        return userService.searchUserListResult(name, minSalary, maxSalary, offset, limit, sort, order);
    }

}
