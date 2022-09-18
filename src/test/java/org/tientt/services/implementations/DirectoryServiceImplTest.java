package org.tientt.services.implementations;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.tientt.constants.MessageConstant;
import org.tientt.models.entities.FileEntity;
import org.tientt.models.entities.FileType;
import org.tientt.repositories.FileRepository;
import org.tientt.services.mappers.FileMapper;
import org.tientt.utils.MessageUtil;

import java.time.Clock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DirectoryServiceImplTest {

    private DirectoryServiceImpl underTest;
    private AutoCloseable autoCloseable;
    @Mock
    private FileRepository fileRepository;

    @Mock
    private FileMapper fileMapper;

    @Mock
    private Clock clock;

    @Mock
    private SnowflakeIdGenerator snowflakeIdGenerator;

    private FileEntity rootEntity = new FileEntity();


    @BeforeEach
    @Transactional
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


    }

    @AfterEach
    void tearDown() throws Exception {
        fileRepository.deleteAll();
        autoCloseable.close();
    }

    @Test
    void createDirectorySuccess() {
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        underTest.create("/boolean", false);
        ArgumentCaptor<FileEntity> fileEntityArgumentCaptor = ArgumentCaptor.forClass(FileEntity.class);
        verify(fileRepository).save(fileEntityArgumentCaptor.capture());

        FileEntity capturedDirectory = fileEntityArgumentCaptor.getValue();
        assertThat(capturedDirectory.getType()).isEqualTo(FileType.DIRECTORY);
    }

    @Test
    void createDirectoryWrongPath() {
        when(fileRepository.getRootDirectory()).thenReturn(rootEntity);
        assertThatThrownBy(() -> underTest.create("/boolean/abc", false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching(MessageUtil.getMessage(MessageConstant.File.PATH_NOT_FOUND));
    }

    @Test
    void getById() {
        underTest.getById(123);
        verify(fileRepository).findById(123L);
    }

    @Test
    @Disabled
    void getByPath() {
    }

    @Test
    @Disabled
    void getAll() {
    }

    @Test
    @Disabled
    void move() {
    }

    @Test
    @Disabled
    void deleteFromPath() {
    }
}