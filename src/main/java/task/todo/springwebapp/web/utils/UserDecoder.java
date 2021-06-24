package task.todo.springwebapp.web.utils;

import task.todo.springwebapp.entities.UserEntity;
import task.todo.springwebapp.web.exceptions.BadRequestException;

import javax.naming.AuthenticationException;
import java.util.Base64;

public class UserDecoder {

    private final Base64.Decoder decoder;

    public UserDecoder() {
        this.decoder = Base64.getDecoder();
    }

    public UserEntity decodeUser(String header) throws AuthenticationException {
        if(header == null || header.isBlank()){
            throw new BadRequestException();
        }
        var splitHeader = header.split(":");

        if(splitHeader.length != 2){
            throw new BadRequestException();
        }

        return getUser(splitHeader[0], splitHeader[1]);
    }

    private UserEntity getUser(String usernameEncoded, String passwordEncoded) throws AuthenticationException {
        String username;
        String password;

        try{
            username = new String(decoder.decode(usernameEncoded));
        } catch(IllegalArgumentException exception){
            throw new AuthenticationException();
        }
        try{
            password = new String(decoder.decode(passwordEncoded));
        } catch(IllegalArgumentException exception){
            throw new AuthenticationException();
        }

        if(username.isBlank() || password.isBlank()){
            throw new AuthenticationException();
        }

        return new UserEntity(username, password);
    }
}
