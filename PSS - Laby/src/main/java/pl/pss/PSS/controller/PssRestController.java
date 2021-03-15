package pl.pss.PSS.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.server.RSocketServer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import pl.pss.PSS.model.Delegation;
import pl.pss.PSS.model.User;
import pl.pss.PSS.model.enums.AutoCapacity;
import pl.pss.PSS.model.enums.TransportType;
import pl.pss.PSS.service.DelegationService;
import pl.pss.PSS.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class PssRestController {

    @Autowired
    DelegationService delegationService;

    @Autowired
    UserService userService;

    public PssRestController(){

    }

    @PostMapping("/user/add")
    public void registerUser(
            @RequestParam("companyName") String companyName,
            @RequestParam("companyAddress") String companyAddress,
            @RequestParam("companyName") String companyNip,
            @RequestParam("name") String name,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ) {
        userService.addUser(new User(companyName,companyAddress,companyNip,name,lastName,email,password));
    }

    @GetMapping("/user/get")
    List<User> getAllUsers()
    {
        return userService.getAllUsers();
    }

    @PutMapping("/user/changePassword/id={userId}")
    void changePassword(
            @PathVariable("userId") long userId,
            @RequestParam("newPassword") String newPassword
    )
    {
        userService.changePassword(userId,newPassword);
    }

    @DeleteMapping("/user/delete")
    boolean deleteUserById(
            @RequestParam("userId") long userId
    )
    {
        return userService.deleteUserById(userId);
    }

    @PostMapping("/delegation/add/id={userId}")
    Delegation addDelegation(
            @PathVariable("userId") long userId,
            @RequestParam("description") String description,
            @RequestParam("dateTimeStart") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeStart,
            @RequestParam("dateTimeStop") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeStop,
            @RequestParam("transportType")TransportType transportType,
            @RequestParam("ticketPrice") double ticketPrice,
            @RequestParam("autoCapacity")AutoCapacity autoCapacity,
            @RequestParam("km") int km,
            @RequestParam("accomodationPrice") double accomodationPrice,
            @RequestParam("otherTicketsPrice") double otherTicketsPrice,
            @RequestParam("otherOutlayDesc") double otherOutlayDesc,
            @RequestParam("otherOutlayPrice") double otherOutlayPrice
            )
    {
        return delegationService.addDelegation(userId, new Delegation(description,dateTimeStart,dateTimeStop,
                transportType,ticketPrice,autoCapacity,km,accomodationPrice,otherTicketsPrice,
                otherOutlayDesc,otherOutlayPrice));
    }
    @DeleteMapping("/delegation/delete")
    boolean removeDelegation(
            @RequestParam("userId") long userId,
            @RequestParam("delegationId") long delegationId
            )
    {
        return delegationService.deleteDelegation(userId,delegationId);
    }
    @PutMapping("/delegation/change/id={delegationId}")
    void changeDelegation(
            @PathVariable("delegationId") long delegationId,
            @RequestParam("description") String description,
            @RequestParam("dateTimeStart (RRRR-MM-DDTHH:MM:SS.000)") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeStart,
            @RequestParam("dateTimeStop (RRRR-MM-DDTHH:MM:SS.000)") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeStop,
            @RequestParam("transportType")TransportType transportType,
            @RequestParam("ticketPrice") double ticketPrice,
            @RequestParam("autoCapacity")AutoCapacity autoCapacity,
            @RequestParam("km") int km,
            @RequestParam("accomodationPrice") double accomodationPrice,
            @RequestParam("otherTicketsPrice") double otherTicketsPrice,
            @RequestParam("otherOutlayDesc") double otherOutlayDesc,
            @RequestParam("otherOutlayPrice") double otherOutlayPrice
    )
    {
        delegationService.changeDelegation(delegationId, new Delegation(description,dateTimeStart,dateTimeStop,
                transportType,ticketPrice,autoCapacity,km,accomodationPrice,otherTicketsPrice,
                otherOutlayDesc,otherOutlayPrice));
    }
    @GetMapping("/delegation/getAll")
    List<Delegation> getAllDelegations()
    {
        return delegationService.getAllDelegations();
    }
    @GetMapping("/delegation/sortDesc")
    List<Delegation> getAllDelegationsOrderByDateStartDesc()
    {
        return delegationService.getAllDelegationsOrderByDateStartDesc();
    }
    @GetMapping("/delegation/userSortDesc")
    List<Delegation> getAllDelByUserOrderByDateStartDesc(long userId)
    {
        return delegationService.getAllDelByUserOrderByDateStartDesc(userId);
    }
    @GetMapping("/user/roleName")
    List<User> getAllUsersByRoleName(
            @RequestParam("roleName") String roleName
    )
    {
        return userService.getAllUsersByRoleName(roleName);
    }

}
