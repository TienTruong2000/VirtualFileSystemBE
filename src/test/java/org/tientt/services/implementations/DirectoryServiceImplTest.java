package org.tientt.services.implementations;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.tientt.constants.MessageConstant;
import org.tientt.models.dtos.FileDTO;
import org.tientt.models.entities.FileEntity;
import org.tientt.models.entities.FileType;
import org.tientt.repositories.FileRepository;
import org.tientt.services.mappers.FileMapper;
import org.tientt.services.mappers.FileMapperImpl;
import org.tientt.utils.MessageUtil;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DirectoryServiceImplTest {

    private DirectoryServiceImpl underTest;
    private AutoCloseable autoCloseable;
    @Mock
    private FileRepository fileRepository;

    @Mock
    private FileMapperImpl fileMapper;

    @Mock
    private Clock clock;

    @Mock
    private SnowflakeIdGenerator snowflakeIdGenerator;

    private final FileEntity rootEntity = new FileEntity();

    private final FileEntity childDirectory = new FileEntity();

    private final FileEntity complexPathChildDirectory = new FileEntity();

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new DirectoryServiceImpl();
        underTest.setFileRepository(fileRepository);
        underTest.setFileMapper(fileMapper);
        underTest.setClock(clock);
        underTest.setSnowflakeIdGenerator(snowflakeIdGenerator);
        String PATH_SEPARATOR = "/";
        underTest.setPATH_SEPARATOR(PATH_SEPARATOR);
        ReflectionTestUtils.setField(underTest, FileServiceImpl.class, "fileRepository", fileRepository, FileRepository.class);
        ReflectionTestUtils.setField(underTest, FileServiceImpl.class, "fileMapper", fileMapper, FileMapper.class);
        ReflectionTestUtils.setField(underTest, FileServiceImpl.class, "PATH_SEPARATOR", PATH_SEPARATOR, String.class);

        rootEntity.setType(FileType.ROOT);
        rootEntity.setId(1);
        rootEntity.setName("root");
        rootEntity.setCreatedAt(0);
        rootEntity.setUpdatedAt(0);
        rootEntity.setChildren(List.of(childDirectory, complexPathChildDirectory));

        childDirectory.setCreatedAt(1L);
        childDirectory.setId(123L);
        childDirectory.setName("Name");
        childDirectory.setParent(rootEntity);
        childDirectory.setType(FileType.DIRECTORY);
        childDirectory.setUpdatedAt(1L);

        complexPathChildDirectory.setCreatedAt(1L);
        complexPathChildDirectory.setId(123L);
        complexPathChildDirectory.setName("Complex name");
        complexPathChildDirectory.setParent(rootEntity);
        complexPathChildDirectory.setType(FileType.DIRECTORY);
        complexPathChildDirectory.setUpdatedAt(1L);
    }

    @AfterEach
    void tearDown() throws Exception {
        fileRepository.deleteAll();
        autoCloseable.close();
    }

    @Test
    void create_simplePath_createNewDirectory() {
        //given
        String path = "/new";
        String expectedName = "new";
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        underTest.create(path, false);
        ArgumentCaptor<FileEntity> fileEntityArgumentCaptor = ArgumentCaptor.forClass(FileEntity.class);
        //then
        verify(fileRepository).save(fileEntityArgumentCaptor.capture());
        FileEntity capturedDirectory = fileEntityArgumentCaptor.getValue();
        assertThat(capturedDirectory.getType()).isEqualTo(FileType.DIRECTORY);
        assertThat(capturedDirectory.getName()).isEqualTo(expectedName);
    }

    @Test
    void create_pathWithWhiteSpace_createNewDirectory() {
        //given
        String path = "/new dir";
        String expectedName = "new dir";
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        underTest.create(path, false);
        ArgumentCaptor<FileEntity> fileEntityArgumentCaptor = ArgumentCaptor.forClass(FileEntity.class);
        //then
        verify(fileRepository).save(fileEntityArgumentCaptor.capture());
        FileEntity capturedDirectory = fileEntityArgumentCaptor.getValue();
        assertThat(capturedDirectory.getType()).isEqualTo(FileType.DIRECTORY);
        assertThat(capturedDirectory.getName()).isEqualTo(expectedName);
    }

    @Test
    void create_invalidPath_throwException() {
        //given
        String path = "/new/abc";
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        assertThatThrownBy(() -> underTest.create(path, false))
                //then
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching(MessageUtil.getMessage(MessageConstant.File.PATH_NOT_FOUND));
    }

    @Test
    void create_emptyPath_throwException() {
        //given
        String path = "";
        //when
        assertThatThrownBy(() -> underTest.create(path, false))
                //then
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching(MessageUtil.getMessage(MessageConstant.File.EMPTY_PATH));

    }

    @Test
    void create_rootPath_throwException() {
        //given
        String path = "/";
        //when
        assertThatThrownBy(() -> underTest.create(path, false))
                //then
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching(MessageUtil.getMessage(MessageConstant.File.INVALID_NAME));
    }


    @Test
    void getById_existedId_getDirectory() {
        //given
        when(fileRepository.findById(childDirectory.getId())).thenReturn(Optional.of(childDirectory));
        when(fileMapper.toDTO(childDirectory)).thenCallRealMethod();
        //when
        FileDTO actual = underTest.getById(childDirectory.getId());
        //then
        verify(fileRepository).findById(childDirectory.getId());
        assertThat(actual.getName()).isEqualTo(childDirectory.getName());
        assertThat(actual.getId()).isEqualTo(childDirectory.getId());
    }

    @Test
    void getById_nonExistedId_getDirectory() {
        //when
        FileDTO actual = underTest.getById(135);
        //then
        verify(fileRepository).findById(135L);
        assertThat(actual).isNull();
    }

    @Test
    void getByPath_existedPath_getDirectory() {
        //given
        String path = "/" + childDirectory.getName();
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        when(fileMapper.toDTO(childDirectory)).thenCallRealMethod();
        FileDTO actual = underTest.getByPath(path);
        //then
        assertThat(actual.getId()).isEqualTo(childDirectory.getId());
    }

    @Test
    void getByPath_invalidPath_getDirectory() {
        //given
        String path = "/123";

        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        assertThatThrownBy(() -> underTest.getByPath(path))
                //then
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getByPath_emptyPath_getDirectory() {
        //given
        String path = "";

        //when
        assertThatThrownBy(() -> underTest.getByPath(path))
                //then
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getByPath_rootPath_getDirectory() {
        //given
        String path = "/";
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        when(fileMapper.toDTO(rootEntity)).thenCallRealMethod();
        FileDTO actual = underTest.getByPath(path);
        //then
        assertThat(actual.getId()).isEqualTo(rootEntity.getId());
    }

    @Test
    void getByPath_complexPath_getDirectory() {
        //given
        String path = "/" + complexPathChildDirectory.getName();
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        when(fileMapper.toDTO(complexPathChildDirectory)).thenCallRealMethod();
        FileDTO actual = underTest.getByPath(path);
        //then
        assertThat(actual.getId()).isEqualTo(complexPathChildDirectory.getId());
    }
}