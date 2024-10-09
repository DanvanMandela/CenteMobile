package com.elmacentemobile.data.repository.splash;

import androidx.navigation.NavDirections;

import com.elmacentemobile.data.source.pref.StorageDataSource;
import com.elmacentemobile.view.navigation.NavigationDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SplashRepository implements SplashDataSource {

    private final NavigationDataSource navigationDataSource;
    private final StorageDataSource storageDataSource;


    @Inject
    public SplashRepository(NavigationDataSource navigationDataSource, StorageDataSource storageDataSource) {
        this.navigationDataSource = navigationDataSource;
        this.storageDataSource = storageDataSource;
    }

    @Override
    public NavDirections getNavigation() {
        if (storageDataSource.isActivated().getValue()) {
            return navigationDataSource.navigateAuth();
        } else return navigationDataSource.navigateToLogin();
    }
}
