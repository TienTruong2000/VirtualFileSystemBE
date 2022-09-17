package org.tientt.services.implementations;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tientt.constants.MessageConstant;
import org.tientt.models.dtos.FileDTO;
import org.tientt.models.entities.FileEntity;
import org.tientt.models.entities.FileType;
import org.tientt.repositories.FileRepository;
import org.tientt.services.interfaces.TextFileService;
import org.tientt.services.mappers.FileMapper;
import org.tientt.utils.MessageUtil;

import java.time.Clock;

@Service
@Transactional(readOnly = true)
public class TextFileServiceImpl extends FileService implements TextFileService {

    @Value("${file.path.separator}")
    private String PATH_SEPARATOR;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    private Clock clock;

    @Autowired
    private FileMapper fileMapper;

    @Override
    @Transactional
    public FileDTO create(String path, String content) {
        if (path == null || path.isEmpty())
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.EMPTY_PATH));
        //separate path
        String[] pathElements = path.split(PATH_SEPARATOR);
        if (pathElements.length == 0) {
            //the only time this happens is when the path is "/"
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.INVALID_NAME));
        }

        String directoryName = pathElements[pathElements.length - 1].trim();
        FileEntity parentDirectory = getNearestParentElement(pathElements);
        validateDirectoryName(directoryName);
        validateDuplicateName(parentDirectory, directoryName);

        //create new directory
        FileEntity newFile = new FileEntity();
        newFile.setId(snowflakeIdGenerator.next());
        newFile.setCreatedAt(clock.millis());
        newFile.setUpdatedAt(clock.millis());
        newFile.setType(FileType.REGULAR_FILE);
        newFile.setParent(parentDirectory);
        newFile.setName(directoryName);
        newFile.setContent(content);
        newFile = fileRepository.save(newFile);

        return fileMapper.toDTO(newFile);
    }
}
