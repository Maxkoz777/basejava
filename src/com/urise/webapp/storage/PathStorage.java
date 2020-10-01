package com.urise.webapp.storage;

import com.urise.webapp.exception.StorageException;
import com.urise.webapp.model.Resume;
import com.urise.webapp.storage.serializer.StreamSerializer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathStorage extends AbstractStorage<Path> {

    private Path directory;
    private StreamSerializer serializer;

    public PathStorage(String dir, StreamSerializer serializer) {
        Objects.requireNonNull(dir, "directory can't be null");
        directory = Paths.get(dir);
        this.serializer = serializer;
        if (!Files.isDirectory(directory) || !Files.isWritable(directory)) {
            throw new IllegalArgumentException(dir + " should be able to be read AND be rewritten");
        }
    }

    @Override

    protected List<Resume> doCopyAll() {
        return getFileList().map(this::doGet).collect(Collectors.toList());

    }

    @Override
    protected void doDelete(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new StorageException("file delete error", getFIleName(path), e);
        }
    }

    @Override
    protected void doUpdate(Resume resume, Path path) {
        try {
            serializer.doWrite(resume, new BufferedOutputStream(Files.newOutputStream(path)));
        } catch (IOException e) {
            throw new StorageException("Path write error", resume.getUuid(), e);
        }
    }

    @Override
    protected boolean isExist(Path path) {
        return Files.isRegularFile(path);
    }

    @Override
    protected void doSave(Resume resume, Path path) {
        try {
            Files.createFile(path);
        } catch (IOException e) {
            throw new StorageException("Can't create file " + path, path.toString());
        }
        doUpdate(resume, path);
    }

    @Override
    protected Resume doGet(Path path) {
        try {
            return serializer.doRead(new BufferedInputStream(Files.newInputStream(path)));
        } catch (IOException e) {
            throw new StorageException("IO error ", getFIleName(path));
        }
    }

    @Override
    protected Path getSearchKey(String uuid) {
        return directory.resolve(uuid);
    }

    @Override
    public void clear() {
        getFileList().forEach(this::doDelete);
    }

    private Stream<Path> getFileList() {
        try {
            return Files.list(directory);
        } catch (IOException e) {
            throw new StorageException("Directory read error", null, e);
        }
    }

    @Override
    public int size() {
        return (int) getFileList().count();
    }

    private String getFIleName(Path path) {
        return path.getFileName().toString();
    }
}
