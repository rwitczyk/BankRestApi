package com.restApi.RestApi.Endpoints;

import com.restApi.RestApi.Entities.Transfer;
import com.restApi.RestApi.Services.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class TransferEndpoint {

    private TransferService transferService;

    @Autowired
    public TransferEndpoint(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer/new")
    public ResponseEntity<Transfer> newTransfer(@RequestBody Transfer transferData)
    {
        boolean ifExecutedTransfer = transferService.saveNewTransfer(transferData);

        if(ifExecutedTransfer) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/transfers")
    public ResponseEntity<Iterable<Transfer>> getAllTransfers()
    {
        Iterable<Transfer> transfers = transferService.getAllTransfers();

        if(transfers != null) {
            return new ResponseEntity<>(transfers, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/transfers/from/{numberAccount}")
    public ResponseEntity<List<Transfer>> getTransfersByFromNumberAccount(@PathVariable String numberAccount)
    {
        List<Transfer> transfers = transferService.getTransfersByFromNumberAccount(numberAccount);

        if(transfers != null) {
            return new ResponseEntity<>(transfers, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/transfers/to/{numberAccount}")
    public ResponseEntity<List<Transfer>> getTransfersByToNumberAccount(@PathVariable String numberAccount)
    {
        List<Transfer> transfers = transferService.getTransfersByToNumberAccount(numberAccount);

        if(transfers != null) {
            return new ResponseEntity<>(transfers, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
