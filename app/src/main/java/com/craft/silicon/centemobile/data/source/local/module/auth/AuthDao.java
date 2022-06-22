package com.craft.silicon.centemobile.data.source.local.module.auth;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.craft.silicon.centemobile.data.model.user.Accounts;
import com.craft.silicon.centemobile.data.model.user.Beneficiary;
import com.craft.silicon.centemobile.data.model.user.FrequentModules;
import com.craft.silicon.centemobile.data.repository.auth.AuthDataSource;

import java.util.List;

import io.reactivex.Observable;

@Dao
public interface AuthDao extends AuthDataSource {

    @Override
    @Insert(onConflict = REPLACE)
    void saveFrequentModule(List<FrequentModules> modules);

    @Override
    @Query("SELECT * FROM  frequent_tbl LIMIT 4")
    Observable<List<FrequentModules>> getFrequentModules();

    @Override
    @Insert(onConflict = REPLACE)
    void saveAccountModule(List<Accounts> modules);

    @Override
    @Query("SELECT * FROM  account_tbl ORDER BY bankAccountID")
    Observable<List<Accounts>> getAccount();

    @Override
    @Insert(onConflict = REPLACE)
    void saveBeneficiary(List<Beneficiary> modules);

    @Override
    @Query("SELECT * FROM  beneficiary_tbl ORDER BY accountAlias")
    Observable<List<Beneficiary>> geBeneficiary();
}
