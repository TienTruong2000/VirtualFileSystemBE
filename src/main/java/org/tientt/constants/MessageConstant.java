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
        public static final String MISSING_PARENT = "error.file.missing-parent";
        public static final String INVALID_NAME = "error.file.invalid-name";
        public static final String DUPLICATED_NAME = "error.file.duplicated-name";
    }


}
