package pl.pss.PSS;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import pl.pss.PSS.controller.PssRestController;
import pl.pss.PSS.model.Delegation;
import pl.pss.PSS.model.User;
import pl.pss.PSS.model.enums.AutoCapacity;
import pl.pss.PSS.model.enums.TransportType;
import pl.pss.PSS.repository.DelegationRepository;
import pl.pss.PSS.repository.UserRepository;
import pl.pss.PSS.service.DelegationService;
import pl.pss.PSS.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=PssApplication.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = PssRestController.class)
public class PssRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    DelegationService delegationService;

    @MockBean
    UserService userService;

    @MockBean
    DelegationRepository delegationRepository;

    @MockBean
    UserRepository userRepository;

    @Test
    public void registerUserTest() throws Exception
    {
        mvc.perform( MockMvcRequestBuilders
                .post("/user/add")
                .param("companyName", "Kompania")
                .param("companyAddress", "Adres")
                .param("companyNip", "Nip")
                .param("name", "imie")
                .param("lastName", "nazwisko")
                .param("email", "email")
                .param("password", "haslo")
                //.content("{}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getAllUsersTest() throws Exception
    {
        User user = new User("Kompania","Adres","123","Imie","Nazwisko","Email","Haslo");
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user));
        mvc.perform( MockMvcRequestBuilders
                .get("/user/get")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].companyName").value("Kompania"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].companyAddress").value("Adres"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].companyNip").value("123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Imie"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName").value("Nazwisko"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("Email"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value(true))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.log())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void changePasswordTest() throws Exception
    {
        User user = new User("Kompania","Adres","123","Imie","Nazwisko","Email","Haslo");
        when(userService.changePassword(1,"abcdefg")).thenReturn(true);

        mvc.perform( MockMvcRequestBuilders
                .put("/user/changePassword/id={userId}",1)
                .contentType(MediaType.ALL_VALUE)
                .accept(MediaType.ALL_VALUE)
                .param("newPassword", "abcdefg")
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string("true"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteUserByIdTest() throws Exception
    {
        when(userService.deleteUserById(1)).thenReturn(true);
        mvc.perform( MockMvcRequestBuilders
                .delete("/user/delete")
                .param("userId", "1")
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string("true"))
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void addDelegationTest() throws Exception
    {
       Delegation delegation = new Delegation("opis", LocalDateTime.parse("2000-10-31T01:30:00.000"),LocalDateTime.parse("2010-12-31T01:30:00.000"),
                30L, 0,0,0,
                TransportType.CAR,90.0d, AutoCapacity.EQUAL_GREATER,80,22d,22d,
                22d,22d);
        when(delegationService.addDelegation(anyLong(), any(Delegation.class))).thenReturn(delegation);
        mvc.perform( MockMvcRequestBuilders
                .post("/delegation/add/id={userId}",1)

                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("description", "opis")
                .param("dateTimeStart", "2000-10-31T01:30:00.000")
                .param("dateTimeStop", "2010-12-31T01:30:00.000")
                .param("travelDietAmount", "30")
                .param("breakfastNumber", "0")
                .param("dinnerNumber", "0")
                .param("supperNumber", "0")
                .param("transportType", "CAR")
                .param("ticketPrice", "90")
                .param("autoCapacity", "EQUAL_GREATER")
                .param("km", "80")
                .param("accomodationPrice", "22")
                .param("otherTicketsPrice", "22")
                .param("otherOutlayDesc", "22")
                .param("otherOutlayPrice", "22")
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.delegationId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.delegationId").value(0L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dateTimeStart").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.dateTimeStart").value("2000-10-31 01:30:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dateTimeStart").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.dateTimeStop").value("2010-12-31 01:30:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.travelDietAmount").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.travelDietAmount").value(30L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakfastNumber").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakfastNumber").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dinnerNumber").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.dinnerNumber").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.supperNumber").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.supperNumber").value(0))


                .andDo(MockMvcResultHandlers.print());

    }
    @Test
    public void removeDelegationTest() throws Exception
    {
        when(delegationService.deleteDelegation(1, 0)).thenReturn(true);
        mvc.perform( MockMvcRequestBuilders
                .delete("/delegation/delete")
                .param("userId", "1")
                .param("delegationId", "0")
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string("true"))
                .andDo(MockMvcResultHandlers.print());

    }
    @Test
    public void changeDelegationTest() throws Exception
    {
        Delegation delegation = new Delegation("opis", LocalDateTime.parse("2000-10-31T01:30:00.000"),LocalDateTime.parse("2010-12-31T01:30:00.000"),
                30L, 2,2,2,
                TransportType.CAR,90.0d, AutoCapacity.EQUAL_GREATER,80,22d,22d,
                22d,22d);
        when(delegationService.changeDelegation(anyLong(), any(Delegation.class))).thenReturn(delegation);
        mvc.perform( MockMvcRequestBuilders
                .put("/delegation/change/id={delegationId}",0)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("description", "opis")
                .param("dateTimeStart", "2000-10-31T01:30:00.000")
                .param("dateTimeStop", "2010-12-31T01:30:00.000")
                .param("travelDietAmount", "30")
                .param("breakfastNumber", "2")
                .param("dinnerNumber", "2")
                .param("supperNumber", "2")
                .param("transportType", "CAR")
                .param("ticketPrice", "90")
                .param("autoCapacity", "EQUAL_GREATER")
                .param("km", "80")
                .param("accomodationPrice", "22")
                .param("otherTicketsPrice", "22")
                .param("otherOutlayDesc", "22")
                .param("otherOutlayPrice", "22")
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.delegationId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.delegationId").value(0L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dateTimeStart").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.dateTimeStart").value(LocalDateTime.parse("2000-10-31T01:30:00.000").format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:SS"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dateTimeStop").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.dateTimeStop").value(LocalDateTime.parse("2010-12-31T01:30:00.000").format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:SS"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.travelDietAmount").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.travelDietAmount").value(30L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakfastNumber").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakfastNumber").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dinnerNumber").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.dinnerNumber").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.supperNumber").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.supperNumber").value(2))
                .andDo(MockMvcResultHandlers.print());

    }
    @Test
    public void getAllDelegationTest() throws Exception
    {
        Delegation delegation = new Delegation("opis", LocalDateTime.parse("2000-10-31T01:30:00.000"),LocalDateTime.parse("2010-12-31T01:30:00.000"),
                30L, 0,0,0,
                TransportType.CAR,90.0d, AutoCapacity.EQUAL_GREATER,80,22d,22d,
                22d,22d);
        Delegation delegation2 = new Delegation("opisek", LocalDateTime.parse("2010-05-31T01:30:00.000"),LocalDateTime.parse("2012-12-31T01:30:00.000"),
                30L, 3,2,3,
                TransportType.CAR,90.0d, AutoCapacity.EQUAL_GREATER,80,22d,22d,
                22d,22d);
        when(delegationService.getAllDelegations()).thenReturn(Arrays.asList(delegation, delegation2));
        mvc.perform( MockMvcRequestBuilders
                .get("/delegation/getAll")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("opis"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].ticketPrice").value(90L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].dinnerNumber").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].supperNumber").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("opisek"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].ticketPrice").value(90L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].dinnerNumber").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].supperNumber").value(3))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.log())
                .andDo(MockMvcResultHandlers.print());
    }
    @Test
    public void getAllDelegationsOrderByDateStartDescTest() throws Exception
    {
        Delegation delegation = new Delegation("opis", LocalDateTime.parse("2000-10-31T01:30:00.000"),LocalDateTime.parse("2010-12-31T01:30:00.000"),
                30L, 0,0,0,
                TransportType.CAR,90.0d, AutoCapacity.EQUAL_GREATER,80,22d,22d,
                22d,22d);
        Delegation delegation2 = new Delegation("opisek", LocalDateTime.parse("2010-05-31T01:30:00.000"),LocalDateTime.parse("2012-12-31T01:30:00.000"),
                30L, 3,2,3,
                TransportType.CAR,90.0d, AutoCapacity.EQUAL_GREATER,80,22d,22d,
                22d,22d);
        when(delegationService.getAllDelegationsOrderByDateStartDesc()).thenReturn(Arrays.asList(delegation2, delegation));
        mvc.perform( MockMvcRequestBuilders
                .get("/delegation/sortDesc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("opis"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].ticketPrice").value(90L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].dinnerNumber").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].supperNumber").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("opisek"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].ticketPrice").value(90L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].dinnerNumber").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].supperNumber").value(3))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.log())
                .andDo(MockMvcResultHandlers.print());
    }
    @Test
    public void getAllDelByUserOrderByDateStartDescTest() throws Exception
    {
        Delegation delegation = new Delegation("opis", LocalDateTime.parse("2000-10-31T01:30:00.000"),LocalDateTime.parse("2010-12-31T01:30:00.000"),
                30L, 0,0,0,
                TransportType.CAR,90.0d, AutoCapacity.EQUAL_GREATER,80,22d,22d,
                22d,22d);
        Delegation delegation2 = new Delegation("opisek", LocalDateTime.parse("2010-05-31T01:30:00.000"),LocalDateTime.parse("2012-12-31T01:30:00.000"),
                30L, 3,2,3,
                TransportType.CAR,90.0d, AutoCapacity.EQUAL_GREATER,80,22d,22d,
                22d,22d);
        when(delegationService.getAllDelByUserOrderByDateStartDesc(1)).thenReturn(Arrays.asList(delegation2, delegation));
        mvc.perform( MockMvcRequestBuilders
                .get("/delegation/userSortDesc")
                .param("userId","1")
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("opis"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].ticketPrice").value(90L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].dinnerNumber").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].supperNumber").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("opisek"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].ticketPrice").value(90L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].dinnerNumber").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].supperNumber").value(3))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.log())
                .andDo(MockMvcResultHandlers.print());
    }
    @Test
    public void getAllUsersByRoleNameTest() throws Exception
    {
        User user1 = new User("Kompania","Adres","123","Imie","Nazwisko","Email","Haslo");
        User user2 = new User("Kompania1","Adres1","1231","Imie1","Nazwisko1","Email1","Haslo1");

        when(userService.getAllUsersByRoleName("ROLE_USER")).thenReturn(Arrays.asList(user1, user2));
        mvc.perform( MockMvcRequestBuilders
                .get("/user/roleName")
                .param("roleName","ROLE_USER")
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].roles").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].roles").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].roles[0]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].roles[0].roleName").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].roles").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].roles").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].roles[0]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].roles[0].roleName").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.log())
                .andDo(MockMvcResultHandlers.print());
    }
}
