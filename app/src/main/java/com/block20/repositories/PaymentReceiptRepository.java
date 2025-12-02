package com.block20.repositories;

import com.block20.models.PaymentReceipt;
import java.util.List;

public interface PaymentReceiptRepository {
    void save(PaymentReceipt receipt);
    List<PaymentReceipt> findByMemberId(String memberId);
}
