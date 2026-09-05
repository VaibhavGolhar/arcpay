package com.btech_major_project.Personal_Cloud;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsageService {

    private static final AppLogger log = AppLogger.getLogger(UsageService.class);

    private final UserUsageRepository repo;

    public UsageService(UserUsageRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void ensureRow(User user) {
        if (repo.findByUserId(user.getId()).isEmpty()) {
            UserUsage u = new UserUsage();
            u.setUser(user);
            repo.save(u);
            log.info("Created usage row for userId=" + user.getId());
        }
    }

    @Transactional
    public void onPut(User user, long bytesDelta, boolean isNewObject) {
        ensureRow(user);
        repo.addPut(user.getId(), 1);
        repo.addBytes(user.getId(), bytesDelta);
        if (isNewObject) repo.addObjects(user.getId(), 1);
    }

    @Transactional
    public void onList(User user) {
        ensureRow(user);
        repo.addList(user.getId(), 1);
    }

    @Transactional
    public void onGet(User user) {
        ensureRow(user);
        repo.addGet(user.getId(), 1);
    }

    @Transactional
    public void onDelete(User user, long bytesDelta) {
        ensureRow(user);
        repo.addDelete(user.getId(), 1);
        repo.addBytes(user.getId(), -bytesDelta);
        repo.addObjects(user.getId(), -1);
    }

    @Transactional
    public void onCopy(User user) {
        ensureRow(user);
        repo.addCopy(user.getId(), 1);
    }

    @Transactional
    public void onPost(User user) {
        ensureRow(user);
        repo.addPost(user.getId(), 1);
    }

    @Transactional
    public void onSelect(User user) {
        ensureRow(user);
        repo.addSelect(user.getId(), 1);
    }

    @Transactional
    public void onOther(User user) {
        ensureRow(user);
        repo.addOther(user.getId(), 1);
    }
}

