package ai.aitia.navigator.navigator_system;

public class NavigatorSystemConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	public static final String GO_TO_POINT_SERVICE_DEFINITION = "go-to-point";
	public static final String GO_TO_POINT_URI= "/go_to_point";
	public static final String STOP_SERVICE_DEFINITION = "stop";
	public static final String STOP_URI= "/go_to_point";
	public static final String FOLLOW_PATH_SERVICE_DEFINITION = "follow-path";
	public static final String FOLLOW_PATH_URI= "/follow_path";

	public static final String GET_GPS_CORDINATES_SERVICE_DEFINITION = "get-gps-cordinates";
	public static final String GET_GPS_HEADING_SERVICE_DEFINITION = "get-gps-heading";
	public static final String SET_TRACK_SPEED_SERVICE_DEFINITION = "set-track-speed";
	public static final String GET_GPS_ACCURACY_SERVICE_DEFINITION = "get-gps-accuracy";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private NavigatorSystemConstants() {
		throw new UnsupportedOperationException();
	}
}
