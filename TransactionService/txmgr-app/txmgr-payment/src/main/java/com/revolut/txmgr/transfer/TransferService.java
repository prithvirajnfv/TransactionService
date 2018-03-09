package com.revolut.txmgr.transfer;

import com.revolut.txmgr.api.ResponseCode;
import com.revolut.txmgr.api.json.TransferRequestDTO;

import java.util.UUID;

public interface TransferService {
    ResponseCode processTransfer(TransferRequestDTO transferReqDto);

    ResponseCode processTransferStatus(UUID transactionId);
}
