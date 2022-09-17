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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
public class DirectoryServiceImpl implements DirectoryService {

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
    private final Pattern fileNamePattern = Pattern.compile("^[a-zA-Z0-9 _-]+$");

    private int findChildIndexByName(FileEntity parent, String childName) {
        if (parent.getChildren() == null || parent.getChildren().isEmpty())
            return -1;
        for (int i = 0; i < parent.getChildren().size(); i++) {
            FileEntity child = parent.getChildren().get(i);
            if (child.getName().equals(childName)) return i;
        }
        return -1;
    }

    private void validateDirectoryName(String name) {
        Matcher matcher = fileNamePattern.matcher(name);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.Directory.INVALID_NAME));
        }
    }

    private void validateDuplicateName(FileEntity parentDirectory, String name) {
        int childIndex = findChildIndexByName(parentDirectory, name);
        if (childIndex != -1) {
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.Directory.DUPLICATED_NAME));
        }
    }

    private FileEntity getNearestParentElement(String[] pathElements) {
        FileEntity parentDirectory = fileRepository.getRootDirectory();
        for (int i = 1; i < pathElements.length - 1; i++) {
            String pathElement = pathElements[i];
            int childIndex = findChildIndexByName(parentDirectory, pathElement);
            if (childIndex == -1) {
                throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.Directory.MISSING_PARENT));
            }
            parentDirectory = parentDirectory.getChildren().get(childIndex);
        }
        return parentDirectory;
    }

    @Override
    @Transactional
    public FileDTO create(String path, boolean isCreateParent) {
        if (path == null || path.isEmpty())
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.Directory.EMPTY_PATH));
        //separate path
        String[] pathElements = path.split(PATH_SEPARATOR);
        if (pathElements.length == 0) {
            //the only time this happens is when the path is "/"
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.Directory.INVALID_NAME));
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
