package com.restApi.RestApi.Daos;

import com.restApi.RestApi.Entities.Transfer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransferDao extends CrudRepository<Transfer, Integer> {

    List<Transfer> getTransfersByToAccountNumberAccount(String numberAccount);

    List<Transfer> getTransfersByFromAccountNumberAccount(String numberAccount);

    List<Transfer> getTransfersByStatus(String status);
}
