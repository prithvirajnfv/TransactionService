package com.revolut.txmgr.controller;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.revolut.txmgr.api.ResponseCode;
import com.revolut.txmgr.api.json.TransferRequestDTO;
import com.revolut.txmgr.api.json.TransferResponseDTO;
import com.revolut.txmgr.api.json.TransferStatusResponseDTO;
import com.revolut.txmgr.transfer.AccountStorage;
import com.revolut.txmgr.transfer.Currency;
import com.revolut.txmgr.transfer.TransferService;
import com.revolut.txmgr.transfer.TransferServiceImpl;

@Path("/txmgr")
public class TransactionController {
	
	private static final TransferService transferService;
	
	private static Map<Long, AccountStorage.Account> accounts=null;
	static{
		accounts = createDefaultAccounts();

        AccountStorage accountStorage = new AccountStorage(accounts);

        transferService = new TransferServiceImpl(accountStorage);
	}
	
		
	static Map<Long, AccountStorage.Account> createDefaultAccounts() {
        Map<Long, AccountStorage.Account> accounts = new ConcurrentHashMap<>();
        for (int i = 0; i < 18; i++) {
            long accountId = i + 1;
            AccountStorage.Account account = new AccountStorage.Account(accountId, Currency.USD, 0, 10000);
            accounts.put(accountId, account);
        }
        return accounts;
    }
	
	@GET
    @Path("api/status/{transactionId}")
	@Produces(MediaType.APPLICATION_JSON)
    public TransferStatusResponseDTO getTransferStatus(@PathParam("transactionId")String transactionId) {
		UUID transactionUUId = UUID.fromString(transactionId);
        ResponseCode responseCode = transferService.processTransferStatus(transactionUUId);

        return new TransferStatusResponseDTO()
                .setTransactionId(transactionId)
                .setResponseCode(responseCode);
    }
	
	@POST
	@Path("api/transfer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TransferResponseDTO createTransfer(TransferRequestDTO req) {
		ResponseCode responseCode = transferService.processTransfer(req);

        return new TransferResponseDTO()
                .setTransactionId(req.getTransactionId())
                .setResponseCode(responseCode);
	}
		
}
