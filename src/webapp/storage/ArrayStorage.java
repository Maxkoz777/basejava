package webapp.storage;

import webapp.model.Resume;

/**
 * Array based storage for Resumes
 */
public class ArrayStorage {
    Resume[] storage = new Resume[10000];
    private int size = 0;

    public void clear() {
        for (int i = 0; i < size; i++) {
            storage[i] = null;
        }
        size = 0;
    }
    
    public void update(Resume resume){

    }

    public void save(Resume r) {
        if (isPresent(r) == -1) {
            storage[size] = r;
            size++;
        }
    }

    public Resume get(String uuid) {
        for (int i = 0; i < size; i++) {
            if (storage[i].uuid.equals(uuid)) {
                return storage[i];
            }
        }
        return null;
    }

    /**
     * @param resume
     * @return index of resume in storage if it is present there and -1 if not
     */

    private int isPresent(Resume resume){
        int index = -1;
        for (int i = 0; i < size; i++) {
            if (storage[i] == resume) {
                index = i;
                break;
            }
        }
        return index;
    }

    public void delete(String uuid) {
        int index = isPresent(new Resume(uuid));
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
    public Resume[] getAll() {
        Resume[] resume = new Resume[size];
        System.arraycopy(storage, 0, resume, 0, size);
        return resume;
    }

    public int size() {
        return size;
    }
}
