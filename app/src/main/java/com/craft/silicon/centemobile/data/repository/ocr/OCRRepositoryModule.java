package com.craft.silicon.centemobile.data.repository.ocr;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.ocr.OCRApiService;
import com.craft.silicon.centemobile.data.source.remote.ocr.OCRRemoteDataSource;

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
