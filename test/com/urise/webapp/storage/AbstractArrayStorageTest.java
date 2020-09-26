package com.urise.webapp.storage;

import com.urise.webapp.exception.StorageException;
import com.urise.webapp.model.Resume;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class AbstractArrayStorageTest extends AbstractStorageTest {

    @Test
    public void saveOverflow() {
        try {
            for (int i = 4; i < AbstractArrayStorage.STORAGE_LIMIT; i++) {
                storage.save(new Resume("" + i, "name" + i));
            }
        } catch (StorageException e) {
            fail();
        }
        assertThrows(StorageException.class, () -> storage.save(new Resume("90", "fullName")));
    }

}
