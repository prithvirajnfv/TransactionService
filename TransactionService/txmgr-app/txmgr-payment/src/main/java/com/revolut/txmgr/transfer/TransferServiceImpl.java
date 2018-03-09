package com.revolut.txmgr.transfer;

import com.revolut.txmgr.api.ResponseCode;
import com.revolut.txmgr.api.json.TransferRequestDTO;

import java.util.Objects;
import java.util.UUID;

public class TransferServiceImpl implements TransferService {
    private final AccountStorage accountStorage;

    public TransferServiceImpl(AccountStorage accountStorage) {
        this.accountStorage = Objects.requireNonNull(accountStorage, "accountStorage");
    }

    @Override
    public ResponseCode processTransfer(TransferRequestDTO transferReqDto) {
        return accountStorage.processTransfer(transferReqDto);
    }

    @Override
    public ResponseCode processTransferStatus(UUID transactionId) {
        return accountStorage.getTransferStatus(transactionId);
    }
}
