package org.tientt.constants;

public final class MessageConstant {
    private MessageConstant() {
    }

    public static final class Exception {
        private Exception() {
        }

        public static final String INTERNAL_ERROR = "error.exception.internal-error";
    }

    public static final class File {
        private File() {
        }

        public static final String EMPTY_PATH = "error.file.empty-path";
        public static final String INVALID_NAME = "error.file.invalid-name";
        public static final String DUPLICATED_NAME = "error.file.duplicated-name";
        public static final String PATH_NOT_FOUND = "error.file.path-not-found";
        public static final String DESTINATION_PATH_IS_SUB_SOURCE_PATH = "error.file.destination-path-is-sub-source-path";
        public static final String DESTINATION_PATH_IS_NOT_DIRECTORY = "error.file.destination-path-is-not-directory";
        public static final String FILE_IS_NOT_TEXT_FILE = "error.file.file-is-not-text-file";
        public static final String FILE_IS_NOT_DIRECTORY = "error.file.file-is-not-directory";
        public static final String SOURCE_PATH_NOT_FOUND = "error.file.source-path-not-found";
        public static final String DESTINATION_PATH_NOT_FOUND = "error.file.destination-path-not-found";
        public static final String DELETE_ROOT = "error.file.delete-root";

    }




}
