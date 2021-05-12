package pl.pss.PSS.controller;

import com.lowagie.text.DocumentException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import pl.pss.PSS.mailer.EmailSender;
import pl.pss.PSS.model.Delegation;
import pl.pss.PSS.model.Role;
import pl.pss.PSS.model.User;
import pl.pss.PSS.model.enums.AutoCapacity;
import pl.pss.PSS.service.DelegationService;
import pl.pss.PSS.service.UserService;
import pl.pss.PSS.tools.DelegationPDFCreator;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class WebController {

    @Autowired
    UserService userService;
    @Autowired
    DelegationService delegationService;

    @Autowired
    EmailSender emailSender;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private Environment env;

    @Autowired
    private ApplicationContext context;

    void addUserToModel(Model model)
    {

    }
    //-----------------
    @GetMapping("/")
    public String homePage(){
        return "homePage";
    }
    @GetMapping("/login")
    public String loginPage(Model model, Authentication auth){
        model.addAttribute("user", new User());
        model.addAttribute("isAuth", auth);
        List<ClientRegistration> oauth2 = new ArrayList<>();
        try {

            ClientRegistrationRepository repository =
                    context.getBean(ClientRegistrationRepository.class);

            if (repository != null) {
                ResolvableType type =
                        ResolvableType.forInstance(repository)
                                .as(Iterable.class);

                if (type != ResolvableType.NONE
                        && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
                    ((Iterable<?>) repository)
                            .forEach(t -> oauth2.add((ClientRegistration) t));
                }
            }
        } catch (Exception exception) {
        }

        model.addAttribute("oauth2", oauth2);
        return "loginPage";
    }
    @GetMapping("/registration")
    public String registrationPage(Model model){
        model.addAttribute("user", new User());
        return "registrationPage";
    }
    @PostMapping("/registration")
    public String registration(@ModelAttribute @Valid User user,BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "registrationPage";
        }

        user.setStatus(true);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActivateCode(RandomStringUtils.random(10, true, true));
        User inBaseUser = userService.addUser(user);



        String link = "https://pss2021.herokuapp.com/activation/userHash="+DigestUtils.sha256Hex(Long.toString(inBaseUser.getUserId()))+"/activationHash="+DigestUtils.sha256Hex(inBaseUser.getActivateCode());
        emailSender.sendEmail(inBaseUser.getEmail(), "Aktywacja konta",
                "Cześć! <br> "
                        + "Aktywuj konto linkiem <a href=\""+link+"\"></a><br>" +
                        "Jeśli link nie działa, wpisz ręcznie: "+link);
        return "redirect:/login";
    }
    @GetMapping("/activation/userHash={userHash}/activationHash={activationHash}")
    public String activationPage(Model model, @PathVariable String userHash, @PathVariable String activationHash){
        //optional ktory ma obiekt wyłącznie gdy jest uzytkownik ktorego hasz z id i hasz z kodu aktywacyjnego równają się temu co wpisane w sciezce
        Optional<User> u = userService.getAllUsers().stream()
                .filter(x->DigestUtils.sha256Hex(Long.toString(x.getUserId())).contentEquals(userHash))//szuka usera
                .filter(x->DigestUtils.sha256Hex(x.getActivateCode()).contentEquals(activationHash))//szuka po aktywacyjnych
                .filter(x-> !x.isActive())// sprawdza czy jest juz aktywny
                .findFirst();
        if(u.isPresent())
        {
            u.get().setActive(true);
            userService.addUser(u.get());
            model.addAttribute("activated",true);
        }else{
            model.addAttribute("activated",false);
        }

        return "activationPage";
    }
    //----------------
    @GetMapping("/userDetails")
    public String userDetails(Model model, Authentication auth){
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        Optional<User> user = userService.getAllUsers().stream().filter(x-> x.getEmail().equals(userDetails.getUsername())).findFirst();
        if(user.isPresent())
        {
            model.addAttribute("user", user.get());
        }
        return "userDetails";
    }
    //----------------------
    @GetMapping("/editUser")
    public String editUser(Model model, Authentication auth){
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<User> user = userService.getAllUsers().stream().filter(x-> x.getEmail().equals(userDetails.getUsername())).findFirst();
        if(user.isPresent())
        {
            model.addAttribute("user", user.get());
        }
        return "editUser";
    }
    @PostMapping("/editUser")
    public String editUserPost(@ModelAttribute User user, Authentication auth){
        user.setPassword("default");
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<User> userOpt = userService.getAllUsers().stream().filter(x-> x.getEmail().equals(userDetails.getUsername())).findFirst();
        if(userOpt.isPresent())
        {
            userOpt.get().setCompanyAddress(user.getCompanyAddress());
            userOpt.get().setCompanyName(user.getCompanyName());
            userOpt.get().setCompanyNip(user.getCompanyNip());
            userOpt.get().setName(user.getName());
            userOpt.get().setLastName(user.getLastName());
            userService.addUser(userOpt.get());
        }

        return "redirect:/userDetails";
    }

    @GetMapping("/delegationList")
    public String delegationList(Model model, Authentication auth){
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<User> userOpt = userService.getAllUsers().stream().filter(x-> x.getEmail().equals(userDetails.getUsername())).findFirst();
        if(userOpt.isPresent()) {
            model.addAttribute("delegations", delegationService.getAllDelByUserOrderByDateStartDesc(userOpt.get().getUserId()));
            model.addAttribute("nowTime", LocalDateTime.now());
        }

        return "delegationList";
    }
    @GetMapping("/delegationPDF/delegationId={delegationId}")
    public void exportToPDF(HttpServletResponse response, @PathVariable Long delegationId) throws DocumentException, IOException {
        response.setContentType("application/pdf");

        Optional<Delegation> delegation = delegationService.getAllDelegations().stream()
                .filter(x->x.getDelegationId()==delegationId)
                .findFirst();
        if(delegation.isPresent()) {
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=delegation_" + delegation.get().getDelegationId() + ".pdf";
            response.setHeader(headerKey, headerValue);

            DelegationPDFCreator exporter = new DelegationPDFCreator(delegation.get());
            exporter.export(response);
        }



    }
    @GetMapping("/delegationPrint/delegationId={delegationId}")
    public void delegationPrint(HttpServletResponse response, @PathVariable Long delegationId) throws DocumentException, IOException {
        response.setContentType("application/pdf");

        Optional<Delegation> delegation = delegationService.getAllDelegations().stream()
                .filter(x->x.getDelegationId()==delegationId)
                .findFirst();
        if(delegation.isPresent()) {
//            String headerKey = "Content-Disposition";
//            String headerValue = "attachment; filename=delegation_" + delegation.get().getDelegationId() + ".pdf";
//            response.setHeader(headerKey, headerValue);

            DelegationPDFCreator exporter = new DelegationPDFCreator(delegation.get());
            exporter.print(response);
        }



    }
    @GetMapping("/changePassword")
    public String changePassword(){
        return "changePassword";
    }
    @PostMapping("/changePassword")
    public String changePasswordPost(@RequestParam String oldpass, @RequestParam String newpass, @RequestParam String renewpass, Model model, Authentication auth){
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<User> userOpt = userService.getAllUsers().stream().filter(x-> x.getEmail().equals(userDetails.getUsername())).findFirst();
        if(userOpt.isPresent()) {
            System.out.println(oldpass);
            if(!bCryptPasswordEncoder.matches(oldpass, userOpt.get().getPassword()))
            {
                model.addAttribute("wrongOldPass", true);
                return "changePassword";
            }
            else if(!newpass.equals(renewpass))
            {
                model.addAttribute("wrongRetype", true);
                return "changePassword";
            }else {
                model.addAttribute("wrongOldPass", false);
                model.addAttribute("wrongRetype", false);
                userService.changePassword(userOpt.get().getUserId(), newpass);
                return "redirect:/userDetails";
            }
        }
        return "changePassword";
    }
    @GetMapping("/addDelegation")
    public String addDelegation(Model model){
        model.addAttribute("delegation", new Delegation());
        return "addDelegationPage";
    }
    //sdsd
    @PostMapping("/addDelegation")
    public String addDelegationPost(@ModelAttribute @Valid Delegation delegation,BindingResult bindingResult, Authentication auth){
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<User> userOpt = userService.getAllUsers().stream().filter(x-> x.getEmail().equals(userDetails.getUsername())).findFirst();

        if (bindingResult.hasErrors()) {
            return "addDelegationPage";
        }

        if(userOpt.isPresent()){
            Long userId = userOpt.get().getUserId();
            delegationService.addDelegation(userId, delegation);
        }
        return "redirect:/delegationList";
    }
    @GetMapping("/editDelegation/delegationId={delegationId}")
    public String editDelegation(@PathVariable Long delegationId, Model model, Authentication auth){
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<User> userOpt = userService.getAllUsers().stream().filter(x-> x.getEmail().equals(userDetails.getUsername())).findFirst();

        Optional<Delegation> delegation = delegationService.getAllDelegations().stream()
                .filter(x->x.getDelegationId()==delegationId)
                .findFirst();
        if(delegation.isPresent())
        {
            if(delegation.get().getDateTimeStart().isAfter(LocalDateTime.now()))
            {
                model.addAttribute("delegation", delegation.get());
                if(
                        (delegation.get().isDelegationAccepted() || delegation.get().isDelegationPending())
                        ||
                        ((delegation.get().getDelegant().getUserId()==userOpt.get().getUserId()))
                )
                {
                    model.addAttribute("isEditable",false);
                }else{
                    model.addAttribute("isEditable",true);
                }
                return "editDelegationPage";
            }
        }


        return "redirect:/delegationList";
    }
    @PostMapping("/editDelegation")
    public String editDelegationPost(@ModelAttribute Delegation delegation, Authentication auth){
        System.out.println("Tu sie cos wykonuje");
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<User> userOpt = userService.getAllUsers().stream().filter(x-> x.getEmail().equals(userDetails.getUsername())).findFirst();
        if(userOpt.isPresent() && !delegation.isDelegationAccepted() && !delegation.isDelegationPending()){
            Long userId = userOpt.get().getUserId();
            delegationService.addDelegation(userId, delegation);
        }
        return "redirect:/delegationList";
    }
    @GetMapping("/deleteDelegation/delegationId={delegationId}")
    public String deleteDelegation(@PathVariable Long delegationId, Model model){

        Optional<Delegation> delegation = delegationService.getAllDelegations().stream()
                .filter(x->x.getDelegationId()==delegationId)
                .findFirst();
        if(delegation.isPresent())
        {
            if(delegation.get().getDateTimeStart().isAfter(LocalDateTime.now()))
            {
                model.addAttribute("delegationId", delegationId);
                model.addAttribute("delegationDesc", delegation.get().getDescription());
                return "deleteDelegationPage";
            }
        }


        return "redirect:/delegationList";
    }
    @PostMapping("/deleteDelegation")
    public String deleteDelegationPost(@RequestParam Long delegationId, Authentication auth){
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<User> userOpt = userService.getAllUsers().stream().filter(x-> x.getEmail().equals(userDetails.getUsername())).findFirst();
        if(userOpt.isPresent()){
            Long userId = userOpt.get().getUserId();
            delegationService.deleteDelegation(userId, delegationId);
        }
        return "redirect:/delegationList";
    }
    @GetMapping("/sendAcceptDelegation/delegationId={delegationId}")
    public String sendAcceptDelegation(@PathVariable Long delegationId){

        Optional<Delegation> delegation = delegationService.getAllDelegations().stream()
                .filter(x->x.getDelegationId()==delegationId)
                .findFirst();
        if(delegation.isPresent())
        {
            if(!delegation.get().isDelegationAccepted() && !delegation.get().isDelegationPending() && !delegation.get().isDelegationRequestCancel())
            {
                delegation.get().setDelegationPending(true);
                delegation.get().setDelegationAccepted(true);
                delegation.get().setDelegationRequestCancel(false);
                delegationService.refreshDelegation(delegation.get());

                emailSender.sendEmail(delegation.get().getDelegant().getEmail(), "Delegacja wysłana do akceptacji",
                        "Cześć! <br> "
                                + "Właśnie wysłałeś delegację "+delegation.get().getDescription()+" do akceptacji");
            }
        }


        return "redirect:/delegationList";
    }
    @GetMapping("/sendDeclineDelegation/delegationId={delegationId}")
    public String sendDeclineDelegation(@PathVariable Long delegationId, Model model){
        Optional<Delegation> delegation = delegationService.getAllDelegations().stream()
                .filter(x->x.getDelegationId()==delegationId)
                .findFirst();
        if(delegation.isPresent())
        {
            if(delegation.get().isDelegationAccepted() && !delegation.get().isDelegationPending() && !delegation.get().isDelegationRequestCancel())
            {
                delegation.get().setDelegationPending(false);
                delegation.get().setDelegationAccepted(true);
                delegation.get().setDelegationRequestCancel(true);
                delegationService.refreshDelegation(delegation.get());

                emailSender.sendEmail(delegation.get().getDelegant().getEmail(), "Prośba o wycofanie delegacji",
                        "Cześć! <br> "
                                + "Właśnie poprosiłeś o wycofanie akceptacji delegacji "+delegation.get().getDescription());
            }
        }


        return "redirect:/delegationList";
    }
