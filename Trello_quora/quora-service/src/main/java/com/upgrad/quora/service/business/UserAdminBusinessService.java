package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserDao;
//import com.upgrad.quora.service.entity.RoleEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.ResourceNotFoundException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    public UserEntity getUser(final String userUuid, final String authorizationToken) throws ResourceNotFoundException, UnauthorizedException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        RoleEntity role = userAuthTokenEntity.getUser().getRole();

        if(role != null && role.getUuid() == 101){
            UserEntity userEntity = userDao.getUser(userUuid);
            if(userEntity == null){
                throw new ResourceNotFoundException("USR-001", "Try any other Username, this Username has already been taken");
            }
            return userEntity;
        }
        throw new UnauthorizedException("ATH-002", "You are not authorized to fetch user details");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity createUser(final UserEntity userEntity) throws SignUpRestrictedException,Exception{
        String userName= userEntity.getUsername();
        String emailId= userEntity.getEmail();
        String uuid= userEntity.getUuid();
        if(userDao.getUser(uuid).toString().equals(userName)){
        	throw new SignUpRestrictedException("USR-001", "Try any other Username, this Username has already been taken");
        } else if(userDao.getEmail(uuid).toString().equals(emailId)) {
        	throw new SignUpRestrictedException("USR-002", "This user has already been registered, try with any other emailId");
        } 
    	String password = userEntity.getPassword();
        String role=userEntity.getRole();
        if(role==null) {
        	role="nonadmin";
        }
        
        String[] encryptedText = cryptographyProvider.encrypt(password);
        userEntity.setRole(role);
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        

        return userDao.createUser(userEntity);

    }

}
