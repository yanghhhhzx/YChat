package com.ychat.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ychat.user.domain.dto.UserDTO;
import com.ychat.user.domain.po.User;
import com.ychat.user.domain.vo.UserLoginVO;
import com.ychat.user.domain.dto.LoginFormDTO;
import org.json.JSONObject;

import java.util.List;


public interface IUserService extends IService<User> {

    UserLoginVO login(LoginFormDTO loginFormDTO);

    /**
     * 扣减余额
     */
    void deductMoney(String pw, Integer totalFee);

    /**
     * 查看他人
     * @param username
     * @return
     */
    User fridendViewGetUser(String username);

    /**
     * 更新用户
     * @param user
     */
    void updateUser(User user);

    /**
     * 新增用户
     * @param userDTO
     * @return
     */
    int newUser(UserDTO userDTO);

    int existUsers(List<String> userIds);

    UserLoginVO LoginWithGithub(JSONObject jsonObject);
}
