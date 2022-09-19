package org.tientt.services.implementations;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.tientt.models.dtos.FileDTO;
import org.tientt.models.entities.FileEntity;
import org.tientt.models.entities.FileType;
import org.tientt.repositories.FileRepository;
import org.tientt.services.mappers.FileMapper;
import org.tientt.services.mappers.FileMapperImpl;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.assertj.core.api.BDDAssumptions.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    private FileServiceImpl underTest;

    private AutoCloseable autoCloseable;

    @Mock
    private FileMapperImpl fileMapper;

    @Mock
    private FileRepository fileRepository;

    private final String PATH_SEPARATOR = "/";

    private final FileEntity rootEntity = new FileEntity();

    private final FileEntity childDirectory = new FileEntity();

    private final FileEntity complexPathChildDirectory = new FileEntity();

    private final FileEntity duplidateNameChildDirectory = new FileEntity();

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new DirectoryServiceImpl();
        underTest.setFileRepository(fileRepository);
        underTest.setFileMapper(fileMapper);
        underTest.setPATH_SEPARATOR(PATH_SEPARATOR);
        ReflectionTestUtils.setField(underTest, FileServiceImpl.class, "fileRepository", fileRepository, FileRepository.class);
        ReflectionTestUtils.setField(underTest, FileServiceImpl.class, "fileMapper", fileMapper, FileMapper.class);
        ReflectionTestUtils.setField(underTest, FileServiceImpl.class, "PATH_SEPARATOR", PATH_SEPARATOR, String.class);


        rootEntity.setType(FileType.ROOT);
        rootEntity.setId(1);
        rootEntity.setName("root");
        rootEntity.setCreatedAt(0);
        rootEntity.setUpdatedAt(0);
        rootEntity.setChildren(List.of(childDirectory));

        childDirectory.setCreatedAt(1L);
        childDirectory.setId(123L);
        childDirectory.setName("Name");
        childDirectory.setParent(rootEntity);
        childDirectory.setType(FileType.DIRECTORY);
        childDirectory.setUpdatedAt(1L);
        childDirectory.setChildren(List.of(complexPathChildDirectory, duplidateNameChildDirectory));

        complexPathChildDirectory.setCreatedAt(1L);
        complexPathChildDirectory.setId(1234L);
        complexPathChildDirectory.setName("Complex name");
        complexPathChildDirectory.setParent(childDirectory);
        complexPathChildDirectory.setType(FileType.DIRECTORY);
        complexPathChildDirectory.setUpdatedAt(1L);

        duplidateNameChildDirectory.setCreatedAt(1L);
        duplidateNameChildDirectory.setId(12345L);
        duplidateNameChildDirectory.setName("Name");
        duplidateNameChildDirectory.setParent(rootEntity);
        duplidateNameChildDirectory.setType(FileType.DIRECTORY);
        duplidateNameChildDirectory.setUpdatedAt(1L);


    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void findChildIndexByName_existedChildName_returnIndex() {
        //given
        String childName = childDirectory.getName();
        //when
        int actual = underTest.findChildIndexByName(rootEntity, childName);
        //then
        assertThat(actual).isNotEqualTo(-1);
    }

    @Test
    void findChildIndexByName_nonExistedChildName_returnMinusOne() {
        //given
        String childName = "abc";
        //when
        int actual = underTest.findChildIndexByName(rootEntity, childName);
        //then
        assertThat(actual).isEqualTo(-1);
    }

    @Test
    void findChildIndexByName_parentHasNullChildren_returnMinusOne() {
        //given
        String childName = "abc";
        given(complexPathChildDirectory.getChildren()).isNull();
        //when
        int actual = underTest.findChildIndexByName(complexPathChildDirectory, childName);
        //then
        assertThat(actual).isEqualTo(-1);
    }

    @Test
    void validateDirectoryName_validNames_notThrowException() {
        //given
        String[] validNames = {"directory", "new_file", "new file", "new     file", "nEw fILE"};
        //when
        //then
        for (String name : validNames) {
            assertThatNoException().isThrownBy(() -> underTest.validateDirectoryName(name));
        }
    }

    @Test
    void validateDirectoryName_invalidNames_throwException() {
        //given
        String[] invalidNames = {"file.txt", "@tien", "..", "/", ";tien"};
        //when
        //then
        for (String name : invalidNames) {
            assertThatThrownBy(() -> underTest.validateDirectoryName(name)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void validateDuplicateName_duplicateName_throwException() {
        //given
        String name = childDirectory.getName();
        //when-then
        assertThatThrownBy(() -> underTest.validateDuplicateName(rootEntity, name))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validateDuplicateName_notDuplicateName_notThrowException() {
        //given
        String name = "123";
        //when-then
        assertThatNoException().isThrownBy(() -> underTest.validateDuplicateName(rootEntity, name));
    }

    @Test
    void getNearestParentElement_emptyPathElements_throwException() {
        //given
        String[] pathElements = {};
        //when-then
        assertThatThrownBy(() -> underTest.getNearestParentElement(pathElements))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getNearestParentElement_notRelativePathElements_throwException() {
        //given
        String[] pathElements = {"abc", "123"};
        //when-then
        assertThatThrownBy(() -> underTest.getNearestParentElement(pathElements))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getNearestParentElement_validRelativePathElements_getFileEntity() {
        //given
        String[] pathElements = {"", childDirectory.getName()};
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        FileEntity actual = underTest.getNearestParentElement(pathElements);
        //then
        assertThat(actual.getId()).isEqualTo(rootEntity.getId());
    }

    @Test
    void getNearestParentElement_invalidRelativePathElements_getFileEntity() {
        //given
        String[] pathElements = {"", "123", childDirectory.getName()};
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        //then
        assertThatThrownBy(() -> underTest.getNearestParentElement(pathElements))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getNearestParentElement_validComplexRelativePathElements_getFileEntity() {
        //given
        String[] pathElements = {"", childDirectory.getName(), complexPathChildDirectory.getName()};
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        FileEntity actual = underTest.getNearestParentElement(pathElements);
        //then
        assertThat(actual.getId()).isEqualTo(childDirectory.getId());
    }

    @Test
    void getFileFromPath_emptyPathElements_throwException() {
        //given
        String path = "";
        //when-then
        assertThatThrownBy(() -> underTest.getFileFromPath(path))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getFileFromPath_notRelativePathElements_throwException() {
        //given
        String path = "/abc/123";
        //when-then
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);

        assertThatThrownBy(() -> underTest.getFileFromPath(path))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getFileFromPath_validRelativePathElements_getFileEntity() {
        //given
        String path = "/" + childDirectory.getName();
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        FileEntity actual = underTest.getFileFromPath(path);
        //then
        assertThat(actual.getId()).isEqualTo(childDirectory.getId());
    }

    @Test
    void getFileFromPath_invalidRelativePathElements_getFileEntity() {
        //given
        String path = "/123/" + childDirectory.getName();
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        //then
        assertThatThrownBy(() -> underTest.getFileFromPath(path))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getFileFromPath_validComplexRelativePathElements_getFileEntity() {
        //given
        String[] pathElements = {"", childDirectory.getName(), complexPathChildDirectory.getName()};
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        FileEntity actual = underTest.getNearestParentElement(pathElements);
        //then
        assertThat(actual.getId()).isEqualTo(complexPathChildDirectory.getId());
    }

    @Test
    void deleteByPath_emptyOrNullPath_throwException() {
        //given
        String path = "";
        //when-then
        assertThatThrownBy(() -> {
            underTest.deleteByPath(path);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteByPath_rootPath_throwException() {
        //given
        String path = "/";
        //when-then
        assertThatThrownBy(() -> {
            underTest.deleteByPath(path);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteByPath_nonExistedPath_throwException() {
        //given
        String path = "/123";
        //when-then
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        assertThatThrownBy(() -> {
            underTest.deleteByPath(path);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteByPath_existedPath_returnDeletedDirectory() {
        //given
        String path = "/" + childDirectory.getName();
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        when(fileMapper.toDTO(childDirectory)).thenCallRealMethod();
        FileDTO actual = underTest.deleteByPath(path);
        //then
        assertThat(actual.getId()).isEqualTo(childDirectory.getId());
    }

    @Test
    void deleteByPath_validComplicatedPath_returnDeletedDirectory() {
        //given
        String path = "/" + childDirectory.getName() + "/" + complexPathChildDirectory.getName();
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        when(fileMapper.toDTO(complexPathChildDirectory)).thenCallRealMethod();

        FileDTO actual = underTest.deleteByPath(path);

        //then
        assertThat(actual.getId()).isEqualTo(complexPathChildDirectory.getId());
    }

    @Test
    void moveFile_emptySourcePath_throwException() {
        //given
        String sourcePath = "";
        String destinationPath = "/" + childDirectory.getName();
        //when-then
        assertThatThrownBy(() -> {
            underTest.moveFile(sourcePath, destinationPath);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void moveFile_emptyDestinationPath_throwException() {
        //given
        String sourcePath = "/" + childDirectory.getName();
        ;
        String destinationPath = "";
        //when-then
        assertThatThrownBy(() -> {
            underTest.moveFile(sourcePath, destinationPath);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void moveFile_destinationPathIsSubSourcePath_throwException() {
        //given
        String sourcePath = "/" + childDirectory.getName();
        String destinationPath = "/" + childDirectory.getName() + "/" + complexPathChildDirectory.getName();
        //when-then
        assertThatThrownBy(() -> {
            underTest.moveFile(sourcePath, destinationPath);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void moveFile_invalidSourcePath_throwException() {
        //given
        String sourcePath = "/123";
        String destinationPath = "/" + childDirectory.getName() + "/" + complexPathChildDirectory.getName();
        //when-then
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        assertThatThrownBy(() -> {
            underTest.moveFile(sourcePath, destinationPath);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void moveFile_invalidDestinationPath_throwException() {
        //given
        String sourcePath = "/" + childDirectory.getName() + "/" + complexPathChildDirectory.getName();
        String destinationPath = "/123";
        //when-then
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        assertThatThrownBy(() -> {
            underTest.moveFile(sourcePath, destinationPath);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void moveFile_validPaths_returnFileEntity() {
        //given
        String sourcePath = "/" + childDirectory.getName() + "/" + complexPathChildDirectory.getName();
        String destinationPath = "/";
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        when(fileMapper.toDTO(complexPathChildDirectory)).thenCallRealMethod();
        FileDTO actual = underTest.moveFile(sourcePath, destinationPath);
        //then
        assertThat(actual.getId()).isEqualTo(complexPathChildDirectory.getId());
    }

    @Test
    void moveFile_duplicateName_returnFileEntity() {
        //given
        String sourcePath = "/" + childDirectory.getName() + "/" + duplidateNameChildDirectory.getName();
        String destinationPath = "/";
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        //then
        assertThatThrownBy(() -> {
            underTest.moveFile(sourcePath, destinationPath);
        }).isInstanceOf(IllegalArgumentException.class);
    }


}

