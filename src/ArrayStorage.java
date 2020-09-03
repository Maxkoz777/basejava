import java.util.Arrays;
import java.util.Objects;

/**
 * Array based storage for Resumes
 */
public class ArrayStorage {
    Resume[] storage = new Resume[10000];

    void clear() {
        for (int i = 0; i < storage.length; i++) {
            if (storage[i] == null) {
                break;
            }
            storage[i] = null;
        }
    }

    void save(Resume r) {
        storage[(int) Arrays.stream(storage)
                .filter(Objects::nonNull)
                .count()] = r;
    }

    Object get(String uuid) {
        for (Resume resume : storage){
            if (resume == null || resume.uuid.equals(uuid)) {
                return resume;
            }
        }
        return null;
    }

    void delete(String uuid) {
        int index = Integer.MIN_VALUE;
        for (int i = 0; i < storage.length; i++) {
            if (storage[i].uuid.equals(uuid)){
                index = i;
                break;
            }
        }
        if (index != Integer.MIN_VALUE){
            for (int i = index; i < storage.length - 1; i++) {
                if (storage[i] == null) {
                    break;
                }
                storage[i] = storage[i + 1];
            }
        }
    }

    /**
     * @return array, contains only Resumes in storage (without null)
     */
    Resume[] getAll() {
        return Arrays.stream(storage)
                .filter(Objects::nonNull)
                .toArray(Resume[]::new);
    }

    int size() {
        return (int) Arrays.stream(storage)
                .filter(Objects::nonNull)
                .count();
    }
}
