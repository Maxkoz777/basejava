package webapp.com.urise.storage;

import webapp.com.urise.model.Resume;

public interface Storage {
    void clear();

    void update(Resume resume);

    void save(Resume r);

    Resume get(String uuid);

    void delete(String uuid);

    Resume[] getAll();

    int size();
}
