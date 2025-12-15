package com.rest;

import com.commons.mvc.WebResult;
import com.commons.data.DataPage;
import com.domain.User;
import com.service.IUserManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;
import java.util.Locale;

@RestController//@RestController注解相当于@Controller和@ResponseBody注解的组合，表示该类是一个控制器，并且所有的方法返回值都将直接写入HTTP响应体中，而不是返回视图名称。
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@RequestMapping("/com")
@Tag(name = "用户管理", description = "用户管理API")

public class UserController implements MessageSourceAware {
    private MessageSource messageSource;
    @Autowired
    @Qualifier(IUserManager.ID)
//用于解决使用@Autowired自动注入时容器中存在多个同类型Bean导致的歧义性问题。该注解通过指定Bean名称，将依赖注入策略从类型匹配（byType）转为名称匹配（byName），从而准确定位目标Bean。
    private IUserManager userManager;

    @PostMapping("/addUser")
    public WebResult<Void> addUser(@RequestBody User user) {//@RequestBody注解用于获取请求体中的数据
        System.out.println(user);
        this.userManager.addOrUpdateUser(user);
        return WebResult.createSuccessWebResult();

    }

    @GetMapping("/getUser")
    @Operation(summary = "获取用户", description = "按用户名获取用户", parameters = {
            @Parameter(name = "username", description = "用户名")
    })
    public WebResult<User> getUser(@RequestParam(value = "username") String username, Locale locale) {//@RequestParam注解用于获取查询参数：
//
        User user = this.userManager.getUserByName(username);
        if (user == null) {
            String error = this.messageSource.getMessage("com.domain.user.not-found", new Object[]{username}, locale);
            return WebResult.createFailureWebresult(error);
        }
        return WebResult.createSuccessWebResult(user);
    }

    @GetMapping("/getUserFtl")
    public ModelAndView getUserFtl(@RequestParam(value = "username") String username, Locale locale) {//@RequestParam注解用于获取查询参数：
//
        User user = this.userManager.getUserByName(username);
        ModelAndView modelAndView=new ModelAndView("system/userView");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("/findUser")//查询用户
    public WebResult<DataPage<User>> findUser(@RequestParam(value = "pageNumber", defaultValue = "1")
                                          int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        DataPage<User> users = this.userManager.findUser(pageNumber, pageSize);
        return WebResult.createSuccessWebResult(users);
    }

    @GetMapping("/findUserFtl")//查询用户
    public ModelAndView findUserFtl(@RequestParam(value = "pageNumber", defaultValue = "1")
                                               int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        DataPage<User> users=this.userManager.findUser(pageNumber, pageSize);
        ModelAndView modelAndView=new ModelAndView("system/userListView");
        modelAndView.addObject("users", users);
        return modelAndView;
    }

    @GetMapping("/istUser")
    public WebResult<Boolean> isUserNameExist(@RequestParam(value = "username") String username) {
        //System.out.println(username);
        boolean exist = this.userManager.isUserNameExist(username);
        return WebResult.createSuccessWebResult(exist);
    }

    @DeleteMapping("/delUser")
    public WebResult<Void> delUser(String[] usernames) {
        //System.out.println(username);
        this.userManager.DelectUser(usernames);
        return WebResult.createSuccessWebResult();
    }
    @GetMapping("/generate")
    public ModelAndView generateJava(@RequestParam(value = "packageName")String packageName,@RequestParam(value = "className")String className) {
        ModelAndView view = new ModelAndView("system/java-class");
        view.addObject("packageName", packageName);
        view.addObject("className", className);
        view.addObject("currentTime",new Date());
        return view;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        Locale locale = Locale.ENGLISH;
        this.messageSource = messageSource;
    }
}