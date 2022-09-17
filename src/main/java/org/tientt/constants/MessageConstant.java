package org.tientt.constants;

public final class MessageConstant {
    private MessageConstant() {}

    public static final class Exception {
        private Exception() {}
        public static final String INTERNAL_ERROR = "error.exception.internal-error";
    }

    public static final class Directory {
        private Directory() {}
        public static final String EMPTY_PATH = "error.directory.empty-path";
        public static final String MISSING_PARENT = "error.directory.missing-parent";
        public static final String INVALID_NAME = "error.directory.invalid-name";
        public static final String DUPLICATED_NAME = "error.directory.duplicated-name";
    }

}
