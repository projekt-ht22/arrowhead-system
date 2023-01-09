package ai.aitia.snowblower.snowblower_consumer;

public class HelloConsumerConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	// The service definitions for the service that is consumed
	public static final String GET_ACCESSORY_ID_SERVICE_DEFINITION = "get-accessory-id";
	// public static final String GET_ACCESSORY_ID_URI = "/get_accessory_id";

	public static final String START_ACCESSORY_SERVICE_DEFINITION = "start-accessory";
	// public static final String START_ACCESSORY_URI = "/start_accessory";
	public static final String STOP_ACCESSORY_SERVICE_DEFINITION = "stop-accessory";
	// public static final String STOP_ACCESSORY_URI = "/stop_accessory";
	public static final String COMMAND_ACCESSORY_SERVICE_DEFINITION = "command-accessory";
	// public static final String COMMAND_ACCESSORY_URI = "/command_accessory";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private HelloConsumerConstants() {
		throw new UnsupportedOperationException();
	}

}
