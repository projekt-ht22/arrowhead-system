package ai.aitia.mission_executor.mission_executor_system;

public class MissionExecutorSystemConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	public static final String DO_MISSION_SERVICE_DEFINITION = "do-mission";
	public static final String DO_MISSION_URI = "/do_mission";
	public static final String TASK_DONE_SERVICE_DEFINITION = "task-done";
	public static final String TASK_DONE_URI = "/task_done";


	public static final String EXECUTOR_READY_SERVICE_DEFINITION =  "executor-ready";
	public static final String GO_TO_POINT_SERVICE_DEFINITION = "go-to-point";
	public static final String FOLLOW_PATH_SERVICE_DEFINITION = "follow-path";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private MissionExecutorSystemConstants() {
		throw new UnsupportedOperationException();
	}
}
