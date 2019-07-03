package com.restApi.RestApi.Endpoints;

import com.restApi.RestApi.Entities.Transfer;
import com.restApi.RestApi.Services.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class TransferEndpoint {

    private TransferService transferService;

    @Autowired
    public TransferEndpoint(TransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping("/transfer/new")
    public ResponseEntity<Transfer> newTransfer(@RequestBody Transfer transferData)
    {
        Transfer transfer = transferService.saveNewTransfer(transferData);

        if(transfer != null) {
            return new ResponseEntity<>(transfer, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/transfers")
    public ResponseEntity<Iterable<Transfer>> getAllTransfers()
    {
        Iterable<Transfer> transfers = transferService.getAllTransfers();

        return new ResponseEntity<>(transfers,HttpStatus.OK);
    }

    @GetMapping("/transfers/{numberAccount}")
    public ResponseEntity<List<Transfer>> getTransfersByNumberAccount(@PathVariable String numberAccount)
    {
        List<Transfer> transfers = transferService.getTranfersByNumberAccount(numberAccount);

        return new ResponseEntity<>(transfers,HttpStatus.OK);
    }
}