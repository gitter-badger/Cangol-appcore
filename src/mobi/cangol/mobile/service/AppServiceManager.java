package mobi.cangol.mobile.service;

public abstract class AppServiceManager {
	
	public static final String CONFIG_SERVICE = "config";
	
	public static final String UPGRADE_SERVICE = "upgrade";
	
	public static final String DOWNLOAD_SERVICE = "download";
	
	public static final String STAT_SERVICE = "stat";
	
	public abstract AppService getAppService(String name);
	
	public abstract void destoryService(String name);
	
	public abstract void destoryAllService();
	
	public abstract void setScanPackage(String ... packageName);
}
