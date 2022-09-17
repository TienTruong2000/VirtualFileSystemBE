package org.tientt.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.tientt.constants.MessageConstant;
import org.tientt.models.entities.FileEntity;
import org.tientt.repositories.FileRepository;
import org.tientt.utils.MessageUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileService {

    @Autowired
    private FileRepository fileRepository;

    private final Pattern fileNamePattern = Pattern.compile("^[a-zA-Z0-9 _-]+$");

    public int findChildIndexByName(FileEntity parent, String childName) {
        if (parent.getChildren() == null || parent.getChildren().isEmpty())
            return -1;
        for (int i = 0; i < parent.getChildren().size(); i++) {
            FileEntity child = parent.getChildren().get(i);
            if (child.getName().equals(childName)) return i;
        }
        return -1;
    }

    public void validateDirectoryName(String name) {
        Matcher matcher = fileNamePattern.matcher(name);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.INVALID_NAME));
        }
    }

    public void validateDuplicateName(FileEntity parentDirectory, String name) {
        int childIndex = findChildIndexByName(parentDirectory, name);
        if (childIndex != -1) {
            throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.DUPLICATED_NAME));
        }
    }

    public FileEntity getNearestParentElement(String[] pathElements) {
        FileEntity parentDirectory = fileRepository.getRootDirectory();
        for (int i = 1; i < pathElements.length - 1; i++) {
            String pathElement = pathElements[i];
            int childIndex = findChildIndexByName(parentDirectory, pathElement);
            if (childIndex == -1) {
                throw new IllegalArgumentException(MessageUtil.getMessage(MessageConstant.File.MISSING_PARENT));
            }
            parentDirectory = parentDirectory.getChildren().get(childIndex);
        }
        return parentDirectory;
    }
}
