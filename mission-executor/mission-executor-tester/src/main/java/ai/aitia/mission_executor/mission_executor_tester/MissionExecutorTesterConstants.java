package ai.aitia.mission_executor.mission_executor_tester;

public class MissionExecutorTesterConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	// The service definitions for the service that is consumed
	public static final String DO_MISSION_SERVICE_DEFINITION = "do-mission";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private MissionExecutorTesterConstants() {
		throw new UnsupportedOperationException();
	}

}
