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
import org.tientt.services.interfaces.DirectoryService;
import org.tientt.services.mappers.FileMapper;
import org.tientt.utils.MessageUtil;

import java.time.Clock;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DirectoryServiceImpl extends FileServiceImpl implements DirectoryService {

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
    public FileDTO create(String path, boolean isCreateParent) {
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
        FileEntity newDirectory = new FileEntity();
        newDirectory.setId(snowflakeIdGenerator.next());
        newDirectory.setCreatedAt(clock.millis());
        newDirectory.setUpdatedAt(clock.millis());
        newDirectory.setType(FileType.DIRECTORY);
        newDirectory.setParent(parentDirectory);
        newDirectory.setName(directoryName);
        newDirectory = fileRepository.save(newDirectory);

        return fileMapper.toDTO(newDirectory);
    }

    @Override
    public FileDTO getById(long id) {
        return fileRepository.findById(id).map(fileMapper::toDTO).orElse(null);
    }

    @Override
    public FileDTO getByPath(String path) {
        if (path == null || path.isEmpty())
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.EMPTY_PATH));
        //separate path
        String[] pathElements = path.split(PATH_SEPARATOR);
        if (pathElements.length == 0) {
            //the only time this happens is when the path is "/"
            return fileMapper.toDTO(fileRepository.getRootDirectory());
        }

        String directoryName = pathElements[pathElements.length - 1].trim();
        FileEntity parentDirectory = getNearestParentElement(pathElements);
        int index = findChildIndexByName(parentDirectory, directoryName);
        if (index == -1)
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.PATH_NOT_FOUND));
        return fileMapper.toDTO(parentDirectory.getChildren().get(index));
    }

    @Override
    public List<FileDTO> getAll() {
        return null;
    }

    @Override
    public FileDTO move(String sourcePath, String destinationPath) {
        return null;
    }

    @Override
    public FileDTO deleteFromPath(String path) {
        return null;
    }
}
