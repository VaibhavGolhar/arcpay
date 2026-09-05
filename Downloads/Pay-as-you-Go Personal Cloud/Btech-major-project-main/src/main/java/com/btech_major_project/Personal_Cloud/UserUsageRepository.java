package com.btech_major_project.Personal_Cloud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserUsageRepository extends JpaRepository<UserUsage, Long> {

    Optional<UserUsage> findByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("update UserUsage u set u.totalBytesStored = u.totalBytesStored + :delta where u.user.id = :userId")
    int addBytes(@Param("userId") Long userId, @Param("delta") long delta);

    @Transactional
    @Modifying
    @Query("update UserUsage u set u.objectCount = u.objectCount + :delta where u.user.id = :userId")
    int addObjects(@Param("userId") Long userId, @Param("delta") long delta);

    @Transactional
    @Modifying
    @Query("update UserUsage u set u.putCount = u.putCount + :c where u.user.id = :userId")
    int addPut(@Param("userId") Long userId, @Param("c") long c);

    @Transactional
    @Modifying
    @Query("update UserUsage u set u.copyCount = u.copyCount + :c where u.user.id = :userId")
    int addCopy(@Param("userId") Long userId, @Param("c") long c);

    @Transactional
    @Modifying
    @Query("update UserUsage u set u.postCount = u.postCount + :c where u.user.id = :userId")
    int addPost(@Param("userId") Long userId, @Param("c") long c);

    @Transactional
    @Modifying
    @Query("update UserUsage u set u.listCount = u.listCount + :c where u.user.id = :userId")
    int addList(@Param("userId") Long userId, @Param("c") long c);

    @Transactional
    @Modifying
    @Query("update UserUsage u set u.getCount = u.getCount + :c where u.user.id = :userId")
    int addGet(@Param("userId") Long userId, @Param("c") long c);

    @Transactional
    @Modifying
    @Query("update UserUsage u set u.selectCount = u.selectCount + :c where u.user.id = :userId")
    int addSelect(@Param("userId") Long userId, @Param("c") long c);

    @Transactional
    @Modifying
    @Query("update UserUsage u set u.deleteCount = u.deleteCount + :c where u.user.id = :userId")
    int addDelete(@Param("userId") Long userId, @Param("c") long c);

    @Transactional
    @Modifying
    @Query("update UserUsage u set u.otherCount = u.otherCount + :c where u.user.id = :userId")
    int addOther(@Param("userId") Long userId, @Param("c") long c);
}

