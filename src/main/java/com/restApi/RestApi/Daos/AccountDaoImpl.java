package com.restApi.RestApi.Daos;

import com.restApi.RestApi.Entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class AccountDaoImpl implements AccountDao{

    private EntityManager entityManager;

    @Autowired
    public AccountDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void addAccount(Account account) {
        entityManager.persist(account);
    }

    @Override
    public List<Account> getAllAccounts() {
        try{
            return entityManager.createQuery("FROM accounts")
                    .getResultList();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Account getBalanceByNumberOfAccount(String accountNum) {
        try {
            return entityManager.createQuery("FROM accounts WHERE numberAccount =: accountNum", Account.class)
                    .getSingleResult();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
