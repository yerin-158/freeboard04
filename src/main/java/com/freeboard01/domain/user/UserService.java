package com.freeboard01.domain.user;

import com.freeboard01.api.user.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

@Service
@Transactional
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public  UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Boolean join(UserForm user) {
        UserEntity userEntity = userRepository.findByAccountId(user.getAccountId());
        if(userEntity == null){
            userRepository.save(user.convertUserEntity());
            return true;
        }
        return false;
    }

    public Boolean login(UserForm user, HttpSession httpSession) {
        UserEntity userEntity = userRepository.findByAccountId(user.getAccountId());
        if(userEntity.getPassword().equals(user.getPassword())){
            httpSession.setAttribute("USER", user);
            return true;
        }
        return false;
    }

    public void logout(HttpSession httpSession){
        httpSession.removeAttribute("USER");
    }
}
