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
    public ResponseEntity<Transfer> newTransfer(@RequestBody Transfer transferData) {
        transferService.createTransfer(transferData);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/transfers")
    public ResponseEntity<Iterable<Transfer>> getAllTransfers() {
        Iterable<Transfer> transfers = transferService.getAllTransfers();
        return new ResponseEntity<>(transfers, HttpStatus.OK);
    }

    @GetMapping("/transfers/from/{numberAccount}")
    public ResponseEntity<List<Transfer>> getTransfersByFromNumberAccount(@PathVariable String numberAccount) {
        List<Transfer> transfers = transferService.getTransfersByFromNumberAccount(numberAccount);
        return new ResponseEntity<>(transfers, HttpStatus.OK);

    }

    @GetMapping("/transfers/to/{numberAccount}")
    public ResponseEntity<List<Transfer>> getTransfersByToNumberAccount(@PathVariable String numberAccount) {
        List<Transfer> transfers = transferService.getTransfersByToNumberAccount(numberAccount);
        return new ResponseEntity<>(transfers, HttpStatus.OK);
    }

    @PostMapping("/transfers/cancel")
    public ResponseEntity cancelTransfer(@RequestBody Transfer transfer) {
        transferService.cancelTransfer(transfer);
        return new ResponseEntity(HttpStatus.OK);
    }
}
