package com.ychat.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.common.exception.BadRequestException;
import com.ychat.common.exception.BizIllegalException;
import com.ychat.common.exception.ForbiddenException;
import com.ychat.common.utils.UserContext;
import com.ychat.user.config.JwtProperties;
import com.ychat.user.domain.dto.LoginFormDTO;
import com.ychat.user.domain.dto.UserDTO;
import com.ychat.user.domain.po.User;
import com.ychat.user.domain.vo.UserFriendVo;
import com.ychat.user.domain.vo.UserLoginVO;
import com.ychat.user.enums.UserStatus;
import com.ychat.user.mapper.UserMapper;
import com.ychat.user.service.IUserService;
import com.ychat.user.utils.JwtTool;
import com.ychat.user.utils.UserRedis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final PasswordEncoder passwordEncoder;

    private final JwtTool jwtTool;

    private final JwtProperties jwtProperties;

    private final UserMapper userMapper;

    private final UserRedis userRedis;

    /**
     * 登录校验
     * @param loginDTO
     * @return
     */
    @Override
    public UserLoginVO login(LoginFormDTO loginDTO) {
        // 1.数据校验
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        // 2.根据用户名或手机号查询
        //
//        //下面是对lambda的修改
//        QueryWrapper queryWrapper = new QueryWrapper();
//        queryWrapper.eq("username", username);
//        User user = getOne(queryWrapper);

        User user = userMapper.getUserByName(username);

        Assert.notNull(user, "用户名错误");
        // 3.校验是否禁用
        if (user.getStatus() == UserStatus.FROZEN) {
            throw new ForbiddenException("用户被冻结");
        }
        // 4.校验密码
        //这里的user是根据id从数据库查到的
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("用户名或密码错误");
        }
        // 5.生成TOKEN
        //todo 这里现在是根据id来加token的，但我希望根据username
        String token = jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());
        // 6.封装VO返回
        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setBalance(user.getBalance());
        vo.setImage(user.getImage());
        vo.setSex(user.getSex());
        vo.setToken(token);
        return vo;
    }

    /**
     * 扣减余额
     * @param pw
     * @param totalFee
     */
    @Override
    public void deductMoney(String pw, Integer totalFee) {
        log.info("开始扣款");
        // 1.校验密码
        User user = getById(UserContext.getUser());
        if(user == null || !passwordEncoder.matches(pw, user.getPassword())){
            // 密码错误
            throw new BizIllegalException("用户密码错误");
        }

        // 2.尝试扣款
        try {
            baseMapper.updateMoney(UserContext.getUser(), totalFee);
        } catch (Exception e) {
            throw new RuntimeException("扣款失败，可能是余额不足！", e);
        }
        log.info("扣款成功");
    }

    @Override
    public User fridendViewGetUser(String username) {
        User user=userMapper.getUserByName(username);
        UserFriendVo friendVo=new UserFriendVo();
        friendVo.setUserId(user.getId());
        friendVo.setUsername(user.getUsername());
        friendVo.setSex(user.getSex());
        friendVo.setImage(user.getImage());
        return user;
    }

    @Override
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    /**
     * 注册
     * @param userDTO
     * @return
     */
    @Override
    public int newUser(UserDTO userDTO) {
//        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
//        queryWrapper.eq("username", userDTO.getUsername());
//        User user= userMapper.selectOne(queryWrapper);
        User user = userMapper.getUserByName(userDTO.getUsername());
        if(user != null){
            return 0; //该用户名已经存在
        }
        else {
            user=userMapper.getUserByPhone(userDTO.getPhone());
            if(user != null){
                return 2; //该手机号已经存在
            }
        }
        User user1=new User();
        BeanUtils.copyProperties(userDTO,user1);
        user1.setCreateTime(LocalDateTime.now());
        user1.setUpdateTime(LocalDateTime.now());
        user1.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userMapper.insertUser(user1);
        User user2=userMapper.getUserByName(user1.getUsername());
        //创建他的Time，用于管理他的最后登录时间
        userRedis.addTime(user2.getId().toString());
        return 1 ;//成功插入
    }

    /**
     * 检测该列表中的所有成员是否真实存在
     * @param userIds
     * @return
     */
    @Override
    public int existUsers(List<String> userIds) {
        for (String userId: userIds){
            User user=userMapper.getUserByName(userId);
            if(user == null){
                return 0;
            }
        }
        return 1;
    }

}
