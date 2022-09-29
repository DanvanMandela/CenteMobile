package com.elmacentemobile.data.source.local.module.auth;

import com.elmacentemobile.data.model.user.Accounts;
import com.elmacentemobile.data.model.user.Beneficiary;
import com.elmacentemobile.data.model.user.FrequentModules;
import com.elmacentemobile.data.repository.auth.AuthDataSource;
import com.elmacentemobile.util.scheduler.BaseSchedulerProvider;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Singleton
public class AuthLocalDataSource implements AuthDataSource {

    private final AuthDao authDao;
    private final BaseSchedulerProvider schedulerProvider;


    @Inject
    public AuthLocalDataSource(AuthDao authDao, BaseSchedulerProvider schedulerProvider) {
        this.authDao = authDao;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public void saveFrequentModule(List<FrequentModules> modules) {
        Completable.fromRunnable(() -> authDao.saveFrequentModule(modules))
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui()).subscribe();
    }

    @Override
    public Observable<List<FrequentModules>> getFrequentModules() {
        return authDao.getFrequentModules();
    }

    @Override
    public void saveAccountModule(List<Accounts> modules) {
        Completable.fromRunnable(() -> authDao.saveAccountModule(modules))
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui()).subscribe();
    }

    @Override
    public Observable<List<Accounts>> getAccount() {
        return authDao.getAccount();
    }

    @Override
    public void saveBeneficiary(List<Beneficiary> modules) {
        Completable.fromRunnable(() -> authDao.saveBeneficiary(modules))
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui()).subscribe();
    }

    @Override
    public Observable<List<Beneficiary>> geBeneficiary() {
        return authDao.geBeneficiary();
    }
}
