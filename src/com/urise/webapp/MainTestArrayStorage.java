package com.urise.webapp;

import com.urise.webapp.model.Resume;
import com.urise.webapp.storage.PathStorage;
import com.urise.webapp.storage.Storage;
import com.urise.webapp.storage.serializer.XmlStreamSerializer;

import java.io.File;

/**
 * Test for your webapp.com.urise.storage.ArrayStorage implementation
 */
public class MainTestArrayStorage {
    public static final File STORAGE_DIR = new File("C:\\Users\\DNS\\IdeaProjects\\basejava\\storage");

    static final Storage ARRAY_STORAGE = new PathStorage(STORAGE_DIR.getAbsolutePath(), new XmlStreamSerializer());

    public static void main(String[] args) {
        Resume r1 = new Resume("uuid1", "dummy");
        Resume r2 = new Resume("uuid2", "dummy");
        Resume r3 = new Resume("uuid3", "dummy");

        ARRAY_STORAGE.save(r1);
        ARRAY_STORAGE.save(r2);
        ARRAY_STORAGE.save(r3);

        System.out.println(r1.equals(new Resume("uuid1", "dummy")));

        System.out.println("Get r1: " + ARRAY_STORAGE.get(r1.getUuid()));
        System.out.println("Size: " + ARRAY_STORAGE.size());

        //System.out.println("Get dummy: " + ARRAY_STORAGE.get("dummy"));

        printAll();
        ARRAY_STORAGE.delete(r1.getUuid());
        printAll();
        ARRAY_STORAGE.clear();
        printAll();

        System.out.println("Size: " + ARRAY_STORAGE.size());
    }

    static void printAll() {
        System.out.println("\nGet All");
        for (Resume r : ARRAY_STORAGE.getAllSorted()) {
            System.out.println(r);
        }
    }
}
