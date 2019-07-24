package com.restApi.RestApi.Daos;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalTransferDao extends JpaRepository<com.restApi.RestApi.Entities.ExternalTransfer,Integer> {
}
