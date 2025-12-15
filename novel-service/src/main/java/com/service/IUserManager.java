package com.service;

import com.commons.data.DataPage;
import com.domain.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import java.util.List;
@Validated
public interface IUserManager {
    //public static final String Service_Type="medical.com.service.type";
    void addOrUpdateUser(@Valid User user);

    String ID="userManager";
    String SERVICE_TYPE="medical.com.service.type";

    User getUserByName(@NotBlank(message = "{com.domain.user.username.not-blank}")String username);

    boolean isUserNameExist(@NotBlank(message = "{com.domain.user.username.not-blank}")String username);

    void DelectUser(String[] usernames);

    DataPage<User> findUser(int pageNumber, int pageSize);
    DataPage<User> findUserFtl(int pageNumber, int pageSize);

    List<String> findRoleName(String username);
}
