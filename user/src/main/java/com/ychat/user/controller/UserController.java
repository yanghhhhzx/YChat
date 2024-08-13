package com.ychat.user.controller;

import com.ychat.user.domain.dto.UserDTO;
import com.ychat.user.domain.po.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ychat.user.domain.dto.LoginFormDTO;
import com.ychat.user.domain.vo.UserLoginVO;
import com.ychat.user.service.IUserService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Api(tags = "用户相关接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final IUserService userService;

    @ApiOperation("用户登录接口")
    @PostMapping(value = "login")
    public UserLoginVO login(@RequestBody @Validated LoginFormDTO loginFormDTO){
        return userService.login(loginFormDTO);
    }

    /**
     * 扣减余额
     * @param pw
     * @param amount
     */
    @ApiOperation("扣减余额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pw", value = "支付密码"),
            @ApiImplicitParam(name = "amount", value = "支付金额")
    })
    @PutMapping("/money/deduct")
    public void deductMoney(@RequestParam("pw") String pw,@RequestParam("amount") Integer amount){
        userService.deductMoney(pw, amount);
    }

    /**
     * 查看其他用户信息
     * @param useName
     * @return
     */
    @ApiOperation("查看他人信息")
    @GetMapping("/{useName}")
    public User getOtherUser(@PathVariable String useName){
        return userService.fridendViewGetUser(useName);
    }

    @ApiOperation("更新个人信息")
    @PutMapping("/update")
    public void updateUser(@RequestBody User user){
        userService.updateUser(user);
    }

    @ApiOperation("新增用户")
    @PostMapping
    public int newUser(@RequestBody UserDTO userDTO){
        int result=userService.newUser(userDTO);
        return result;
    }

    /**
     * 用于上传头像
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation("上传头像")
    @PostMapping("/upload")
    public String upload(MultipartFile file) throws IOException {

        log.info("文件上传");
        String filename = file.getOriginalFilename();
        String fileExName = null;
        if (filename != null) {
            fileExName = filename.substring(filename.lastIndexOf("."));
        }
        filename = UUID.randomUUID() + fileExName;
        log.info("新文件名");

        String filePath = "C:\\Users\\Administrator\\Desktop\\javajava\\ychat\\image\\"+filename;
        file.transferTo(new File(filePath));
        return filePath;
    }

    @ApiOperation("检测是否存在该成员")
    @GetMapping("/existUsers")
    public int existUsers(@RequestBody List<String> userIds){
        return userService.existUsers(userIds);
    }

}

