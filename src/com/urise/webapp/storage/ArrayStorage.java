package com.urise.webapp.storage;

import com.urise.webapp.model.Resume;

/**
 * Array based storage for Resumes
 */
public class ArrayStorage extends AbstractArrayStorage {

    @Override
    protected void insertElement(int index, Resume resume) {
        storage[size] = resume;
    }

    /**
     * @param uuid just an instance of Resume-class
     * @return index of resume in storage if it is present there and -1 if not
     */

    @Override
    public Integer getSearchKey(String uuid) {
        for (int i = 0; i < size; i++) {
            if (storage[i].getUuid().equals(uuid)) {
                return i;
            }
        }
        return -1;
    }

}
