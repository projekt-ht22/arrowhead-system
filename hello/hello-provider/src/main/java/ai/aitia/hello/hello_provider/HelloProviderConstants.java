package ai.aitia.hello.hello_provider;

public class HelloProviderConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String HELLO_SERVICE_DEFINITION = "hello";
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	public static final String HELLO_URI = "/hello";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private HelloProviderConstants() {
		throw new UnsupportedOperationException();
	}
}
