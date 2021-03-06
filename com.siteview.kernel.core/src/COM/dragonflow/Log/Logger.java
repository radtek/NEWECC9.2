/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Log;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Enumeration;
import java.util.Vector;

import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.Utils.FileTools;
import COM.dragonflow.itsm.data.Config;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;

public abstract class Logger {

	static String FIELD_SEPARATOR = "\t";

	protected java.util.Vector buffers;

	protected long bufferDuration;

	public String echoTo;

	protected String fullMonitorName;

	public Logger() {
		buffers = new Vector();
		bufferDuration = 0L;
		echoTo = "";
		fullMonitorName = null;
	}

	public Logger(long l) {
		buffers = new Vector();
		bufferDuration = 0L;
		echoTo = "";
		fullMonitorName = null;
		bufferDuration = l;
	}

	public static String dateToString(java.util.Date date) {
		return COM.dragonflow.Utils.TextUtils.dateToString(date);
	}

	public void setEchoTo(String s) {
		echoTo = s.toLowerCase();
	}

	String getMonitorName(
			COM.dragonflow.Properties.PropertiedObject propertiedobject) {
		if (fullMonitorName == null) {
			jgl.HashMap hashmap = COM.dragonflow.SiteView.MasterConfig
					.getMasterConfig();
			fullMonitorName = COM.dragonflow.Utils.TextUtils.getValue(hashmap,
					"_fullMonitorName");
		}
		String s = "";
		if (fullMonitorName.length() > 0) {
			s = COM.dragonflow.Page.CGI
					.getGroupFullName(propertiedobject
							.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pGroupID));
			s = s
					+ ": "
					+ propertiedobject
							.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pName);
		} else {
			s = propertiedobject
					.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pName);
		}
		return s;
	}

	public void log(String s, java.util.Date date,
			COM.dragonflow.Properties.PropertiedObject propertiedobject) {
		StringBuffer stringbuffer = new StringBuffer();
		jgl.Array array = propertiedobject.getLogProperties();
		String s1;
		StringBuffer s0 = new StringBuffer();
		for (Enumeration enumeration = array.elements(); enumeration
				.hasMoreElements(); stringbuffer.append(s1)) {
			COM.dragonflow.Properties.StringProperty stringproperty = (COM.dragonflow.Properties.StringProperty) enumeration
					.nextElement();
			s0.append(stringproperty + "=");
			if (stringbuffer.length() > 0) {
				stringbuffer.append("\t");
			}
			if (stringproperty == COM.dragonflow.SiteView.Monitor.pID) {
				s1 = propertiedobject.getProperty(stringproperty)
						+ ":"
						+ propertiedobject
								.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pSample);
			} else if (stringproperty == COM.dragonflow.SiteView.AtomicMonitor.pName) {
				s1 = getMonitorName(propertiedobject);
			} else {
				s1 = propertiedobject.getProperty(stringproperty);
			}
			s1 = s1.replace('\r', ' ');
			s1 = s1.replace('\n', ' ');
			s1 = s1.replace('\t', ' ');
			s0.append(s1 + "\t");
		}
		if (propertiedobject.getProperty(
				COM.dragonflow.SiteView.Monitor.pCategory).equals(
				COM.dragonflow.SiteView.Monitor.ERROR_CATEGORY)) {
			stringbuffer.append("\t");
			if (((COM.dragonflow.SiteView.AtomicMonitor) propertiedobject)
					.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pNoData)
					.length() > 0) {
				stringbuffer.append(COM.dragonflow.SiteView.Monitor.FAILURE);
			} else {
				stringbuffer
						.append(COM.dragonflow.SiteView.Monitor.NON_FAILURE);
			}

		}
		log(s, date, s0.toString());
	}

	public void log(String s, java.util.Date date, String s1) {

	}

	// ��־�洢
	protected static void savaLog(String s1) {
		// TODO Auto-generated method stub
		String counterMonitorStr = s1;
		String category = s1.substring(s1.indexOf("=") + 1, s1.indexOf("\t"));
		s1 = s1.substring(s1.indexOf("\t") + 1);
		String ownerID = s1.substring(s1.indexOf("=") + 1, s1.indexOf("\t"));
		s1 = s1.substring(s1.indexOf("\t") + 1);
		String MonitorName = s1.substring(s1.indexOf("=") + 1, s1.indexOf("\t"));
		String MonitorId = s1.substring(s1.indexOf("_id=") + 4);
		String stateString = MonitorId.substring(MonitorId.indexOf("\t") + 1);
		MonitorId = MonitorId.substring(0, MonitorId.indexOf(":"));
		BusinessObject bo_0=FileTools.CreateBo("RecId", MonitorId, "Ecc");
		String monitorType = "";
		monitorType=bo_0.GetField("EccType").get_NativeValue().toString();
		monitorType=Config.getReturnStr("itsm_siteview9.2.properties",monitorType);
		BusinessObject bo=FileTools.getBusinessObject("MonitorLog");
		if (FrameFile.isHave(FrameFile.MonitorCounterGroups, monitorType)) {
//			String state = counterMonitorStr.substring(counterMonitorStr.indexOf("stateString=")+12, counterMonitorStr.indexOf("_id"));
			String state=s1.substring(s1.indexOf("_id="));
			state=s1.substring(s1.indexOf("\t")+1);
//			String[] stateArray = state.split(",");
//			String counterMonitorStateString = "";
//			StringBuffer counterSbf = new StringBuffer();
//			for (String indexState : stateArray) {
//				String indexStateKey = indexState.substring(0, indexState.indexOf("="));
//				String indexStateValue = indexState.substring(indexState.indexOf("=")+1, indexState.lastIndexOf("/")).trim();
//				counterMonitorStateString = indexStateKey+"="+indexStateValue+"\t";
//				counterSbf.append(counterMonitorStateString);
//			}
			bo.GetField("MonitorMassage").SetValue(new SiteviewValue(state.toString()));
		} else {
			bo.GetField("MonitorMassage").SetValue(new SiteviewValue(stateString));
		}
		bo.GetField("ownerID").SetValue(new SiteviewValue(ownerID));
		bo.GetField("MonitorStatus").SetValue(new SiteviewValue(category));
		bo.GetField("MonitorName").SetValue(new SiteviewValue(MonitorName));
		bo.GetField("MonitorId").SetValue(new SiteviewValue(MonitorId));
		bo.SaveObject(FileTools.getApi_1(), false, true);
	}

	public void close() {
	}

	public String customLogName() {
		return "";
	}

	public long getBufferDuration() {
		return bufferDuration;
	}

	public void flush() {
		for (; buffers.size() > 0; flush(buffers.remove(0))) {
		}
	}

	public void flush(java.lang.Object obj) {
	}

}
