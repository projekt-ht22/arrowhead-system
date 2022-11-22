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
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private NavigatorSystemConstants() {
		throw new UnsupportedOperationException();
	}
}
