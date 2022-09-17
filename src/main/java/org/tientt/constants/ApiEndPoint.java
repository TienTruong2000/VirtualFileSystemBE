package org.tientt.constants;

public final class ApiEndPoint {
    private ApiEndPoint() {
    }

    public static final String ROOT_ENDPOINT = "/api";
    public static final String VERSION = "/v1";
    public static final String API_ENDPOINT = ROOT_ENDPOINT + VERSION;

    public static final class File {
        private File(){}
        public static final String FILE_END_POINT = API_ENDPOINT + "/files";
    }

    public static final class Directory {
        private Directory(){}

        public static final String DIRECTORY_END_POINT = API_ENDPOINT + "/directories";
    }
}
