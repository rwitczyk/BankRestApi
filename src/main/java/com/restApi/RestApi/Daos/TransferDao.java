package com.restApi.RestApi.Daos;

import com.restApi.RestApi.Entities.Transfer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransferDao extends CrudRepository<Transfer,Integer> {
    List<Transfer> getTransfersByFromNumberAccount(String numberAccount);
}
