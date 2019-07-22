package com.restApi.RestApi.Daos;

import com.restApi.RestApi.Entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class AccountDaoImpl implements AccountDao{

    private EntityManager entityManager;

    @Autowired
    public AccountDaoImpl(EntityManager entityManager) {
            this.entityManager = entityManager;
    }

    @Override
    public void addAccount(Account account) {
         entityManager.merge(account);
    }

    @Override
    public List<Account> getAllAccounts() {
        return entityManager.createQuery("FROM Account",Account.class)
                .getResultList();

    }

    @Override
    public Account getAccountByNumberOfAccount(String accountNum) {
        return entityManager.createQuery("FROM Account WHERE numberAccount =: accountNum", Account.class)
                .setParameter("accountNum",accountNum)
                .getSingleResult();
    }
}
