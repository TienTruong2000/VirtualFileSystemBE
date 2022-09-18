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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TextFileServiceImplTest {
    private TextFileServiceImpl underTest;
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

    private final FileEntity complexPathChildTextFile = new FileEntity();

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new TextFileServiceImpl();
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
        rootEntity.setChildren(List.of(childDirectory, complexPathChildTextFile));

        childDirectory.setCreatedAt(1L);
        childDirectory.setId(123L);
        childDirectory.setName("Name");
        childDirectory.setParent(rootEntity);
        childDirectory.setType(FileType.DIRECTORY);
        childDirectory.setUpdatedAt(1L);

        complexPathChildTextFile.setCreatedAt(1L);
        complexPathChildTextFile.setId(123L);
        complexPathChildTextFile.setName("Complex name");
        complexPathChildTextFile.setParent(rootEntity);
        complexPathChildTextFile.setType(FileType.TEXT_FILE);
        complexPathChildTextFile.setUpdatedAt(1L);


    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void create_simplePath_createNewDirectory() {
        //given
        String path = "/new";
        String expectedName = "new";
        String content = "New file";
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        underTest.create(path, content);
        ArgumentCaptor<FileEntity> fileEntityArgumentCaptor = ArgumentCaptor.forClass(FileEntity.class);
        //then
        verify(fileRepository).save(fileEntityArgumentCaptor.capture());
        FileEntity capturedFile = fileEntityArgumentCaptor.getValue();
        assertThat(capturedFile.getType()).isEqualTo(FileType.TEXT_FILE);
        assertThat(capturedFile.getName()).isEqualTo(expectedName);
    }

    @Test
    void create_pathWithWhiteSpace_createNewDirectory() {
        //given
        String path = "/new dir";
        String expectedName = "new dir";
        String content = "New file";
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        underTest.create(path, content);
        ArgumentCaptor<FileEntity> fileEntityArgumentCaptor = ArgumentCaptor.forClass(FileEntity.class);
        //then
        verify(fileRepository).save(fileEntityArgumentCaptor.capture());
        FileEntity capturedDirectory = fileEntityArgumentCaptor.getValue();
        assertThat(capturedDirectory.getType()).isEqualTo(FileType.TEXT_FILE);
        assertThat(capturedDirectory.getName()).isEqualTo(expectedName);
    }

    @Test
    void create_invalidPath_throwException() {
        //given
        String path = "/new/abc";
        String content = "New file";
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        assertThatThrownBy(() -> underTest.create(path, content))
                //then
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching(MessageUtil.getMessage(MessageConstant.File.PATH_NOT_FOUND));
    }

    @Test
    void create_emptyPath_throwException() {
        //given
        String path = "";
        String content = "New file";
        //when
        assertThatThrownBy(() -> underTest.create(path, content))
                //then
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching(MessageUtil.getMessage(MessageConstant.File.EMPTY_PATH));

    }

    @Test
    void create_rootPath_throwException() {
        //given
        String path = "/";
        String content = "New file";
        //when
        assertThatThrownBy(() -> underTest.create(path, content))
                //then
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching(MessageUtil.getMessage(MessageConstant.File.INVALID_NAME));
    }
    

    @Test
    void getFileByPath_existedPath_getDirectory() {
        //given
        String path = "/" + complexPathChildTextFile.getName();
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        when(fileMapper.toDTO(complexPathChildTextFile)).thenCallRealMethod();
        FileDTO actual = underTest.getFileByPath(path);
        //then
        assertThat(actual.getId()).isEqualTo(childDirectory.getId());
    }

    @Test
    void getFileByPath_invalidPath_getDirectory() {
        //given
        String path = "/123";

        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        assertThatThrownBy(() -> underTest.getFileByPath(path))
                //then
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getFileByPath_emptyPath_getDirectory() {
        //given
        String path = "";

        //when
        assertThatThrownBy(() -> underTest.getFileByPath(path))
                //then
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getFileByPath_rootPath_getDirectory() {
        //given
        String path = "/";
        //when-then
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        assertThatThrownBy(() -> underTest.getFileByPath(path)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getFileByPath_complexPath_getDirectory() {
        //given
        String path = "/" + complexPathChildTextFile.getName();
        //when
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        when(fileMapper.toDTO(complexPathChildTextFile)).thenCallRealMethod();
        FileDTO actual = underTest.getFileByPath(path);
        //then
        assertThat(actual.getId()).isEqualTo(complexPathChildTextFile.getId());
    }
}
