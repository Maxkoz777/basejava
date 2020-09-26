package com.urise.webapp.storage;

import com.urise.webapp.exception.ExistStorageException;
import com.urise.webapp.exception.NotExistStorageException;
import com.urise.webapp.model.Resume;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractStorage<SK> implements Storage {

    private static final Logger LOG = Logger.getLogger(AbstractStorage.class.getName());

    @Override
    public void update(Resume resume) {
        LOG.info("update " + resume);
        SK searchKey = getExistedSearchKey(resume.getUuid());
        doUpdate(resume, searchKey);
    }

    @Override
    public void save(Resume resume) {
        LOG.info("save " + resume);
        SK searchKey = getNotExistedSearchKey(resume.getUuid());
        doSave(resume, searchKey);
    }

    @Override
    public void delete(String uuid) {
        LOG.info("delete " + uuid);
        SK searchKey = getExistedSearchKey(uuid);
        doDelete(searchKey);
    }

    @Override
    public Resume get(String uuid) {
        LOG.info("update " + uuid);
        SK searchKey = getExistedSearchKey(uuid);
        return doGet(searchKey);
    }


    private SK getExistedSearchKey(String uuid) {
        LOG.info("getExistedKey " + uuid);
        SK searchKey = getSearchKey(uuid);
        if (!isExist(searchKey)) {
            LOG.log(Level.WARNING, "uuid " + uuid + " already exist");
            throw new NotExistStorageException(uuid);
        }
        return searchKey;
    }

    private SK getNotExistedSearchKey(String uuid) {
        LOG.info("getNotExistedKey " + uuid);
        SK searchKey = getSearchKey(uuid);
        if (isExist(searchKey)) {
            LOG.log(Level.WARNING, "uuid " + uuid + " not exist");
            throw new ExistStorageException(uuid);
        }
        return searchKey;
    }

    /**
     * @return array, contains only Resumes in storage (without null)
     */
    @Override
    public List<Resume> getAllSorted() {
        LOG.info("getAllSorted");
        List<Resume> list = doCopyAll();
        Collections.sort(list);
        return list;
    }

    protected abstract List<Resume> doCopyAll();

    protected abstract void doDelete(SK searchKey);

    protected abstract void doUpdate(Resume resume, SK searchKey);

    protected abstract boolean isExist(SK searchKey);

    protected abstract void doSave(Resume resume, SK searchKey);

    protected abstract Resume doGet(SK searchKey);

    protected abstract SK getSearchKey(String uuid);
}
