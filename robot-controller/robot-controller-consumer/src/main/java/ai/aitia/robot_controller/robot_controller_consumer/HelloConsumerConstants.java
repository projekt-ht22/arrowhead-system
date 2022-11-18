package ai.aitia.robot_controller.robot_controller_consumer;

public class HelloConsumerConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	// The service definitions for the service that is consumed
	public static final String ADD_MESSAGE_SERVICE_DEFINITION = "add-message";
	public static final String GET_MESSAGE_SERVICE_DEFINITION = "get-message";
	public static final String SET_TILT_AMOUNT_SERVICE_DEFINITION = "set-tilt-amount";
	public static final String SET_TRACK_SPEED_SERVICE_DEFINITION = "set-track-speed";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private HelloConsumerConstants() {
		throw new UnsupportedOperationException();
	}

}
