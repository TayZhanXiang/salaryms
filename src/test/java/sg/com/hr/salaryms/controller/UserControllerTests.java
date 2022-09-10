package sg.com.hr.salaryms.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.com.hr.salaryms.dto.UserDTO;
import sg.com.hr.salaryms.entity.UserEntity;
import sg.com.hr.salaryms.exception.AppExceptionHandler;
import sg.com.hr.salaryms.exception.InvalidInputException;
import sg.com.hr.salaryms.exception.NoSuchObjException;
import sg.com.hr.salaryms.exception.ObjExistedException;
import sg.com.hr.salaryms.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTests {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new AppExceptionHandler())
                .build();
    }

    @Test
    public void createUser_saveOne_httpCreated() throws Exception {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userService.createUser(any(UserDTO.class)))
                .thenReturn(new UserEntity("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15)));
        MockHttpServletResponse response = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapToJson(input))).andReturn().getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        verify(userService, times(1)).createUser(any(UserDTO.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void createUser_invalidInputException_httpBadRequest() throws Exception {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userService.createUser(any(UserDTO.class))).thenThrow(new InvalidInputException(anyString()));
        MockHttpServletResponse response = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapToJson(input))).andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("message"));
        verify(userService, times(1)).createUser(any(UserDTO.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void createUser_objExistedException_httpBadRequest() throws Exception {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userService.createUser(any(UserDTO.class))).thenThrow(new ObjExistedException(anyString()));
        MockHttpServletResponse response = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapToJson(input))).andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("message"));
        verify(userService, times(1)).createUser(any(UserDTO.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void uploadUserList_saveOne_httpCreated() throws Exception {
        MockMultipartFile csvMockFile = new MockMultipartFile("file", "test.csv", "text/csv",
                "id,login,name,salary,startDate\ne0001,hpotter,Harry Potter,1234.00,16-Nov-01".getBytes());
        when(userService.uploadUserList(any(MockMultipartFile.class))).thenReturn(true);
        MockHttpServletResponse response = mockMvc.perform(multipart("/users/upload").file(csvMockFile)).andReturn()
                .getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        verify(userService, times(1)).uploadUserList(any(MockMultipartFile.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void uploadUserList_saveNone_httpOk() throws Exception {
        MockMultipartFile csvMockFile = new MockMultipartFile("file", "test.csv", "text/csv",
                "id,login,name,salary,startDate\ne0001,hpotter,Harry Potter,1234.00,16-Nov-01".getBytes());
        when(userService.uploadUserList(any(MockMultipartFile.class))).thenReturn(false);
        MockHttpServletResponse response = mockMvc.perform(multipart("/users/upload").file(csvMockFile)).andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        verify(userService, times(1)).uploadUserList(any(MockMultipartFile.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void uploadUserList_invalidInputException_httpBadRequest() throws Exception {
        MockMultipartFile csvMockFile = new MockMultipartFile("file", "test.csv", "text/csv",
                "id,login,name,salary,startDate\ne0001,hpotter,Harry Potter,1234.00,16-Nov-01".getBytes());
        when(userService.uploadUserList(any(MockMultipartFile.class)))
                .thenThrow(new InvalidInputException(anyString()));
        MockHttpServletResponse response = mockMvc.perform(multipart("/users/upload").file(csvMockFile)).andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("message"));
        verify(userService, times(1)).uploadUserList(any(MockMultipartFile.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void updateOverwriteUser_saveOne_httpOk() throws Exception {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userService.updateOverwriteUser(any(UserDTO.class)))
                .thenReturn(new UserEntity("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15)));
        MockHttpServletResponse response = mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapToJson(input))).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        verify(userService, times(1)).updateOverwriteUser(any(UserDTO.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void updateOverwriteUser_invalidInputException_httpBadRequest() throws Exception {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userService.updateOverwriteUser(any(UserDTO.class))).thenThrow(new InvalidInputException(anyString()));
        MockHttpServletResponse response = mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapToJson(input))).andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("message"));
        verify(userService, times(1)).updateOverwriteUser(any(UserDTO.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void updateOverwriteUser_noSuchObjException_httpBadRequest() throws Exception {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userService.updateOverwriteUser(any(UserDTO.class))).thenThrow(new NoSuchObjException(anyString()));
        MockHttpServletResponse response = mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapToJson(input))).andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("message"));
        verify(userService, times(1)).updateOverwriteUser(any(UserDTO.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void updatePartialUser_saveOne_httpOk() throws Exception {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userService.updatePartialUser(any(UserDTO.class)))
                .thenReturn(new UserEntity("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15)));
        MockHttpServletResponse response = mockMvc.perform(patch("/users").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapToJson(input))).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        verify(userService, times(1)).updatePartialUser(any(UserDTO.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void updatePartialUser_invalidInputException_httpBadRequest() throws Exception {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userService.updatePartialUser(any(UserDTO.class))).thenThrow(new InvalidInputException(anyString()));
        MockHttpServletResponse response = mockMvc.perform(patch("/users").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapToJson(input))).andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("message"));
        verify(userService, times(1)).updatePartialUser(any(UserDTO.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void updatePartialUser_noSuchObjException_httpBadRequest() throws Exception {
        UserDTO input = new UserDTO("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15));
        when(userService.updatePartialUser(any(UserDTO.class))).thenThrow(new NoSuchObjException(anyString()));
        MockHttpServletResponse response = mockMvc.perform(patch("/users").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapToJson(input))).andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("message"));
        verify(userService, times(1)).updatePartialUser(any(UserDTO.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void deleteUser_deleteOne_httpOk() throws Exception {
        String inputId = "tc001";
        doNothing().when(userService).deleteUser(anyString());
        MockHttpServletResponse response = mockMvc
                .perform(delete("/users/" + inputId).contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        verify(userService, times(1)).deleteUser(anyString());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void deleteUser_noSuchObjException_httpBadRequest() throws Exception {
        String inputId = "tc001";
        doThrow(new NoSuchObjException("")).when(userService).deleteUser(anyString());
        MockHttpServletResponse response = mockMvc
                .perform(delete("/users/" + inputId).contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("message"));
        verify(userService, times(1)).deleteUser(anyString());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void getUserObj_getOne_httpOk() throws Exception {
        String inputId = "tc001";
        when(userService.getUserObj(anyString()))
                .thenReturn(new UserEntity("tc001", "Joe", "lJoe", 100.25, LocalDate.of(2022, Month.JANUARY, 15)));
        MockHttpServletResponse response = mockMvc
                .perform(get("/users/" + inputId).contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        verify(userService, times(1)).getUserObj(anyString());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void getUserObj_noSuchObjException_httpBadRequest() throws Exception {
        String inputId = "tc001";
        when(userService.getUserObj(anyString())).thenThrow(new NoSuchObjException(anyString()));
        MockHttpServletResponse response = mockMvc
                .perform(get("/users/" + inputId).contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("message"));
        verify(userService, times(1)).getUserObj(anyString());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void searchUserList_getTwo_httpOk() throws Exception {
        when(userService.searchUserList(null, 100.5, 5000.5, 0, 0, "id", "asc"))
                .thenReturn(Arrays.asList(new UserEntity(), new UserEntity()));
        MockHttpServletResponse response = mockMvc
                .perform(get("/users?minSalary=100.5&maxSalary=5000.5").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        verify(userService, times(1)).searchUserList(null, 100.5, 5000.5, 0, 0, "id", "asc");
        verifyNoMoreInteractions(userService);
    }

    private String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper.writeValueAsString(obj);
    }

}
