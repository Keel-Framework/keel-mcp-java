#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model.errors.enums;

public final class ExemploErrorsMsg {

    public static final String HTTP_CLIENT_EXCEPTION_MSG = "Transmiting 4XX error from URL: {}. Cause: {}. Message: {}";
    public static final String HTTP_SERVER_EXCEPTION_MSG = "Transmiting 5XX error from URL: {}. Cause: {}. Message: {}";
    public static final String REST_CLIENT_EXCEPTION_MSG = "Error invoking URL: {}. Cause: {}. Message: {}";
    public static final String URI_SYNTAX_EXCEPTION_MSG = "Invalid URL: {}. Cause: {}. Message: {}";
    public static final String UNHANDLED_EXCEPTION_MSG =
            "An Unhandled Exception has occurred when trying to contact URL: {}. Cause: {}. Message: {}";

    private ExemploErrorsMsg(){}

}