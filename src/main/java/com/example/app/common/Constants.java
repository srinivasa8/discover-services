package com.example.app.common;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public static final List<String> ACCEPTABLE_SERVICE_LIST = new ArrayList<>();
    public static final String INVALID_INPUT_MESSAGE = "Input contains invalid value! Please use both or any one of the following : ";
    public static final String INVALID_INPUT_ERROR= "Invalid input";
    public static final String MISSING_PARAMETER_ERROR = "Missing parameter";

    public static final String MISSING_PARAMETER_ERROR_MESSAGE = "Required request parameter is not present! If you are using any reserved characters like % "
            + "Please use in their encoded form. For example if you are using \"n%\" use \"n25%\"";

    static {
        ACCEPTABLE_SERVICE_LIST.add("EC2");
        ACCEPTABLE_SERVICE_LIST.add("S3");
    }
}
