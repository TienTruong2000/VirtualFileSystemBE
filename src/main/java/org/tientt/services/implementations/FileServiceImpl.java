package org.tientt.services.implementations;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tientt.constants.MessageConstant;
import org.tientt.models.dtos.FileDTO;
import org.tientt.models.entities.FileEntity;
import org.tientt.models.entities.FileType;
import org.tientt.repositories.FileRepository;
import org.tientt.services.interfaces.FileService;
import org.tientt.services.mappers.FileMapper;
import org.tientt.utils.MessageUtil;

import javax.validation.constraints.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
@Primary
@Setter
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRepository fileRepository;

    @Value("${file.path.separator}")
    private String PATH_SEPARATOR;

    @Autowired
    private FileMapper fileMapper;

    private final Pattern fileNamePattern = Pattern.compile("^[a-zA-Z0-9 _-]+$");

    protected int findChildIndexByName(@NotNull FileEntity parent, String childName) {
        if (parent.getChildren() == null || parent.getChildren().isEmpty())
            return -1;
        for (int i = 0; i < parent.getChildren().size(); i++) {
            FileEntity child = parent.getChildren().get(i);
            if (child.getName().equals(childName)) return i;
        }
        return -1;
    }

    protected void validateDirectoryName(String name) {
        Matcher matcher = fileNamePattern.matcher(name);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.INVALID_NAME));
        }
    }

    protected void validateDuplicateName(FileEntity parentDirectory, String name) {
        int childIndex = findChildIndexByName(parentDirectory, name);
        if (childIndex != -1) {
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.DUPLICATED_NAME));
        }
    }

    protected FileEntity getNearestParentElement(String[] pathElements) {
        //if path begins with / then the first element of pathElements must be ""
        if (pathElements.length == 0 || !pathElements[0].equals("")) {
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.PATH_NOT_FOUND));
        }
        FileEntity parentDirectory = fileRepository.getRootDirectory();

        for (int i = 1; i < pathElements.length - 1; i++) {
            String pathElement = pathElements[i];
            int childIndex = findChildIndexByName(parentDirectory, pathElement);
            if (childIndex == -1) {
                throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.PATH_NOT_FOUND));
            }
            parentDirectory = parentDirectory.getChildren().get(childIndex);
            if (parentDirectory.getType() != FileType.DIRECTORY && parentDirectory.getType() != FileType.ROOT){
                throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.FILE_IS_NOT_DIRECTORY));
            }
        }
        return parentDirectory;
    }

    protected FileEntity getFileFromPath(String path) {
        if (path == null || path.isEmpty())
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.EMPTY_PATH));
        if (path.equals("/")) return fileRepository.getRootDirectory();

        String[] pathElements = path.split(PATH_SEPARATOR);
        //if path begins with / then the first element of pathElements must be ""
        if (pathElements.length == 0 || !pathElements[0].equals("")) {
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.PATH_NOT_FOUND));
        }
        FileEntity file = fileRepository.getRootDirectory();
        for (int i = 1; i < pathElements.length; i++) {
            String pathElement = pathElements[i];
            int childIndex = findChildIndexByName(file, pathElement);
            if (childIndex == -1) {
                throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.PATH_NOT_FOUND));
            }
            file = file.getChildren().get(childIndex);
        }
        return file;
    }

    @Override
    @Transactional
    public FileDTO deleteByPath(String path) {
        if (path == null || path.isEmpty())
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.EMPTY_PATH));
        //separate path
        if (path.equals("/")) throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.DELETE_ROOT));
        FileEntity file = getFileFromPath(path);
        if (file.getType() == FileType.ROOT)
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.DELETE_ROOT));
        fileRepository.delete(file);
        return fileMapper.toDTO(file);
    }

    @Override
    @Transactional
    public FileDTO moveFile(String sourcePath, String destinationPath) {
        if (sourcePath == null || sourcePath.isEmpty())
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.SOURCE_PATH_NOT_FOUND));
        if (destinationPath == null || destinationPath.isEmpty()) {
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.DESTINATION_PATH_NOT_FOUND));
        }
        if (destinationPath.contains(sourcePath))
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.DESTINATION_PATH_IS_SUB_SOURCE_PATH));

        FileEntity sourceFile;
        FileEntity destinationFile;
        try {
            sourceFile = getFileFromPath(sourcePath);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.SOURCE_PATH_NOT_FOUND));
        }
        try {
            destinationFile = getFileFromPath(destinationPath);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.DESTINATION_PATH_NOT_FOUND));
        }

        if (destinationFile.getType() != FileType.DIRECTORY && destinationFile.getType() != FileType.ROOT) {
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.DESTINATION_PATH_IS_NOT_DIRECTORY));
        }
        sourceFile.setParent(destinationFile);
        return fileMapper.toDTO(sourceFile);
    }
}
