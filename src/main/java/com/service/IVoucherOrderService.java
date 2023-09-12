package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dto.Result;
import com.entity.VoucherOrder;

public interface IVoucherOrderService extends IService<VoucherOrder> {
    Result addSeckillVoucherOrder(Long voucherId);

    void createVoucherOrder(VoucherOrder voucherOrder);
}
