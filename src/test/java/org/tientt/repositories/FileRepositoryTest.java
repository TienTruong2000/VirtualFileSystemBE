package org.tientt.repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.tientt.models.entities.FileEntity;
import org.tientt.models.entities.FileType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FileRepositoryTest {

    @Autowired
    private FileRepository fileRepository;

    @BeforeEach
    public void setup() {
        FileEntity rootEntity = new FileEntity();
        rootEntity.setType(FileType.ROOT);
        rootEntity.setId(1);
        rootEntity.setName("root");
        rootEntity.setCreatedAt(0);
        rootEntity.setUpdatedAt(0);
        rootEntity = fileRepository.save(rootEntity);
    }

    @AfterEach
    void tearDown() {
        fileRepository.deleteAll();
    }

    @Test
    void itShouldGetRootDirectory() {
        //when
        FileEntity rootDirectory = fileRepository.getRootDirectory();
        //then
        assertThat(rootDirectory.getType()).isEqualTo(FileType.ROOT);
    }

}
