package org.tientt.repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import org.tientt.models.entities.FileEntity;
import org.tientt.models.entities.FileType;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FileRepositoryTest {

    @Autowired
    private FileRepository fileRepository;

    @BeforeEach
    @Transactional
    public void setup() {
        FileEntity rootEntity = new FileEntity();
        FileEntity childDirectory = new FileEntity();
        FileEntity complexPathChildDirectory = new FileEntity();

        rootEntity.setType(FileType.ROOT);
        rootEntity.setId(1);
        rootEntity.setName("root");
        rootEntity.setCreatedAt(0);
        rootEntity.setUpdatedAt(0);

        childDirectory.setCreatedAt(1L);
        childDirectory.setId(123L);
        childDirectory.setName("Name");
        childDirectory.setParent(rootEntity);
        childDirectory.setType(FileType.DIRECTORY);
        childDirectory.setUpdatedAt(1L);

        complexPathChildDirectory.setCreatedAt(1L);
        complexPathChildDirectory.setId(1234L);
        complexPathChildDirectory.setName("Complex name");
        complexPathChildDirectory.setParent(childDirectory);
        complexPathChildDirectory.setType(FileType.DIRECTORY);
        complexPathChildDirectory.setUpdatedAt(1L);

        fileRepository.saveAll(List.of(rootEntity, childDirectory, complexPathChildDirectory));

    }

    @AfterEach
    void tearDown() {
        fileRepository.deleteAll();
    }

    @Test
    void getRootDirectory() {
        //when
        FileEntity rootDirectory = fileRepository.getRootDirectory();
        //then
        assertThat(rootDirectory.getType()).isEqualTo(FileType.ROOT);
    }



}
