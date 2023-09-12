package com.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entity.SeckillVoucher;
import com.mapper.SeckillVoucherMapper;
import com.service.ISeckillVoucherService;
import org.springframework.stereotype.Service;

/**
 * 秒杀优惠券表，与优惠券是一对一关系
 */
@Service
public class SeckillVoucherServiceImpl extends ServiceImpl<SeckillVoucherMapper, SeckillVoucher> implements ISeckillVoucherService {
}
