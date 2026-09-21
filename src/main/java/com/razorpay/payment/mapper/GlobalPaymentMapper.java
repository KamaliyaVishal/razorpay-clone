package com.razorpay.payment.mapper;

import com.razorpay.payment.dto.response.OrderResponse;
import com.razorpay.payment.dto.response.PaymentResponse;
import com.razorpay.payment.entity.OrderRecord;
import com.razorpay.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GlobalPaymentMapper {

    OrderResponse toOrderResponse(OrderRecord orderRecord);

    @Mapping(target = "orderId", source = "orderRecord.id")
    PaymentResponse toPaymentResponse(Payment payment);

    List<PaymentResponse> toPaymentResponseList(List<Payment> payments);

}
