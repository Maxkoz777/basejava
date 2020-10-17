package com.urise.webapp.storage;

import com.urise.webapp.exception.NotExistStorageException;
import com.urise.webapp.exception.StorageException;
import com.urise.webapp.model.*;
import com.urise.webapp.util.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbstractStorageTest {

    public static final File STORAGE_DIR = Config.getInstance().getStorageDir();

    protected Storage storage;

    private static final String UUID_1 = UUID.randomUUID().toString();
    private static final Resume R1 = new Resume(UUID_1, "new1");

    private static final String UUID_2 = UUID.randomUUID().toString();
    private static final Resume R2 = new Resume(UUID_2, "new2");

    private static final String UUID_3 = UUID.randomUUID().toString();
    private static final Resume R3 = new Resume(UUID_3, "new3");

    private static final String UUID_4 = UUID.randomUUID().toString();
    private static final Resume R4 = new Resume(UUID_4, "new4");

    static {
        R1.addContact(ContactType.MAIL, "mail1@ya.ru");
        R1.addContact(ContactType.PHONE, "11111");
//        R1.addSection(SectionType.OBJECTIVE, new TextSection("Objective1"));
//        R1.addSection(SectionType.PERSONAL, new TextSection("Personal data"));
//        R1.addSection(SectionType.ACHIEVEMENT, new ListSection("Achivment11", "Achivment12", "Achivment13"));
//        R1.addSection(SectionType.QUALIFICATIONS, new ListSection("Java", "SQL", "JavaScript"));
//        R1.addSection(SectionType.EXPERIENCE,
//                new OrganizationSection(
//                        new Organization("Organization11", "http://Organization11.ru",
//                                new Organization.Position(2005, Month.JANUARY, "position1", "content1"),
//                                new Organization.Position(2001, Month.MARCH, 2005, Month.JANUARY, "position2", "content2"))));
//        R1.addSection(SectionType.EDUCATION,
//                new OrganizationSection(
//                        new Organization("Institute", null,
//                                new Organization.Position(1996, Month.JANUARY, 2000, Month.DECEMBER, "aspirant", null),
//                                new Organization.Position(2001, Month.MARCH, 2005, Month.JANUARY, "student", "IT facultet")),
//                        new Organization("Organization12", "http://Organization12.ru")));
        R2.addContact(ContactType.SKYPE, "skype2");
        R2.addContact(ContactType.PHONE, "22222");
//        R1.addSection(SectionType.EXPERIENCE,
//                new OrganizationSection(
//                        new Organization("Organization2", "http://Organization2.ru",
//                                new Organization.Position(2015, Month.JANUARY, "position1", "content1"))));
    }

    @BeforeEach
    void setUp() {
        storage = Config.getInstance().getStorage();
        storage.clear();
        storage.save(R1);
        storage.save(R2);
        storage.save(R3);
    }

    @Test
    void get() {
        assertGet(R1);
        assertGet(R2);
        assertGet(R3);
    }

    @Test
    void clear() {
        storage.clear();
        assertSize(0);
    }

    @Test
    void update() {
        Resume newResume = new Resume(UUID_1, "NEW");
        R1.addContact(ContactType.MAIL, "mail1@google.com");
        R1.addContact(ContactType.SKYPE, "skype666");
        R1.addContact(ContactType.PHONE, "1234567890");
        storage.update(newResume);
        assertEquals(newResume, storage.get(UUID_1));
    }

    @Test
    void save() {
        storage.save(R4);
        assertSize(4);
        assertGet(R4);
    }

    @Test
    public void saveExist() {
        assertThrows(StorageException.class, () -> storage.save(R1));
    }

    @Test
    void delete() {
        storage.delete(UUID_1);
        assertSize(2);
        assertThrows(NotExistStorageException.class, () -> storage.get(UUID_1));
    }

    @Test
    public void deleteNotExist() {
        assertThrows(NotExistStorageException.class, () -> storage.delete("dummy"));

    }

    @Test
    void getAllSorted() {
        List<Resume> array = storage.getAllSorted();
        assertEquals(3, array.size());
        List<Resume> sortedResumes = Arrays.asList(R1, R2, R3);
        Collections.sort(sortedResumes);
        assertEquals(sortedResumes, array);
    }

    @Test
    void size() {
        assertSize(3);
    }

    @Test
    public void getNotExist() {
        assertThrows(NotExistStorageException.class, () -> storage.get("dummy"));
    }

    private void assertGet(Resume r) {
        assertEquals(r, storage.get(r.getUuid()));
    }

    private void assertSize(int size) {
        assertEquals(size, storage.size());
    }
}