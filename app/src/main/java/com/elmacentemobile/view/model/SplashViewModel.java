package com.elmacentemobile.view.model;

import androidx.lifecycle.ViewModel;
import androidx.navigation.NavDirections;

import com.elmacentemobile.data.repository.splash.SplashDataSource;
import com.elmacentemobile.data.repository.splash.SplashRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;


@HiltViewModel
public class SplashViewModel extends ViewModel implements SplashDataSource {
    private final SplashRepository repository;

    @Inject
    public SplashViewModel(SplashRepository repository) {
        this.repository = repository;
    }

    @Override
    public NavDirections getNavigation() {
        return repository.getNavigation();
    }
}
