package ai.aitia.mission_scheduler.mission_scheduler_provider;

public class MissionSchedulerProviderConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String ADD_MISSION_SERVICE_DEFINITION = "add-mission";
	public static final String ADD_MISSION_URI = "/add_mission";

	public static final String GET_NEXT_MISSION_SERVICE_DEFINITION = "get-next-mission";
	public static final String GET_NEXT_MISSION_URI = "/get_next_mission";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private MissionSchedulerProviderConstants() {
		throw new UnsupportedOperationException();
	}
}
