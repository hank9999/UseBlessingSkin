package com.github.hank9999.useblessingskin.shared;

public enum ErrorCode {
    ROLE_NOT_EXIST("RoleNotExist"),
    ROLE_RESPONSE_EMPTY("RoleResponseEmpty"),
    ROLE_SKIN_NOT_EXIST("RoleSkinNotExist"),
    REQUEST_ERROR("RequestError"),
    SAVE_TEXTURE_ERROR("SaveTextureError"),
    UPLOAD_TEXTURE_ERROR("UploadTextureError"),
    UNKNOWN_ERROR("UnknownError"),
    NO_RESULT("NoResult");

    private final String configKey;

    ErrorCode(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigKey() {
        return configKey;
    }
}