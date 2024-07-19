package com.ychat.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ychat.user.domain.po.Address;
import com.ychat.user.mapper.AddressMapper;
import org.springframework.stereotype.Service;
import com.ychat.user.service.IAddressService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

}
