package pl.pss.PSS.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pss.PSS.model.Delegation;
import pl.pss.PSS.model.User;
import pl.pss.PSS.repository.DelegationRepository;
import pl.pss.PSS.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DelegationRepository delegationRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    //dodanie uzytkownika do bazy
    public User addUser(User user)
    {
        return userRepository.save(user);
    }

    //zwroc wszystkich uzytkownikow
    public List<User> getAllUsers()
    {
        return userRepository.findAll();
    }

    //zmiana hasla
    public boolean changePassword(long userId, String newPassword)
    {
        boolean isChanged = false;
        Optional<User> userOpt = userRepository.findById(userId);
        if(userOpt.isPresent()){
            User userToChange = userOpt.get();
            userToChange.setPassword(bCryptPasswordEncoder.encode(newPassword));
            userRepository.save(userToChange);
            return true;
        }
        return false;
    }

    //usuwanie uzytkownika po id razem z zaleznosciami
    public boolean deleteUserById(long userId)
    {
        Optional<User> user = userRepository.findById(userId);
        boolean res = false;
        if(user.isPresent())
        {
            for(Delegation delegation : delegationRepository.findDelegationsByUser(userId)) {
                delegationRepository.delete(delegation);
            }

            res = true;
            userRepository.deleteById(userId);
        }
        return res;
    }

    public List<User> getAllUsersByRoleName(String roleName)
    {
        return userRepository.findAll().stream()
                .filter(x -> x.getRoles().stream().filter(y-> y.getRoleName().equals(roleName)).findFirst().isPresent())
                .collect(Collectors.toList());
    }
}
