package webapp.com.urise.storage;

import webapp.com.urise.model.Resume;

/**
 * Array based storage for Resumes
 */
public class ArrayStorage extends AbstractArrayStorage {

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            storage[i] = null;
        }
        size = 0;
    }

    @Override
    public void update(Resume resume) {
        int index = isPresent(resume.getUuid());
        storage[index] = resume;
    }

    @Override
    public void save(Resume r) {
        if (isPresent(r.getUuid()) == -1) {
            if (size != STORAGE_LIMIT - 1) {
                storage[size] = r;
                size++;
            }
        } else {
            System.out.println("such element already exist");
        }
    }

    /**
     * @param uuid just an instance of Resume-class
     * @return index of resume in storage if it is present there and -1 if not
     */

    protected int isPresent(String uuid) {
        int index = -1;
        for (int i = 0; i < size; i++) {
            if (storage[i].getUuid().equals(uuid)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void delete(String uuid) {
        int index = isPresent(uuid);
        if (index != -1) {
            if (size - 1 - index >= 0) {
                System.arraycopy(storage, index + 1, storage, index, size - 1 - index);
            }
            size--;
        }
    }

    /**
     * @return array, contains only Resumes in storage (without null)
     */
    @Override
    public Resume[] getAll() {
        Resume[] resume = new Resume[size];
        System.arraycopy(storage, 0, resume, 0, size);
        return resume;
    }

    @Override
    public int size() {
        return size;
    }
}
