package ru.vixtor.cloudservice.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vixtor.cloudservice.entity.File;
import ru.vixtor.cloudservice.entity.User;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, String> {

    File findByUserAndFilename(User user, String filename);
    void removeByUserAndFilename(User user, String filename);
    List<File> findAllByUser(User user, Sort sort);

    @Modifying
    @Query("update File f set f.filename = :newName where f.filename = :filename and f.user = :user")
    void editFileNameByUser(@Param("user") User user, @Param("filename") String filename, @Param("newName") String newName);
}
