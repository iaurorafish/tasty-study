package com.controller;

import com.dto.Result;
import com.service.IVoucherOrderService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {
    @Resource
    private IVoucherOrderService voucherOrderService;

    @PostMapping("/seckill/{id}")
    public Result addSeckillVoucherOrder(@PathVariable("id")Long voucherId) {
        return voucherOrderService.addSeckillVoucherOrder(voucherId);
    }
}
