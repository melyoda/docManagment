package com.mawkszuxz.docmanagment.config;

import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleCloudStorageConfig {

    @Bean
    public Storage googleCloudStorage() {
        // Retrieve the default HttpTransportOptions which already uses ADC for credentials
        HttpTransportOptions defaultTransportOptions = (HttpTransportOptions) StorageOptions.getDefaultInstance().getTransportOptions();

        HttpTransportOptions customTransportOptions = defaultTransportOptions.toBuilder()
                .setConnectTimeout(60000) // 60 seconds
                .setReadTimeout(300000)   // 5 minutes
                .build();

        // Build the Storage client, applying your custom transport options.
        // The credentials will be automatically picked up by ADC based on
        // GOOGLE_APPLICATION_CREDENTIALS environment variable (local)
        // or the VM's attached service account (GCP).
        return StorageOptions.newBuilder()
                .setTransportOptions(customTransportOptions)
                .build()
                .getService();
    }
}