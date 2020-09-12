package webapp.com.urise.storage;

import webapp.com.urise.model.Resume;

public abstract class AbstractArrayStorage implements Storage {
    protected static final int STORAGE_LIMIT = 10000;
    public int size = 0;
    protected Resume[] storage = new Resume[STORAGE_LIMIT];

    @Override
    public Resume get(String uuid) {
        int index = isPresent(uuid);
        if (index == -1){
            System.out.println("no such element");
            return null;
        }
        else{
            return storage[index];
        }
    }

    protected abstract int isPresent(String uuid);
}
