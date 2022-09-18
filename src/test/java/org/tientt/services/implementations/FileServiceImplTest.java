package org.tientt.services.implementations;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.tientt.models.entities.FileEntity;
import org.tientt.models.entities.FileType;
import org.tientt.repositories.FileRepository;
import org.tientt.services.mappers.FileMapper;

@ContextConfiguration(classes = {FileServiceImpl.class})
@ExtendWith(SpringExtension.class)
class FileServiceImplTest {
    @MockBean
    private FileMapper fileMapper;

    @MockBean
    private FileRepository fileRepository;

    @Autowired
    private FileServiceImpl fileServiceImpl;

    /**
     * Method under test: {@link FileServiceImpl#getNearestParentElement(String[])}
     */
    @Test
    void testGetNearestParentElement() {
        assertThrows(IllegalArgumentException.class,
                () -> fileServiceImpl.getNearestParentElement(new String[]{"Path Elements"}));
    }

    /**
     * Method under test: {@link FileServiceImpl#getNearestParentElement(String[])}
     */
    @Test
    void testGetNearestParentElement2() {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setChildren(new ArrayList<>());
        fileEntity.setContent("Not all who wander are lost");
        fileEntity.setCreatedAt(1L);
        fileEntity.setId(123L);
        fileEntity.setName("Name");
        fileEntity.setParent(new FileEntity());
        fileEntity.setType(FileType.REGULAR_FILE);
        fileEntity.setUpdatedAt(1L);

        FileEntity fileEntity1 = new FileEntity();
        fileEntity1.setChildren(new ArrayList<>());
        fileEntity1.setContent("Not all who wander are lost");
        fileEntity1.setCreatedAt(1L);
        fileEntity1.setId(123L);
        fileEntity1.setName("Name");
        fileEntity1.setParent(fileEntity);
        fileEntity1.setType(FileType.REGULAR_FILE);
        fileEntity1.setUpdatedAt(1L);

        FileEntity fileEntity2 = new FileEntity();
        fileEntity2.setChildren(new ArrayList<>());
        fileEntity2.setContent("Not all who wander are lost");
        fileEntity2.setCreatedAt(1L);
        fileEntity2.setId(123L);
        fileEntity2.setName("Name");
        fileEntity2.setParent(fileEntity1);
        fileEntity2.setType(FileType.REGULAR_FILE);
        fileEntity2.setUpdatedAt(1L);

        FileEntity fileEntity3 = new FileEntity();
        fileEntity3.setChildren(new ArrayList<>());
        fileEntity3.setContent("Not all who wander are lost");
        fileEntity3.setCreatedAt(1L);
        fileEntity3.setId(123L);
        fileEntity3.setName("Name");
        fileEntity3.setParent(fileEntity2);
        fileEntity3.setType(FileType.REGULAR_FILE);
        fileEntity3.setUpdatedAt(1L);

        FileEntity fileEntity4 = new FileEntity();
        fileEntity4.setChildren(new ArrayList<>());
        fileEntity4.setContent("Not all who wander are lost");
        fileEntity4.setCreatedAt(1L);
        fileEntity4.setId(123L);
        fileEntity4.setName("Name");
        fileEntity4.setParent(fileEntity3);
        fileEntity4.setType(FileType.REGULAR_FILE);
        fileEntity4.setUpdatedAt(1L);
        when(fileRepository.getRootDirectory()).thenReturn(fileEntity4);
        assertSame(fileEntity4, fileServiceImpl.getNearestParentElement(new String[]{""}));
        verify(fileRepository).getRootDirectory();
    }

    /**
     * Method under test: {@link FileServiceImpl#getNearestParentElement(String[])}
     */
    @Test
    void testGetNearestParentElement3() {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setChildren(new ArrayList<>());
        fileEntity.setContent("Not all who wander are lost");
        fileEntity.setCreatedAt(1L);
        fileEntity.setId(123L);
        fileEntity.setName("Name");
        fileEntity.setParent(new FileEntity());
        fileEntity.setType(FileType.REGULAR_FILE);
        fileEntity.setUpdatedAt(1L);

        FileEntity fileEntity1 = new FileEntity();
        fileEntity1.setChildren(new ArrayList<>());
        fileEntity1.setContent("Not all who wander are lost");
        fileEntity1.setCreatedAt(1L);
        fileEntity1.setId(123L);
        fileEntity1.setName("Name");
        fileEntity1.setParent(fileEntity);
        fileEntity1.setType(FileType.REGULAR_FILE);
        fileEntity1.setUpdatedAt(1L);

        FileEntity fileEntity2 = new FileEntity();
        fileEntity2.setChildren(new ArrayList<>());
        fileEntity2.setContent("Not all who wander are lost");
        fileEntity2.setCreatedAt(1L);
        fileEntity2.setId(123L);
        fileEntity2.setName("Name");
        fileEntity2.setParent(fileEntity1);
        fileEntity2.setType(FileType.REGULAR_FILE);
        fileEntity2.setUpdatedAt(1L);

        FileEntity fileEntity3 = new FileEntity();
        fileEntity3.setChildren(new ArrayList<>());
        fileEntity3.setContent("Not all who wander are lost");
        fileEntity3.setCreatedAt(1L);
        fileEntity3.setId(123L);
        fileEntity3.setName("Name");
        fileEntity3.setParent(fileEntity2);
        fileEntity3.setType(FileType.REGULAR_FILE);
        fileEntity3.setUpdatedAt(1L);

        FileEntity fileEntity4 = new FileEntity();
        fileEntity4.setChildren(new ArrayList<>());
        fileEntity4.setContent("Not all who wander are lost");
        fileEntity4.setCreatedAt(1L);
        fileEntity4.setId(123L);
        fileEntity4.setName("Name");
        fileEntity4.setParent(fileEntity3);
        fileEntity4.setType(FileType.REGULAR_FILE);
        fileEntity4.setUpdatedAt(1L);
        when(fileRepository.getRootDirectory()).thenReturn(fileEntity4);
        assertThrows(IllegalArgumentException.class, () -> fileServiceImpl.getNearestParentElement(new String[]{}));
    }

    /**
     * Method under test: {@link FileServiceImpl#getNearestParentElement(String[])}
     */
    @Test
    void testGetNearestParentElement4() {
        when(fileRepository.getRootDirectory()).thenThrow(new IllegalArgumentException("foo"));
        assertThrows(IllegalArgumentException.class, () -> fileServiceImpl.getNearestParentElement(new String[]{""}));
        verify(fileRepository).getRootDirectory();
    }
}