//    @GetMapping("/")
//    public String userDetails(ModelMap model)
//    {
//        model.put("name","imie");
//        return "userDetails";
//    }

    //------ADMIN ENDPOINTS--------
    @GetMapping("/admin")
    String adminHomePage(Model model)
    {
        return "adminHomePage";
    }
    @GetMapping("/admin/showDelegationRequests")
    String showDelegationRequests(Model model)
    {
        model.addAttribute("delegations", delegationService.getAllDelegations()
                .stream().filter(x->x.isDelegationPending() || x.isDelegationRequestCancel())
                .collect(Collectors.toList()));
        return "adminDelegationRequestsList";
    }
    @GetMapping("/admin/delegationDetails/delegationId={delegationId}")
    public String delegationDetails(@PathVariable Long delegationId, Model model){

        Optional<Delegation> delegation = delegationService.getAllDelegations().stream()
                .filter(x->x.getDelegationId()==delegationId)
                .findFirst();
        if(delegation.isPresent())
        {
            model.addAttribute("delegation", delegation.get());
            return "adminDelegationDetailsPage";
        }


        return "redirect:/admin/showDelegationRequests";
    }

    @GetMapping("/admin/acceptDelegation/delegationId={delegationId}/status={status}")
    public String delegationDetails(@PathVariable Long delegationId,@PathVariable boolean status, Model model){

        Optional<Delegation> delegation = delegationService.getAllDelegations().stream()
                .filter(x->x.getDelegationId()==delegationId)
                .findFirst();
        if(delegation.isPresent())
        {
            if(delegation.get().isDelegationPending())
            {
                if(status)
                {
                    delegation.get().setDelegationPending(false);
                    delegation.get().setDelegationAccepted(true);
                    delegation.get().setDelegationRequestCancel(false);

                    emailSender.sendEmail(delegation.get().getDelegant().getEmail(), "Delegacja zaakceptowana",
                            "Cześć! <br> "
                                    + "Delegacja "+delegation.get().getDescription()+" została zaakceptowana :D");
                }else{
                    delegation.get().setDelegationPending(false);
                    delegation.get().setDelegationAccepted(false);
                    delegation.get().setDelegationRequestCancel(false);
                    emailSender.sendEmail(delegation.get().getDelegant().getEmail(), "Delegacja niezaakceptowana",
                            "Cześć! <br> "
                                    + "Delegacja "+delegation.get().getDescription()+" nie została zaakceptowana :C");
                }
                delegationService.refreshDelegation(delegation.get());
            }
        }
        return "redirect:/admin/showDelegationRequests";
    }
    @GetMapping("/admin/cancelDelegation/delegationId={delegationId}/status={status}")
    public String cancelDelegation(@PathVariable Long delegationId,@PathVariable boolean status, Model model){

        Optional<Delegation> delegation = delegationService.getAllDelegations().stream()
                .filter(x->x.getDelegationId()==delegationId)
                .findFirst();
        if(delegation.isPresent())
        {
            if(delegation.get().isDelegationRequestCancel())
            {
                if(status)
                {
                    delegation.get().setDelegationPending(false);
                    delegation.get().setDelegationAccepted(false);
                    delegation.get().setDelegationRequestCancel(false);
                    emailSender.sendEmail(delegation.get().getDelegant().getEmail(), "Delegacja wycofana",
                            "Cześć! <br> "
                                    + "Prośba wycofania akceptacji delegacji "+delegation.get().getDescription()+" została zaakceptowana :D");
                }else{
                    delegation.get().setDelegationPending(false);
                    delegation.get().setDelegationAccepted(true);
                    delegation.get().setDelegationRequestCancel(false);
                    emailSender.sendEmail(delegation.get().getDelegant().getEmail(), "Delegacja niewycofana",
                            "Cześć! <br> "
                                    + "Prośba wycofania akceptacji delegacji "+delegation.get().getDescription()+"nie została zaakceptowana :C");
                }
                delegationService.refreshDelegation(delegation.get());
            }
        }
        return "redirect:/admin/showDelegationRequests";
    }
    @GetMapping("/admin/userList")
    public String userList(Model model){
        model.addAttribute("users", userService.getAllUsers());
        return "adminUserList";
    }
    @GetMapping("/admin/delegationList/userId={userId}")
    public String delegationList(@PathVariable Long userId, Model model){
        Optional<User> userOpt = userService.getAllUsers().stream().filter(x-> x.getUserId()==userId).findFirst();
        if(userOpt.isPresent()) {
            model.addAttribute("namesurname", userOpt.get().getName()+" "+userOpt.get().getLastName());
            model.addAttribute("delegations", delegationService.getAllDelByUserOrderByDateStartDesc(userOpt.get().getUserId()));

            return "adminDelegationList";
        }else{
            return "redirect:/admin/delegationList";
        }
    }


    @GetMapping("/admin/editDelegation/delegationId={delegationId}")
    public String adminEditDelegation(@PathVariable Long delegationId, Model model){

        Optional<Delegation> delegation = delegationService.getAllDelegations().stream()
                .filter(x->x.getDelegationId()==delegationId)
                .findFirst();
        if(delegation.isPresent())
        {
            model.addAttribute("delegation", delegation.get());
//            model.addAttribute("delegationOwner", delegation.get().getDelegant().getUserId());
            return "adminEditDelegationPage";
        }

        return "redirect:/admin/userList";
    }
    @PostMapping("/admin/editDelegation")
    public String adminEditDelegationPost(@RequestParam String acceptationStatus, @ModelAttribute Delegation delegation){
        Optional<Delegation> del = delegationService.getAllDelegations().stream()
                .filter(x->x.getDelegationId()==delegation.getDelegationId())
                .findFirst();
        if(del.isPresent())
        {
            if(acceptationStatus.equals("ACCEPTED"))
            {
                delegation.setDelegationAccepted(true);
                delegation.setDelegationPending(false);
                delegation.setDelegationRequestCancel(false);
                emailSender.sendEmail(delegation.getDelegant().getEmail(), "Delegacja zaakceptowana ręcznie",
                        "Cześć! <br> "
                                + "Administrator zmienił status delegacji "+delegation.getDescription()+" na zaakceptowaną ręcznie.");
            }else if(acceptationStatus.equals("NOT ACCEPTED"))
            {
                delegation.setDelegationAccepted(false);
                delegation.setDelegationPending(false);
                delegation.setDelegationRequestCancel(false);
                emailSender.sendEmail(delegation.getDelegant().getEmail(), "Delegacja odrzucona ręcznie",
                        "Cześć! <br> "
                                + "Administrator zmienił status delegacji "+delegation.getDescription()+" na odrzuconą ręcznie.");
            }
            delegation.setDelegant(del.get().getDelegant());
            delegationService.refreshDelegation(delegation);
        }

        return "redirect:/admin/userList";
    }


    @GetMapping("/admin/deleteDelegation/delegationId={delegationId}")
    public String adminDeleteDelegation(@PathVariable Long delegationId){
        delegationService.deleteDelegation(delegationId);
        return "redirect:/admin/userList";
    }
    @GetMapping("/admin/deleteUser/userId={userId}")
    public String adminDeleteUser(@PathVariable Long userId){
        Optional<User> userOpt = userService.getAllUsers().stream().filter(x-> x.getUserId()==userId).findFirst();
        if(userOpt.isPresent()) {
            userOpt.get().getDelegations()
                    .stream()
                    .forEach(x->{
                        x.setDelegant(null);
                        delegationService.refreshDelegation(x);
                    });
            userOpt.get().getDelegations().clear();

            userService.addUser(userOpt.get());

            userService.deleteUserById(userId);
        }
        return "redirect:/admin/userList";
    }
    @GetMapping("/admin/makeAdmin/userId={userId}")
    public String adminMakeAdmin(@PathVariable Long userId){
        Optional<User> userOpt = userService.getAllUsers().stream().filter(x-> x.getUserId()==userId).findFirst();
        if(userOpt.isPresent()) {
            userOpt.get().getRoles().add(new Role("ROLE_ADMIN"));

            userService.addUser(userOpt.get());
        }
        return "redirect:/admin/userList";
    }
}
