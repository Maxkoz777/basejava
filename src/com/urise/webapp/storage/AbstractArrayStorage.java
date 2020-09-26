package com.urise.webapp.storage;

import com.urise.webapp.exception.StorageException;
import com.urise.webapp.model.Resume;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractArrayStorage extends AbstractStorage<Integer> {
    protected static final int STORAGE_LIMIT = 10000;
    public int size = 0;
    protected Resume[] storage = new Resume[STORAGE_LIMIT];

    @Override
    public void clear() {
        Arrays.fill(storage, 0, size, null);
        size = 0;
    }

    protected abstract void insertElement(int index, Resume resume);

    @Override
    protected List<Resume> doCopyAll() {
        return Arrays.asList(Arrays.copyOfRange(storage, 0, size));
    }

    @Override
    protected void doDelete(Integer searchKey) {
        System.arraycopy(storage, searchKey + 1, storage, searchKey, size - 1 - searchKey);
        storage[size - 1] = null;
        size--;
    }

    @Override
    protected void doUpdate(Resume resume, Integer searchKey) {
        storage[searchKey] = resume;
    }

    @Override
    protected boolean isExist(Integer searchKey) {
        return searchKey >= 0;
    }

    @Override
    protected void doSave(Resume resume, Integer searchKey) {
        if (size == STORAGE_LIMIT) {
            throw new StorageException("Storage overflow", resume.getUuid());
        } else {
            insertElement(searchKey, resume);
            size++;
        }
    }

    @Override
    protected Resume doGet(Integer searchKey) {
        return storage[(Integer) searchKey];
    }

    @Override
    public int size() {
        return size;
    }
}
