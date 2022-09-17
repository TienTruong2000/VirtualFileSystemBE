package org.tientt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.tientt.models.entities.FileEntity;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    @Query(value = "select f from FileEntity f WHERE f.type = org.tientt.models.entities.FileType.ROOT")
    FileEntity getRootDirectory();
}
