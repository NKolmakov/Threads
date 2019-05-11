package by.grodno.managers;

import by.grodno.entities.Storage;

public class StorageManager {
    private Storage storage;

    public StorageManager(Storage storage) {
        this.storage = storage;
    }

    public Storage getStorage() {
        return storage;
    }
}
