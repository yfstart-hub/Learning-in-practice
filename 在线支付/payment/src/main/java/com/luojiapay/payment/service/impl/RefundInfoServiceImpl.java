package com.luojiapay.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luojiapay.payment.entity.OrderInfo;
import com.luojiapay.payment.entity.RefundInfo;
import com.luojiapay.payment.mapper.RefundInfoMapper;
import com.luojiapay.payment.service.OrderInfoService;
import com.luojiapay.payment.service.RefundInfoService;
import com.luojiapay.payment.util.OrderNoUtils;
import com.wechat.pay.java.service.refund.model.Refund;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {

    @Autowired
    OrderInfoService orderInfoService;

    @Override
    public RefundInfo createRefundByOrderNo(String orderNo, String reason) {
        OrderInfo orderInfo = orderInfoService.getOrderByOrderNo(orderNo);
        if (null == orderInfo)
            return null;

        // 根据订单号生成退款订单
        RefundInfo refundInfo = new RefundInfo();
        refundInfo.setOrderNo(orderNo);
        refundInfo.setRefundNo(OrderNoUtils.getRefundNo());// 退款单号
        refundInfo.setTotalFee(orderInfo.getTotalFee());// 原订单金额
        refundInfo.setRefund(orderInfo.getTotalFee());// 退款金额
        refundInfo.setReason(reason);// 退款原因

        // 保存退款订单
        baseMapper.insert(refundInfo);

        return refundInfo;
    }

    @Override
    public void updateRefund(Refund refund) {
        log.info("更新退款单信息");
        RefundInfo refundInfo = new RefundInfo();
        refundInfo.setRefundId(refund.getRefundId());// 微信支付退款单号
        refundInfo.setRefundStatus(refund.getStatus().name());// 退款状态
        refundInfo.setContentNotify(refund.toString());

        baseMapper.update(refundInfo, new QueryWrapper<RefundInfo>()
                .eq("refund_no", refund.getOutRefundNo()));
    }
}
