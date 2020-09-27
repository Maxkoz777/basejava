package com.urise.webapp.storage.serializer;

import com.urise.webapp.exception.StorageException;
import com.urise.webapp.model.Resume;
import com.urise.webapp.storage.serializer.StreamSerializer;

import java.io.*;

public class ObjectStreamSerializer implements StreamSerializer {

    @Override
    public void doWrite(Resume resume, OutputStream stream) throws IOException {
        try(ObjectOutputStream outputStream = new ObjectOutputStream(stream)){
            outputStream.writeObject(resume);
        }
    }

    @Override
    public Resume doRead(InputStream stream) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(stream)) {
            return (Resume) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new StorageException("Error read resume", null, e);
        }
    }
}
