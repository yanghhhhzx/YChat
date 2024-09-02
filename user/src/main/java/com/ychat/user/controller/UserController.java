package com.ychat.user.controller;

import com.ychat.user.domain.AccessTokenResponse;
import com.ychat.user.domain.dto.UserDTO;
import com.ychat.user.domain.po.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ychat.user.domain.dto.LoginFormDTO;
import com.ychat.user.domain.vo.UserLoginVO;
import com.ychat.user.service.IUserService;
import org.springframework.web.client.RestTemplate;
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

    //github相关
    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;

    //微信相关
    @Value("${wx.app.id}")
    private String appId;
    @Value("${wx.app.secret}")
    private String appSecret;

    @Autowired
    //利用名称来标记，指定我需要注入的bean为RestTemplateConfig中的那个bean，不然会注入普通的restTemplate
    @Qualifier("myRestTemplate")
    private RestTemplate restTemplate ;

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

    @GetMapping("/oauth/redirect")//填刚刚在登记时写的重定向url
    public UserLoginVO handleRedirect(@RequestParam("code") String code) {
        // 1.拿token
        // 1.1 利用认证信息，生成获取Token的Url，准备获取token
        String tokenUrl = "https://github.com/login/oauth/access_token" +
                "?client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&code=" + code;
        // 1.2使用restTemplate向GitHub发送请求，获取Token
        AccessTokenResponse tokenResponse = restTemplate.postForObject(tokenUrl, null, AccessTokenResponse.class);
        // 1.3从响应体拿到Token数据
        String accessToken = tokenResponse.getAccessToken();

        // 2.携带Token，再次向GitHub发送请求，获取用户信息
        // 2.1 生成URL
        String apiUrl = "https://api.github.com/user";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        // 2.2 发送请求
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
        // 2.3 拿到用户信息
        String body = response.getBody();
        System.out.println(body);
        JSONObject jsonObject=new JSONObject(body);

        //需要处理用户信息如：检查该用户是否已有账号，有账号则定位到那个账号，无账号这创建账号
        return userService.LoginWithGithub(jsonObject);
    }


    @GetMapping("/oauth/wx/redirect")//填刚刚在登记时写的重定向url
    public UserLoginVO wxRedirect(@RequestParam("code") String code) {
        // 1.拿token
        // 1.1 利用认证信息，生成获取Token的Url，准备获取token
        String tokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=" + appId +
                "secret=" + appSecret +
                "&code=" + code+"&grant_type=authorization_code";
        // 1.2使用restTemplate发送请求，获取Token
        AccessTokenResponse tokenResponse = restTemplate.postForObject(tokenUrl, null, AccessTokenResponse.class);
        // 1.3从响应体拿到Token数据
        String accessToken = tokenResponse.getAccessToken();

        // 2.携带Token，再次向微信发送请求，获取用户信息
        // 2.1 生成URL
        // 微信官方文档里是把access_token是直接放在url里的
        String apiUrl = "https://api.weixin.qq.com/sns/userinfo?"+
                "access_token=" + accessToken+
                "&openid=OPENID&lang=zh_CN";

        HttpEntity<String> entity = new HttpEntity<>("parameters", new HttpHeaders());
        // 2.2 发送请求
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
        // 2.3 拿到用户信息
        String body = response.getBody();
        System.out.println(body);
        JSONObject jsonObject=new JSONObject(body);

        //需要处理用户信息如：检查该用户是否已有账号，有账号则定位到那个账号，无账号这创建账号
        return userService.LoginWithWx(jsonObject);
    }



}

