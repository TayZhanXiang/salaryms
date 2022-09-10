package sg.com.hr.salaryms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import sg.com.hr.salaryms.dto.OffsetPageableDTO;
import sg.com.hr.salaryms.dto.UserDTO;
import sg.com.hr.salaryms.entity.UserEntity;
import sg.com.hr.salaryms.exception.InvalidInputException;
import sg.com.hr.salaryms.exception.NoSuchObjException;
import sg.com.hr.salaryms.exception.ObjExistedException;
import sg.com.hr.salaryms.repository.UserRepository;
import sg.com.hr.salaryms.utility.CommonString;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void createUser_saveOne_returnDTO() {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(new UserEntity("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15)));
        when(userRepository.existsById(anyString())).thenReturn(false);
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);
        UserEntity output = userService.createUser(input);

        assertEquals(input.getId(), output.getId());
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userRepository, times(1)).existsById(anyString());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void createUser_idExist_throwException() {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.existsById(anyString())).thenReturn(true);
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(ObjExistedException.class, () -> userService.createUser(input));
        assertEquals(CommonString.ERROR_EMP_ID_EXIST, exception.getMessage());
        verify(userRepository, times(1)).existsById(anyString());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void createUser_loginExist_throwException() {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(true);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.createUser(input));
        assertEquals(CommonString.ERROR_EMP_LOGIN_EXIST, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void createUser_salaryLessZero_throwException() {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", -0.1, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.createUser(input));
        assertEquals(CommonString.ERROR_INVALID_SALARY, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void createUser_idEmpty_throwException() {
        UserDTO input = new UserDTO("", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.createUser(input));
        assertEquals(CommonString.ERROR_MISSING_ID, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void createUser_nameEmpty_throwException() {
        UserDTO input = new UserDTO("tc001", "", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.createUser(input));
        assertEquals(CommonString.ERROR_MISSING_NAME, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void createUser_loginEmpty_throwException() {
        UserDTO input = new UserDTO("tc001", "Joe", "", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.createUser(input));
        assertEquals(CommonString.ERROR_MISSING_LOGIN, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void createUser_salaryNull_throwException() {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", null, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.createUser(input));
        assertEquals(CommonString.ERROR_MISSING_SALARY, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void createUser_startDateNull_throwException() {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, null);
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.createUser(input));
        assertEquals(CommonString.ERROR_MISSING_DATE, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void createUser_salaryNullStartDateNull_throwException() {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", null, null);
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.createUser(input));
        assertEquals(CommonString.ERROR_MISSING_SALARY + ", " + CommonString.ERROR_MISSING_DATE,
                exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void uploadUserList_saveOne_returnTrue() {
        MockMultipartFile csvMockFile = new MockMultipartFile("file", "test.csv", "text/csv",
                "id,login,name,salary,startDate\ne0001,hpotter,Harry Potter,1234.00,16-Nov-01".getBytes());
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(new UserEntity("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15)));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);
        boolean outputBoolean = userService.uploadUserList(csvMockFile);

        assertEquals(true, outputBoolean);
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void uploadUserList_saveNone_returnFalse() {
        MockMultipartFile csvMockFile = new MockMultipartFile("file", "test.csv", "text/csv", "test data".getBytes());
        boolean outputBoolean = userService.uploadUserList(csvMockFile);

        assertEquals(false, outputBoolean);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void uploadUserList_saveNoneCommented_returnFalse() {
        MockMultipartFile csvMockFile = new MockMultipartFile("file", "test.csv", "text/csv",
                "id,login,name,salary,startDate\n#e0001,hpotter,Harry Potter,1234.00,16-Nov-01".getBytes());
        boolean outputBoolean = userService.uploadUserList(csvMockFile);

        assertEquals(false, outputBoolean);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void uploadUserList_duplicateId_throwException() {
        MockMultipartFile csvMockFile = new MockMultipartFile("file", "test.csv", "text/csv",
                "id,login,name,salary,startDate\ned002,wesley,Wesley Woo,19234.50,2001-11-16\ned002,ranger,Granger,0.0,2001-11-18"
                        .getBytes());

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.uploadUserList(csvMockFile));
        assertEquals(CommonString.ERROR_DUPLICATE_ROW, exception.getMessage());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void uploadUserList_loginExist_throwException() {
        MockMultipartFile csvMockFile = new MockMultipartFile("file", "test.csv", "text/csv",
                "id,login,name,salary,startDate\ned002,wesley,Wesley Woo,19234.50,2001-11-16\ned003,wesley,Granger,0.0,2001-11-18"
                        .getBytes());

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.uploadUserList(csvMockFile));
        assertEquals(CommonString.ERROR_DUPLICATE_ROW, exception.getMessage());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void uploadUserList_salaryLessZero_throwException() {
        MockMultipartFile csvMockFile = new MockMultipartFile("file", "test.csv", "text/csv",
                "id,login,name,salary,startDate\ned002,wesley,Wesley Woo,-0.1,2001-11-16".getBytes());
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.uploadUserList(csvMockFile));
        assertEquals(CommonString.ERROR_INVALID_SALARY, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void uploadUserList_salaryEmpty_throwException() {
        MockMultipartFile csvMockFile = new MockMultipartFile("file", "test.csv", "text/csv",
                "id,login,name,salary,startDate\ned002,wesley,Wesley Woo,,2001-11-16".getBytes());
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.uploadUserList(csvMockFile));
        assertEquals(CommonString.ERROR_MISSING_SALARY, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void uploadUserList_salaryEmptyStartDateEmpty_throwException() {
        MockMultipartFile csvMockFile = new MockMultipartFile("file", "test.csv", "text/csv",
                "id,login,name,salary,startDate\ned002,wesley,Wesley Woo,,".getBytes());
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.uploadUserList(csvMockFile));
        assertEquals(CommonString.ERROR_MISSING_SALARY + ", " + CommonString.ERROR_MISSING_DATE,
                exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void uploadUserList_invalidFileType_throwException() {
        MockMultipartFile csvMockFile = new MockMultipartFile("file", "test.pdf", "application/pdf",
                "test data".getBytes());

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.uploadUserList(csvMockFile));
        assertEquals(CommonString.ERROR_INVALID_FILE_FORMAT, exception.getMessage());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void uploadUserList_emptyFile_throwException() {
        MockMultipartFile csvMockFile = new MockMultipartFile("file", "test.pdf", "application/pdf", "".getBytes());

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.uploadUserList(csvMockFile));
        assertEquals(CommonString.ERROR_MISSING_FILE, exception.getMessage());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updateOverwriteUser_saveOne_returnDTO() {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(new UserEntity("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15)));
        when(userRepository.existsById(anyString())).thenReturn(true);
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        UserEntity output = userService.updateOverwriteUser(input);
        assertEquals(input.getId(), output.getId());
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userRepository, times(1)).existsById(anyString());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updateOverwriteUser_idNotExist_throwException() {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.existsById(anyString())).thenReturn(false);
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(NoSuchObjException.class, () -> userService.updateOverwriteUser(input));
        assertEquals(CommonString.ERROR_EMP_NOTFOUND, exception.getMessage());
        verify(userRepository, times(1)).existsById(anyString());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updateOverwriteUser_loginExist_throwException() {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(true);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.updateOverwriteUser(input));
        assertEquals(CommonString.ERROR_EMP_LOGIN_EXIST, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updateOverwriteUser_salaryLessZero_throwException() {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", -0.1, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.updateOverwriteUser(input));
        assertEquals(CommonString.ERROR_INVALID_SALARY, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updateOverwriteUser_idEmpty_throwException() {
        UserDTO input = new UserDTO("", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.updateOverwriteUser(input));
        assertEquals(CommonString.ERROR_MISSING_ID, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updateOverwriteUser_nameEmpty_throwException() {
        UserDTO input = new UserDTO("tc001", "", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.updateOverwriteUser(input));
        assertEquals(CommonString.ERROR_MISSING_NAME, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updateOverwriteUser_loginEmpty_throwException() {
        UserDTO input = new UserDTO("tc001", "Joe", "", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.updateOverwriteUser(input));
        assertEquals(CommonString.ERROR_MISSING_LOGIN, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updateOverwriteUser_salaryNull_throwException() {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", null, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.updateOverwriteUser(input));
        assertEquals(CommonString.ERROR_MISSING_SALARY, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updateOverwriteUser_startDateNull_throwException() {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, null);
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.updateOverwriteUser(input));
        assertEquals(CommonString.ERROR_MISSING_DATE, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updateOverwriteUser_salaryNullStartDateNull_throwException() {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", null, null);
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.updateOverwriteUser(input));
        assertEquals(CommonString.ERROR_MISSING_SALARY + ", " + CommonString.ERROR_MISSING_DATE,
                exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updatePartialUser_saveOne_returnDTO() {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(new UserEntity("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15)));
        when(userRepository.findById(anyString())).thenReturn(
                Optional.of(new UserEntity("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15))));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);
        UserEntity output = userService.updatePartialUser(input);

        assertEquals(input.getId(), output.getId());
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userRepository, times(1)).findById(anyString());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updatePartialUser_partialName_returnDTO() {
        UserDTO input = new UserDTO("tc001", "Joe Tan", "", null, null);
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(new UserEntity("tc001", "Joe Tan", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15)));
        when(userRepository.findById(anyString())).thenReturn(
                Optional.of(new UserEntity("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15))));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);
        UserEntity output = userService.updatePartialUser(input);

        assertEquals(input.getName(), output.getName());
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userRepository, times(1)).findById(anyString());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updatePartialUser_partialLogin_returnDTO() {
        UserDTO input = new UserDTO("tc001", "", "lJoeXY", null, null);
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(new UserEntity("tc001", "Joe", "lJoeXY", 100.25, LocalDate.of(2022, Month.JANUARY, 15)));
        when(userRepository.findById(anyString())).thenReturn(
                Optional.of(new UserEntity("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15))));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);
        UserEntity output = userService.updatePartialUser(input);

        assertEquals(input.getLogin(), output.getLogin());
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userRepository, times(1)).findById(anyString());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updatePartialUser_partialSalary_returnDTO() {
        UserDTO input = new UserDTO("tc001", "", "", 580.25, null);
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(new UserEntity("tc001", "Joe", "lJoe", 580.25, LocalDate.of(2022, Month.JANUARY, 15)));
        when(userRepository.findById(anyString())).thenReturn(
                Optional.of(new UserEntity("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15))));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);
        UserEntity output = userService.updatePartialUser(input);

        assertEquals(input.getSalary(), output.getSalary());
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userRepository, times(1)).findById(anyString());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updatePartialUser_partialStartDate_returnDTO() {
        UserDTO input = new UserDTO("tc001", "", "", null, LocalDate.of(2021, Month.FEBRUARY, 15));
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(new UserEntity("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2021, Month.FEBRUARY, 15)));
        when(userRepository.findById(anyString())).thenReturn(
                Optional.of(new UserEntity("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15))));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);
        UserEntity output = userService.updatePartialUser(input);

        assertEquals(input.getStartDate(), output.getStartDate());
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userRepository, times(1)).findById(anyString());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updatePartialUser_idNotExist_throwException() {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(NoSuchObjException.class, () -> userService.updatePartialUser(input));
        assertEquals(CommonString.ERROR_EMP_NOTFOUND, exception.getMessage());
        verify(userRepository, times(1)).findById(anyString());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updatePartialUser_loginExist_throwException() {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(true);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.updatePartialUser(input));
        assertEquals(CommonString.ERROR_EMP_LOGIN_EXIST, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updatePartialUser_salaryLessZero_throwException() {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", -0.1, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.updatePartialUser(input));
        assertEquals(CommonString.ERROR_INVALID_SALARY, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updatePartialUser_idEmpty_throwException() {
        UserDTO input = new UserDTO("", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userRepository.existsByLoginAndIdNot(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(InvalidInputException.class, () -> userService.updatePartialUser(input));
        assertEquals(CommonString.ERROR_MISSING_ID, exception.getMessage());
        verify(userRepository, times(1)).existsByLoginAndIdNot(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void deleteUser_deleteOne_void() {
        String inputId = "tc001";
        when(userRepository.existsById(anyString())).thenReturn(true);
        doNothing().when(userRepository).deleteById(anyString());

        userService.deleteUser(inputId);
        verify(userRepository, times(1)).existsById(anyString());
        verify(userRepository, times(1)).deleteById(anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void deleteUser_idNotExist_throwException() {
        String inputId = "tc001";
        when(userRepository.existsById(anyString())).thenReturn(false);

        Exception exception = assertThrows(NoSuchObjException.class, () -> userService.deleteUser(inputId));
        assertEquals(CommonString.ERROR_EMP_NOTFOUND, exception.getMessage());
        verify(userRepository, times(1)).existsById(anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void getUserObj_getOne_returnDTO() {
        String inputId = "tc001";
        when(userRepository.findById(anyString())).thenReturn(
                Optional.of(new UserEntity("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15))));
        UserEntity output = userService.getUserObj(inputId);

        assertEquals(inputId, output.getId());
        verify(userRepository, times(1)).findById(anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void getUserObj_idNotExist_throwException() {
        String inputId = "tc001";
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchObjException.class, () -> userService.getUserObj(inputId));
        assertEquals(CommonString.ERROR_EMP_NOTFOUND, exception.getMessage());
        verify(userRepository, times(1)).findById(anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void searchUserList_getTwoName_returnList() {
        when(userRepository.searchUser(anyString(), anyDouble(), anyDouble(), any(OffsetPageableDTO.class)))
                .thenReturn(Arrays.asList(new UserEntity(), new UserEntity()));
        List<UserEntity> outputList = userService.searchUserList("test", 100.5, 5000.5, 0, 0, "id", "asc");

        assertEquals(2, outputList.size());
        verify(userRepository, times(1)).searchUser(anyString(), anyDouble(), anyDouble(),
                any(OffsetPageableDTO.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void searchUserList_getTwoNoName_returnList() {
        when(userRepository.searchUser(anyDouble(), anyDouble(), any(OffsetPageableDTO.class)))
                .thenReturn(Arrays.asList(new UserEntity(), new UserEntity()));
        List<UserEntity> outputList = userService.searchUserList(null, 100.5, 5000.5, 0, 0, "id", "asc");

        assertEquals(2, outputList.size());
        verify(userRepository, times(1)).searchUser(anyDouble(), anyDouble(), any(OffsetPageableDTO.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void searchUserList_getNoneName_returnList() {
        when(userRepository.searchUser(anyString(), anyDouble(), anyDouble(), any(OffsetPageableDTO.class)))
                .thenReturn(Collections.emptyList());
        List<UserEntity> outputList = userService.searchUserList("test", 100.5, 5000.5, 0, 0, "id", "asc");

        assertEquals(0, outputList.size());
        verify(userRepository, times(1)).searchUser(anyString(), anyDouble(), anyDouble(),
                any(OffsetPageableDTO.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void searchUserList_getNoneNoName_returnList() {
        when(userRepository.searchUser(anyDouble(), anyDouble(), any(OffsetPageableDTO.class)))
                .thenReturn(Collections.emptyList());
        List<UserEntity> outputList = userService.searchUserList(null, 100.5, 5000.5, 0, 0, "id", "asc");

        assertEquals(0, outputList.size());
        verify(userRepository, times(1)).searchUser(anyDouble(), anyDouble(), any(OffsetPageableDTO.class));
        verifyNoMoreInteractions(userRepository);
    }

}
