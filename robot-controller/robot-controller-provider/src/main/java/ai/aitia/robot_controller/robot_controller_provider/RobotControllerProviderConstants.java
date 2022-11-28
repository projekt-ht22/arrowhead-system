package ai.aitia.robot_controller.robot_controller_provider;

public class RobotControllerProviderConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String SET_TRACK_SPEED_SERVICE_DEFINITION = "set-track-speed";
	public static final String SET_TRACK_SPEED_URI = "/set_track_speed";

	public static final String SET_TILT_AMOUNT_SERVICE_DEFINITION = "set-tilt-amount";
	public static final String SET_TILT_AMOUNT_URI = "/set_tilt_amount";

	public static final String ADD_MESSAGE_SERVICE_DEFINITION = "add-message";
	public static final String ADD_MESSAGE_URI = "/add_message";

	public static final String GET_MESSAGE_SERVICE_DEFINITION = "get-message";
	public static final String GET_MESSAGE_URI = "/get_message";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private RobotControllerProviderConstants() {
		throw new UnsupportedOperationException();
	}
}
