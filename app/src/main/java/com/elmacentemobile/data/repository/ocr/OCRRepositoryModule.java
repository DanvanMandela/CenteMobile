package com.elmacentemobile.data.repository.ocr;

import com.elmacentemobile.data.scope.Remote;
import com.elmacentemobile.data.source.remote.ocr.OCRApiService;
import com.elmacentemobile.data.source.remote.ocr.OCRRemoteDataSource;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class OCRRepositoryModule {

    @Remote
    @Provides
    public OCRDataSource provideRemote(OCRApiService ocrApiService) {
        return new OCRRemoteDataSource(ocrApiService);
    }
}
