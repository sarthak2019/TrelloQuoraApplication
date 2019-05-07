package com.upgrad.quora.service.business;

import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {

	/*@Autowired
    private UserDao userDao;*/
    
    @Autowired
    private UserAdminBusinessService userAdminBusinessService;



    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) {
    	
    	userAdminBusinessService.getUser(userUuid, authorizationToken)
        return userAdminBusinessService.createUser(userEntity);
    }
}
