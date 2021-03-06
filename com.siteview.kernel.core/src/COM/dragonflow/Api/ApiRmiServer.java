package COM.dragonflow.Api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import jgl.Array;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import COM.dragonflow.Page.machinePage;
import COM.dragonflow.Page.monitorPage;
import COM.dragonflow.Page.ntmachinePage;
import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.Machine;
import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.SiteViewGroup;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.StandardMonitor.SNMPCPUMonitor;

public class ApiRmiServer extends java.rmi.server.UnicastRemoteObject implements
		APIInterfaces {
	String hostname;
	Registry registry;
	APIGroup apigroup;
	APIMonitor apimonitor;
	static String[] NTCounterGroups = { "SQLServerMonitor",
			"WindowsMediaMonitor", "ADPerformanceMonitor", "ASPMonitor",
			"ColdFusionMonitor", "IISServerMonitor", "RealMonitor" };
	static String[] DispatcherMonitorCounterGroups = { "OracleDBMonitor",
			"PatrolMonitor", "TuxedoMonitor" };
	static String[] MultiContentBaseCounterGroups = {
			"HealthUnixServerMonitor", "LogEventHealthMonitor",
			"MediaPlayerMonitorBase", "MonitorLoadMonitor" };
	static String[] MediaPlayerMonitorBaseCounterGroups = {
			"RealMediaPlayerMonitor", "WindowsMediaPlayerMonitor" };
	static String[] SNMPBaseCounterGroups = { "DynamoMonitor",
			"CheckPointMonitor", "WebLogic5xMonitor" };
	static String[] BrowsableSNMPBaseCounterGroups = { "BrowsableSNMPMonitor",
			"CiscoMonitor", "F5Monitor", "IPlanetAppServerMonitor",
			"IPlanetWSMonitor", "NetworkBandwidthMonitor", "VMWareMonitor" };
	static String[] ServerMonitorCounterGroups = { "AssetMonitor",
			"CPUMonitor", "DiskSpaceMonitor", "MemoryMonitor",
			"NTCounterMonitor", "NTEventLogMonitor", "ScriptMonitor",
			"ServiceMonitor", "UnixCounterMonitor", "WebServerMonitor" };
	static String[] BrowsableExeBaseCounterGroups = { "DB2Monitor",
			"SAPMonitor" };
	static String[] BrowsableBaseCounterGroups = { "BrowsableNTCounterMonitor",
			"BrowsableWMIMonitor", "DatabaseCounterMonitor", "IPMIMonitor",
			"OracleJDBCMonitor", "SiebelMonitor", "WebLogic6xMonitor",
			"WebSphereMonitor" };
	static String[] InterFaceCounterGroups = { "InterfaceMonitor" };

	public ApiRmiServer() throws RemoteException {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
		} catch (Exception e) {
			System.out.println("can't get inet address.");
		}
		int port = 3232;
		System.out.println("RMI server start at this address=" + hostname
				+ ",port=" + port);
		try {
			registry = LocateRegistry.createRegistry(port);
			registry.rebind("kernelApiRmiServer", this);

			apigroup = new APIGroup();
			apimonitor = new APIMonitor();
		} catch (RemoteException e) {
			System.out.println("remote exception" + e);
		}
	}

	public String getHostname() {
		return hostname;
	}

	public static boolean isHave(String[] strs, String s) {
		/*
		 * 此方法有两个参数，第一个是要查找的字符串数组，第二个是要查找的字符或字符串
		 */
		for (int i = 0; i < strs.length; i++) {
			if (strs[i].indexOf(s) != -1) {// 循环查找字符串数组中的每个字符串中是否包含所有查找的内容
				return true;// 查找到了就返回真，不在继续查询
			}
		}
		return false;// 没找到返回false
	}

	public List<Map<String, Object>> getAllGroupInstances()
			throws SiteViewException {
		List<MonitorGroup> mgs = apigroup.getAllGroupInstances();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (MonitorGroup mg : mgs) {
			SSInstanceProperty assinstanceproperty1[] = apigroup
					.getInstanceProperties(mg.getProperty("_id"),
							APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
			Map<String, Object> nodedata = new HashMap<String, Object>();
			for (int k = 0; k < assinstanceproperty1.length; k++) {
				nodedata.put(assinstanceproperty1[k].getName(),
						assinstanceproperty1[k].getValue());
			}

			nodedata.put("_id", mg.getFullID());
			list.add(nodedata);
		}
		return list;
	}

	public List<Map<String, Object>> getTopLevelGroupInstances()
			throws RemoteException, SiteViewException {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			APIGroup apimg = new APIGroup();
			ArrayList<MonitorGroup> mgs = apigroup.getTopLevelGroupInstances();

			for (MonitorGroup mg : mgs) {
				SSInstanceProperty assinstanceproperty1[] = apigroup
						.getInstanceProperties(mg.getProperty("_id"),
								APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
				Map<String, Object> nodedata = new HashMap<String, Object>();
				for (int k = 0; k < assinstanceproperty1.length; k++) {
					nodedata.put(assinstanceproperty1[k].getName(),
							assinstanceproperty1[k].getValue());
				}
				nodedata.put("_id", mg.getFullID());
				list.add(nodedata);
			}
		} catch (java.lang.Exception e) {
			System.out.println(e);
		}
		return list;

	}

	public List<Map<String, Object>> getChildGroupInstances(String groupID)
			throws RemoteException, SiteViewException {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			Collection mgs = apigroup.getChildGroupInstances(groupID);

			if (mgs.size() > 0) {
				for (Iterator iter = mgs.iterator(); iter.hasNext();) {
					MonitorGroup mg = (MonitorGroup) iter.next();
					SSInstanceProperty assinstanceproperty1[] = apigroup
							.getInstanceProperties(mg.getProperty("_id"),
									APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
					Map<String, Object> nodedata = new HashMap<String, Object>();
					for (int k = 0; k < assinstanceproperty1.length; k++) {
						nodedata.put(assinstanceproperty1[k].getName(),
								assinstanceproperty1[k].getValue());
					}
					nodedata.put("_id", mg.getFullID());
					list.add(nodedata);
				}
			}
		} catch (java.lang.Exception e) {
			System.out.println(e);
		}
		return list;
	}

	public void deleteGroup(String groupID) throws RemoteException,
			SiteViewException {
		apigroup.delete(groupID);
	}

	public void deleteMonitor(String monitorID, String groupID)
			throws RemoteException, SiteViewException {
		apimonitor.delete(monitorID, groupID);
	}

	public List<Map<String, Object>> getMonitorsForGroup(String groupID)
			throws RemoteException, SiteViewException {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			Collection mgs = apimonitor.getMonitorsForGroup(groupID);

			if (mgs.size() > 0) {
				for (Iterator iter = mgs.iterator(); iter.hasNext();) {
					AtomicMonitor monitor = (AtomicMonitor) iter.next();
					SSInstanceProperty assinstanceproperty1[] = apimonitor
							.getInstanceProperties(monitor.getProperty("_id"),
									groupID,
									APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
					Map<String, Object> nodedata = new HashMap<String, Object>();
					for (int k = 0; k < assinstanceproperty1.length; k++) {
						if(assinstanceproperty1[k].getName().equals("_hostname")){
							nodedata.put("_hostname", monitor.getFull("_hostname"));
						}else{
							nodedata.put(assinstanceproperty1[k].getName(),assinstanceproperty1[k].getValue());
						}
					}
					nodedata.put("_id", monitor.getFullID());
					nodedata.put("_class",monitor.getFull("_class"));
					list.add(nodedata);
				}
			}
		} catch (java.lang.Exception e) {
			System.out.println(e);
		}
		return list;
	}

	public int getNumOfMonitorsForGroup(String groupID) throws RemoteException,
			SiteViewException {
		return apigroup.getNumOfMonitorsForGroup(groupID);
	}

	static public ArrayList<HashMap<String, String>> getMonitorsData() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		Map<String, MonitorGroup> map=MonitorGroup.monitorgroups;
		Set s=map.entrySet();
		Iterator iter =s.iterator();
		while(iter.hasNext()){
			String k=(String)iter.next();
            Object v=map.get(k);
		}
//		try {
//			APIMonitor apim = new APIMonitor();
//			Collection collection = apim.getAllMonitors();
//			Vector vector = (Vector) collection;
//
//			Monitor monitor;
//			int index = 0;
//			for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
//				monitor = (Monitor) iterator.next();
//
//				HashMap<String, String> ndata = new HashMap<String, String>();
//				ndata.put(new String("Name"),
//						monitor.getProperty(AtomicMonitor.pName));
//				ndata.put(new String("GroupID"),
//						monitor.getProperty(AtomicMonitor.pOwnerID));
//				ndata.put(new String("MonitorID"),
//						monitor.getProperty(AtomicMonitor.pID));
//				ndata.put(new String("Type"),
//						monitor.getProperty(AtomicMonitor.pClass));
//				list.add(ndata);
//			}
//		} catch (java.lang.Exception e) {
//			System.out.println(e);
//		}
		return list;
	}

	public boolean trylogin(String strUser, String strPwd)
			throws RemoteException, SiteViewException {
		jgl.Array array = COM.dragonflow.SiteView.User.findUsersForLogin(
				strUser, strPwd);
		if (array.size() > 0)
			return true;
		else
			return false;
	}

	public void createMonitor(String monitorType, String groupid,
			List<Map<String, String>> paramlist) throws RemoteException,
			SiteViewException {
		// TODO Auto-generated method stub
		int size = getPropertyCount(paramlist);
		SSInstanceProperty[] assinstanceproperty = new SSInstanceProperty[size];
		int j = 0;

		for (int i = 0; i < paramlist.size(); i++) {
			Map<String, String> map = paramlist.get(i);
			for (Entry<String, String> entry : map.entrySet()) {
				String k = entry.getKey();
				String v = entry.getValue();
				assinstanceproperty[j] = new SSInstanceProperty(k, v);
				j++;
			}
		}
		COM.dragonflow.Api.SSStringReturnValue ssstringreturnvalue2 = apimonitor
				.create(monitorType, groupid, assinstanceproperty);
	}

	private int getPropertyCount(List<Map<String, String>> paramlist) {
		int total = 0;
		for (int i = 0; i < paramlist.size(); i++) {
			Map<String, String> map = paramlist.get(i);
			// assinstanceproperty = new SSInstanceProperty[map.size()];
			total += map.size();
		}

		return total;
	}

	/**
	 * Get monitor counters parms:hostname,monitorType return String
	 * monitorcounters
	 */
	public String getMonitorCounters(Map parmsmap) throws RemoteException,
			SiteViewException {
		boolean flag = true;
		String monitorcounters = "";
		String xmlDoc = "";
		StringBuffer stringbuffer = new StringBuffer("");
		String cachexmlname = "";
		if (isHave(InterFaceCounterGroups, parmsmap.get("monitortype")
				.toString())) {// InterFace Monitor
			SNMPCPUMonitor snmp = new SNMPCPUMonitor();
			snmp.createSnmp(parmsmap.get("serverInterface").toString(), Integer
					.parseInt(parmsmap.get("PortInterface").toString()),
					parmsmap.get("InterfaceSNMPPublic").toString(), Integer
							.parseInt(parmsmap.get("TimeOutInterface")
									.toString()), 800);
			List<String> interfaceportcounterlist = snmp
					.getTree("1.3.6.1.2.1.2.2.1.1");
			List<String> interfacedesccounterlist = snmp
					.getTree("1.3.6.1.2.1.2.2.1.2");
			for (String interfacedesc : interfacedesccounterlist) {
				interfacedesc = interfacedesc.split("&")[1];
				monitorcounters += interfacedesc + ",";
			}
		} else {
			AtomicMonitor atomicmonitor = null;
			try {
				atomicmonitor = AtomicMonitor.MonitorCreate(parmsmap.get(
						"monitortype").toString());

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (isHave(NTCounterGroups, parmsmap.get("monitortype").toString())) {// NTCounterGroups
//				Array array_1=new Array();
//				String as[]=TextUtils.split("SQLServer:Access Methods, SQLServer:Backup Device, SQLServer:Buffer Manager, SQLServer:Buffer Partition, SQLServer:Cache Manager, SQLServer:Databases, SQLServer:General Statistics, SQLServer:Latches, SQLServer:Locks, SQLServer:Memory Manager, SQLServer:Replication Agents, SQLServer:Replication Dist., SQLServer:Replication Logreader, SQLServer:Replicaton Merge, SQLServer:Replication Snapshot, SQLServer:SQL Statistics, SQLServer:User Settable", ",");
//				  for (int i = 0; i < as.length; i++) {
//			            array_1.add(as[i]);
//			        }
//				Array array=NTCounterBase.getPerfCounters(parmsmap.get("hostname").toString(), array_1, stringbuffer, "");
//				String s="";
//				int i = 0;
//				for (int j = 0; j < array.size(); j++) {
//					if (!((PerfCounter) array.at(j)).instance.equals("_Total")) {
//						continue;
//					}
//					if (i >= nMaxCounters) {
//						break;
//					}
//					if (!flag) {
//						s = s + ",";
//					} else {
//						flag = false;
//					}
//					s = s + ((PerfCounter) array.at(j)).object + " -- ";
//					s = s + ((PerfCounter) array.at(j)).counterName;
//					if (((PerfCounter) array.at(j)).instance.length() > 0) {
//						s = s + " -- " + ((PerfCounter) array.at(j)).instance;
//					}
//					i++;
//				}
//				System.out.println(s);
				monitorPage m=new monitorPage();
				  atomicmonitor.setProperty("_machine", parmsmap.get("hostname").toString());
				monitorcounters=m.getC(atomicmonitor);
//				monitorcounters = ((NTCounterBase) atomicmonitor)
//						.getCountersContent();
//				if (monitorcounters.length() == 0) {
//					jgl.Array array1 = ((NTCounterBase) atomicmonitor)
//							.getAvailableCounters();
//					if (array1.size() > 0) {
//						monitorcounters = ((NTCounterBase) atomicmonitor)
//								.getDefaultCounters();
//					} else {
//						flag = false;
//					}
//				}
			} else if (isHave(BrowsableBaseCounterGroups,
					parmsmap.get("monitortype").toString())) {// BrowsableBaseCounterGroups
				if (parmsmap.get("monitortype").toString()
						.equals("OracleJDBCMonitor")) {
					atomicmonitor.setProperty("_driver",
							parmsmap.get("oracleuserdriver").toString());
					atomicmonitor.setProperty("_connectTimeout",
							parmsmap.get("connectiontimeout").toString());
					atomicmonitor.setProperty("_queryTimeout",
							parmsmap.get("querytimeout").toString());
					atomicmonitor.setProperty("_server",
							parmsmap.get("oracleuserurl").toString());
					atomicmonitor.setProperty("_user",
							parmsmap.get("oracleusername").toString());
					atomicmonitor.setProperty("_password",
							parmsmap.get("oracleuserpwd").toString());
					boolean flag1 = ((COM.dragonflow.SiteView.BrowsableMonitor) atomicmonitor)
							.isUsingCountersCache();// Check if use cache
					if (flag1) {
						cachexmlname = COM.dragonflow.SiteView.BrowsableCache
								.getXmlFileName(atomicmonitor);// get Cache xml
																// name
						xmlDoc = COM.dragonflow.SiteView.BrowsableCache
								.getXmlFile(cachexmlname);// Get xml data
						monitorcounters = analyticXml(xmlDoc);
					}
					if (xmlDoc.length() == 0) {
						xmlDoc = ((COM.dragonflow.SiteView.BrowsableMonitor) atomicmonitor)
								.getBrowseData(stringbuffer).trim();
						if (stringbuffer.length() == 0 && flag1) {
							COM.dragonflow.SiteView.BrowsableCache.saveXmlFile(
									cachexmlname, xmlDoc);
						}
					}
				}

			}
		}
		if (monitorcounters.length() == 0) {
			if (flag) {
				monitorcounters = "No Counters selected";
			} else {
				monitorcounters = "No Counters available for this machine";
			}
		}
		return monitorcounters;
	}

	// 解析返回的XML格式的数据成String
	public static String analyticXml(String xmlDoc) {
		String xmldata = "";

		try {
			StringReader xmlString = new StringReader(xmlDoc);
			InputSource source = new InputSource(xmlString);
			SAXBuilder saxb = new SAXBuilder();
			Document document = saxb.build(source); // 把文件解析成DOCUMENT类型
			Element root = document.getRootElement(); // 得到Document的根
			List nodeList = root.getChildren();
			Element et = null;
			for (int i = 0; i < nodeList.size(); i++) {
				et = (Element) nodeList.get(i);// 循环依次得到子元素
				String objname = et.getAttributeValue("name");
				List counterNodes = et.getChildren(); // 得到内层子节点
				Element counterEt = null;
				for (int j = 0; j < counterNodes.size(); j++) {
					counterEt = (Element) counterNodes.get(j); // 循环依次得到子元素
					String countername = counterEt.getAttributeValue("name");
					String everycounterstr = objname + " " + countername;
					xmldata += everycounterstr + ",";
				}

			}
			System.out.println(xmldata);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xmldata;

	}

	public String getSysOid(Map<String, String> map) throws Exception {
		// TODO Auto-generated method stub
		return SNMPCPUMonitor.getSysOid(map);
	}

	public void adjustGroups(String s, String s1,String s2) throws Exception {
		Array array = new jgl.Array();
		Array array1 = new jgl.Array();
		Array array2 = new jgl.Array();
		jgl.HashMap hashmap = new jgl.HashMap();
		String RootFilePath=System.getProperty("user.dir");
		RootFilePath=RootFilePath.substring(0,RootFilePath.lastIndexOf("\\")+1);
		RootFilePath=RootFilePath+"com.siteview.kernel.core\\groups\\history.config";
		if(s2.equals("add")){
			array.add(s);
		}else if(s2.equals("edit")){
			array1.add(s1);
		}else if(s2.equals("delete")){
			array1.add(RootFilePath);
			array2.add(s1);
		}
//		if (!s.equals("")) {
//			array.add(s);
//		}
//		if (!s1.equals("")) {
//			array1.add(s1);
//		}
		SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
		siteviewgroup.adjustGroups(array, array1, array2, hashmap);
	}
	/**
	 * 将设备信息写入配置文件（避免查询数据库）
	 */
	public void writeRemoteMachineToFile(String remoteMachineInfo) throws Exception {
		try {
			String RootFilePath=System.getProperty("user.dir");
			String masterFilePathUrl = RootFilePath+"\\groups\\master.config";
			File masterfile = new File(masterFilePathUrl);
			BufferedReader input = new BufferedReader(
					new FileReader(masterfile));
			String st = "";
			while ((st = input.readLine()) != null) {
				remoteMachineInfo += st + "\n";
			}
			input.close();
			BufferedWriter output = new BufferedWriter(new FileWriter(
					masterfile));
			output.write(remoteMachineInfo);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public List<String[]> doTestMachine(String s,String hostname,String id)
			throws Exception {
		List<String[]> array_0=new ArrayList<String[]>();
		String s0[]=new String[1];
		s+="#;"+"\r";
		Array array =FrameFile.mangleIt(s);
		Enumeration enumeration=FrameFile.readFrames(array.elements()).elements();
		jgl.HashMap hashMap=(jgl.HashMap) enumeration.nextElement();
		if(s.contains("_remoteMachine")){
			machinePage ma=new machinePage();
			s0[0]=ma.doTest1(Machine.createMachine(hashMap));
		}else if(s.contains("_remoteNTMachine")){
			ntmachinePage ms=new ntmachinePage();
			Machine machine = Machine.createMachine(hashMap);
			s0[0]=ms.doTest1(machine,hashMap);
		}
		Vector vector = null;
		Array array_1 = null;
		String method = (String)hashMap.get("_method");
		if("wmi".equals(method)){
			vector=Platform.getDisks(hostname);//磁盘
			array_1 = Platform.getProcesses(hostname, true);//服务
		}else if(s.contains("_remoteMachine")){
			vector=Platform.getDisks("remote:"+id);//磁盘
			array_1 = Platform.getProcesses("remote:"+id, true);//服务
		}else{
			vector=Platform.getDisks(hostname);//磁盘
			array_1 = Platform.getProcesses(hostname, true);//服务
		}
		String s1[]=new String[vector.size()/2];
		String s2[]=new String[array_1.size()];
		Enumeration disk = vector.elements();
		int i=0;
		while (disk.hasMoreElements()) {
			String s5 = (String) disk.nextElement();
			String s4=(String) disk.nextElement();
			s1[i++]=s4;
		 }
		Enumeration<String> d = array_1.elements();
		int n=0;
		while (d.hasMoreElements()) {
			String test=d.nextElement();
			if(test.equals("Perfex - Add Connection Failed")||test.equals("")||test.contains("登录失败: 未知的用户名或错误密码")){
				continue;
			}
			s2[n++]=test;
		}
		array_0.add(s0);
		array_0.add(s1);
		array_0.add(s2);
		return array_0;
	}

	 public static void main(String[] args){
	    	String s="\\\\dragonfl-fc1faa";
	    	 Array array = Platform.getProcesses(s, true);
	    	 System.out.println(array.size());
	    }
	//获取磁盘信息
		public String[] getDiskSpace(String hostname,String s) throws SiteViewException,RemoteException {
			Vector vector;
			if(s==null ||s.equals("")){
				vector = Platform.getDisks(hostname);// 磁盘
			}else{
				vector=Platform.getDisks(s);
			}
			String s1[]= new String[vector.size() / 2];
			Enumeration disk = vector.elements();
			int i = 0;
			while (disk.hasMoreElements()) {
				String s5 = (String) disk.nextElement();
				String s4=(String) disk.nextElement();
				s1[i++]=s5;
			 }
			return s1;
		}

		//获取服务信息
		public String[] getServer(String hostname,String s) throws RemoteException {
			Array array_1;
			if(s.equals("")){
				array_1= Platform.getProcesses(hostname, true);// 服务
			}else{
				array_1 = Platform.getProcesses(s, true);// 服务=
			}
			
			String s2[] = new String[array_1.size()];
			Enumeration<String> d = array_1.elements();
			int n = 0;
			while (d.hasMoreElements()) {
				String test = d.nextElement();
				if (test.equals("Perfex - Add Connection Failed")
						|| test.equals("") || test.contains("登录失败: 未知的用户名或错误密码")) {
					continue;
				}
				s2[n++] = test;
			}
			return s2;
		}

		public void Refresh(String s,String groupid)throws RemoteException{
			Array array = new jgl.Array();
			Array array1 = new jgl.Array();
			Array array2 = new jgl.Array();
			jgl.HashMap hashmap = new jgl.HashMap();
			String ss = "GroupId="+groupid;
			array1.add(ss);
			SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
			siteviewgroup.adjustGroups(array, array1, array2, hashmap);
		}
}
