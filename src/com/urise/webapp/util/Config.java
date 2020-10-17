package com.urise.webapp.util;

import com.urise.webapp.storage.SqlStorage;
import com.urise.webapp.storage.Storage;

import java.io.*;
import java.util.Properties;

public class Config {
    private static final Config INSTANCE = new Config();
    public static File PROPS;
    private final File storageDir;
    private final Storage storage;

    private Config() {
        PROPS = new File(".\\config\\resumes.properties");
        try(InputStream inputStream = new FileInputStream(PROPS)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            storageDir = new File(properties.getProperty("storage.dir"));
            storage = new SqlStorage(properties.getProperty("db.url"), properties.getProperty("db.user"), properties.getProperty("db.password"));
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid config file " + PROPS.getAbsolutePath());
        }
    }

    public Storage getStorage() {
        return storage;
    }

    public static Config getInstance() {
        return INSTANCE;
    }

    public File getStorageDir() {
        return storageDir;
    }
}