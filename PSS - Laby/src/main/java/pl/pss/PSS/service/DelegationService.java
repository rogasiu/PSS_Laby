package pl.pss.PSS.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.pss.PSS.model.Delegation;
import pl.pss.PSS.model.User;
import pl.pss.PSS.repository.DelegationRepository;
import pl.pss.PSS.repository.UserRepository;

import javax.transaction.Transactional;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DelegationService {
    @Autowired
    private DelegationRepository delegationRepository;
    @Autowired
    private UserRepository userRepository;

    public Delegation addDelegation(long userId, Delegation delegation){
        Optional<User> userOpt = userRepository.findById(userId);
        if(userOpt.isPresent())
        {
            Delegation dele = delegationRepository.save(delegation);
            dele.setDelegant(userOpt.get());
            return delegationRepository.save(dele);
        }
        return null;
    }
    public Delegation refreshDelegation(Delegation delegation){
        return delegationRepository.save(delegation);
    }
    @Transactional
    public boolean deleteDelegation(long userId, long delegationId)
    {
        Optional<User> userOpt = userRepository.findById(userId);

        if(userOpt.isPresent())
        {
            Optional<Delegation> delegation = userOpt.get().getDelegations()
                .stream()
                .filter(x-> x.getDelegationId()==delegationId)
                .findFirst();
            if(delegation.isPresent())
            {

                Delegation dele = delegation.get();
                System.out.println(dele.getDelegationId());
                delegationRepository.deleteById(dele.getDelegationId());
                return true;
            }
        }

        return false;
    }
    @Transactional
    public boolean deleteDelegation(long delegationId)
    {
        delegationRepository.deleteById(delegationId);
        return true;
    }
//    public boolean deleteDelegation(long userId, long delegationId){
//        Optional<User> userOpt = userRepository.findById(userId);
//        if(userOpt.isPresent()){
//            delegationRepository.findById(delegationId).ifPresent(dele -> {
//                delegationRepository.deleteById(delegationId);
//            });
//            return true;
//        }
//        return false;
//    }

    public Delegation changeDelegation(long delegationId, Delegation delegation)
    {
        Optional<Delegation> delegationInBase = delegationRepository.findById(delegationId);

        if(delegationInBase.isPresent())
        {
            delegation.setDelegationId(delegationInBase.get().getDelegationId());
            delegation.setDelegant(delegationInBase.get().getDelegant());

            return delegationRepository.save(delegation);
        }
        return null;
    }

    public List<Delegation> getAllDelegations()
    {
        return delegationRepository.findAll();
    }

    public List<Delegation> getAllDelegationsOrderByDateStartDesc()
    {
        return delegationRepository.findAll(Sort.by(Sort.Direction.DESC, "dateTimeStart"));
    }

    public List<Delegation> getAllDelByUserOrderByDateStartDesc(long userId)
    {
        Optional<User> user = userRepository.findById(userId);

        if(user.isPresent())
        {
            List<Delegation> delList = user.get().getDelegations().stream().distinct().collect(Collectors.toList());
            delList.sort((a,b) -> a.getDateTimeStart().compareTo(b.getDateTimeStart()));
            return delList;
        }

        return new ArrayList<>();
    }
}
