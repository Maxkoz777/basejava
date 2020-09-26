package com.urise.webapp.storage;

import com.urise.webapp.exception.StorageException;
import com.urise.webapp.model.Resume;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

abstract class AbstractFileStorage extends AbstractStorage<File> {

    private File directory;

    public AbstractFileStorage(File directory) {
        Objects.requireNonNull(directory, "directory can't be null");
        if (!directory.isDirectory()){
            throw new IllegalArgumentException(directory.getAbsolutePath() + " is not a directory");
        }
        if (!directory.canRead() || !directory.canWrite()){
            throw new IllegalArgumentException(directory.getAbsolutePath() + " should be able to be read AND be rewritten");
        }
        this.directory = directory;
    }

    @Override

    protected List<Resume> doCopyAll() {
        File[] files = directory.listFiles();
        if (files == null){
            throw new StorageException("directory read error", null);
        }
        List<Resume> list = new ArrayList<>();
        for (File file : files){
            list.add(doGet(file));
        }
        return list;
    }

    @Override
    protected void doDelete(File file) {
        if (!file.delete()){
            throw new StorageException("file delete error", file.getName());
        }
    }

    @Override
    protected void doUpdate(Resume resume, File file) {
        try {
            doWrite(resume, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean isExist(File file) {
        return file.exists();
    }

    @Override
    protected void doSave(Resume resume, File file) {
        try {
            file.createNewFile();
            doWrite(resume, file);
        } catch (IOException e) {
            throw new StorageException("IO error ", file.getName());
        }
    }

    protected abstract void doWrite(Resume resume, File file) throws IOException;
    protected abstract Resume doRead(File file) throws IOException;

    @Override
    protected Resume doGet(File file) {
        try {
            return doRead(file);
        } catch (IOException e) {
            throw new StorageException("IO error ", file.getName());
        }
    }

    @Override
    protected File getSearchKey(String uuid) {
        return new File(directory, uuid);
    }

    @Override
    public void clear() {
        File[] files = directory.listFiles();
        if (files != null){
            for (File file : files){
                doDelete(file);
            }
        }
    }

    @Override
    public int size() {
        File[] files = directory.listFiles();
        if (files == null){
            throw new StorageException("Directory read error", null);
        }
        return files.length;
    }
}
