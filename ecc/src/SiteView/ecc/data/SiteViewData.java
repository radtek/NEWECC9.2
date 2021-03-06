package SiteView.ecc.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import system.Collections.ICollection;
import system.Collections.IEnumerator;

import SiteView.ecc.Modle.AlarmModle;
import SiteView.ecc.Modle.GroupModle;
import SiteView.ecc.Modle.MachineModle;
import SiteView.ecc.Modle.MonitorModle;
import SiteView.ecc.Modle.MonitorSetUpModel;
import SiteView.ecc.Modle.SetUpModle;
import SiteView.ecc.Modle.SiteViewEcc;
import SiteView.ecc.Modle.StatementsModle;
import SiteView.ecc.tools.FileTools;
import SiteView.ecc.view.EccTreeControl;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;

public class SiteViewData {
	public static Siteview.User user;
	public boolean  hasSuperUser=false;
	public static List<GroupModle> groups_0 = new ArrayList<GroupModle>();
	public static Map<String, GroupModle> subgroups = new HashMap<String, GroupModle>();
	public static Object item;
	public static Map<String,BusinessObject> permissions=new HashMap<String,BusinessObject>();

	public  List<SiteViewEcc> getData() {
		String userid=ConnectionBroker.get_SiteviewApi().get_AuthenticationService().get_CurrentAuthenticationId();
		user=ConnectionBroker.get_SiteviewApi().get_AuthenticationService().GetUser("User", userid);
		SiteViewEcc siteview = new SiteViewEcc();
		if(user.get_SecurityGroupName().equals("监测经理")){
			getPermissions(userid);
		}else{
			hasSuperUser=true;
		}
		List site = new ArrayList();
		ICollection iCollection = FileTools.getBussCollection("parentGroupId","", "EccGroup");
		IEnumerator interfaceTableIEnum = iCollection.GetEnumerator();// 返回可循环访问集合的枚举数
		List list = new ArrayList();
		if(hasSuperUser){
			while(interfaceTableIEnum.MoveNext()) {
				BusinessObject bo = (BusinessObject) interfaceTableIEnum.get_Current();
				GroupModle g = new GroupModle(bo,true,true,true,true,true,true,true,true);
				List<GroupModle> sub = createTreeItem(bo.get_RecId());
				g.setGroups(sub);
				List<MachineModle> machine = createMachines(bo.get_RecId());
				g.setMachines(machine);
				subgroups.put(bo.get_RecId(), g);
				list.add(g);
				siteview.setAddGroup(true);
			}
		}else if(permissions.get("SE")!=null){
			BusinessObject bo2=permissions.get("SE");
			siteview.setAddGroup((Boolean)bo2.GetField("AddGroup").get_NativeValue());
			if((Boolean)bo2.GetField("SelectPerimissions").get_NativeValue()){
				while (interfaceTableIEnum.MoveNext()) {// 移到集合的下一个元素。如果成功则返回为
					BusinessObject bo = (BusinessObject) interfaceTableIEnum.get_Current();// 获取集合中的当前元素
					if(permissions.get(bo.get_RecId())!=null) {
						BusinessObject bo1=permissions.get(bo.get_RecId());
						if(bo1.GetField("PermissionsType").get_NativeValue().toString().equals("Group")&&(Boolean)bo1.GetField("SelectPerimissions").get_NativeValue()){
							GroupModle g = new GroupModle(bo);
							g.setAddMachine((Boolean)bo1.GetField("AddMachine").get_NativeValue());
							g.setAddSubGroup((Boolean)bo1.GetField("AddGroup").get_NativeValue());
							g.setAddMonitor((Boolean)bo1.GetField("AddMonitor").get_NativeValue());
							g.setEditGroup((Boolean)bo1.GetField("EditGroup").get_NativeValue());
							g.setDeleteGroup((Boolean)bo1.GetField("DeleteGroup").get_NativeValue());
							g.setDeleteMchine((Boolean)bo1.GetField("DeleteMachine").get_NativeValue());
							g.setDeleteMonitor((Boolean)bo1.GetField("DeleteMonitor").get_NativeValue());
							g.setEditmonitor((Boolean)bo1.GetField("EditMonitor").get_NativeValue());
							List<GroupModle> sub = createTreeItem(bo.get_RecId());
							g.setGroups(sub);
							List<MachineModle> machine = createMachines(bo.get_RecId());
							g.setMachines(machine);
							subgroups.put(bo.get_RecId(), g);
							list.add(g);
						}
					}
				}
			}
		}
		groups_0.addAll(list);
		siteview.setList(list);
		site.add(siteview);
//		if(hasSuperUser){
//			site.add(new MonitorSetUpModel());
//			site.add(new AlarmModle());	
//			site.add(new StatementsModle());
//			site.add(new SetUpModle());
//		}
//		if(permissions.get("告警")!=null){
//			site.add(new AlarmModle());	
//		}
//		if(permissions.get("")!=null){
//			site.add(new StatementsModle());
//		}
//		if(permissions.get("userManage")!=null){
//			site.add(new SetUpModle());
//		}
		return site;
	}

	private  List<MachineModle> createMachines(String id) {
		List<MachineModle> list = new ArrayList<MachineModle>();
		ICollection iCollection = FileTools.getBussCollection("Groups", id,
				"RemoteMachine");
		IEnumerator interfaceTableIEnum = iCollection.GetEnumerator();// 返回可循环访问集合的枚举数
		while (interfaceTableIEnum.MoveNext()) {// 移到集合的下一个元素。如果成功则返回为
												// true；如果超过集合结尾，则返回false
			BusinessObject bo = (BusinessObject) interfaceTableIEnum.get_Current();// 获取集合中的当前元素
			if(hasSuperUser){
				MachineModle machines = new MachineModle(bo,true,true,true,true,true);
				list.add(machines);
			}else if(permissions.get(bo.get_RecId())!=null ){
				BusinessObject bo1=permissions.get(bo.get_RecId());
				if(bo1.GetField("PermissionsType").get_NativeValue().toString().equals("Machine")&&(Boolean)((BusinessObject)permissions.get(bo.get_RecId())).GetField("SelectPerimissions").get_NativeValue()){
					MachineModle machines = new MachineModle(bo);
					machines.setEditMachine((Boolean)bo1.GetField("EditMachine").get_NativeValue());
					machines.setAddMonitor((Boolean)bo1.GetField("AddMonitor").get_NativeValue());
					machines.setDeleteMchine((Boolean)bo1.GetField("DeleteMachine").get_NativeValue());
					machines.setDeleteMonitor((Boolean)bo1.GetField("DeleteMonitor").get_NativeValue());
					machines.setDeleteMonitor((Boolean)bo1.GetField("EditMonitor").get_NativeValue());
					list.add(machines);
				}
			}
		}
		return list;
	}

	public  List<GroupModle> createTreeItem(String id) {
		ICollection iCollection = FileTools.getBussCollection("parentGroupId",id, "EccGroup");
		IEnumerator interfaceTableIEnum = iCollection.GetEnumerator();// 返回可循环访问集合的枚举数
		List<GroupModle> list = new java.util.ArrayList<GroupModle>();
		while (interfaceTableIEnum.MoveNext()) {
			BusinessObject bo = (BusinessObject) interfaceTableIEnum.get_Current();// 获取集合中的当前元素
			if(hasSuperUser){
				GroupModle g = new GroupModle(bo,true,true,true,true,true,true,true,true);
				g.setGroups(createTreeItem(bo.get_RecId()));
				g.setMachines(createMachines(bo.get_RecId()));
				subgroups.put(bo.get_RecId(), g);
				list.add(g);
			}else if(permissions.get(bo.get_RecId())!=null ){
				BusinessObject bo1=permissions.get(bo.get_RecId());
				if(bo1.GetField("PermissionsType").get_NativeValue().toString().equals("Group")
						&&(Boolean)((BusinessObject)permissions.get(bo.get_RecId())).GetField("SelectPerimissions").get_NativeValue()){
					GroupModle g = new GroupModle(bo);
					g.setAddMachine((Boolean)bo1.GetField("AddMachine").get_NativeValue());
					g.setAddSubGroup((Boolean)bo1.GetField("AddGroup").get_NativeValue());
					g.setAddMonitor((Boolean)bo1.GetField("AddMonitor").get_NativeValue());
					g.setEditGroup((Boolean)bo1.GetField("EditGroup").get_NativeValue());
					g.setDeleteGroup((Boolean)bo1.GetField("DeleteGroup").get_NativeValue());
					g.setDeleteMchine((Boolean)bo1.GetField("DeleteMachine").get_NativeValue());
					g.setDeleteMonitor((Boolean)bo1.GetField("DeleteMonitor").get_NativeValue());
					g.setEditmonitor((Boolean)bo1.GetField("EditMonitor").get_NativeValue());
					g.setGroups(createTreeItem(bo.get_RecId()));
					g.setMachines(createMachines(bo.get_RecId()));
					subgroups.put(bo.get_RecId(), g);
					list.add(g);
				}
			}
		}
		return list;
	}
	
	public void getPermissions(String userId){
		ICollection ico=FileTools.getBussCollection("UserId", userId, "Permissions");
		IEnumerator ienum=ico.GetEnumerator();
		while(ienum.MoveNext()){
			BusinessObject bo=(BusinessObject) ienum.get_Current();
			permissions.put(bo.GetField("PermissionsId").get_NativeValue().toString(), bo);
		}
	}
	
	public static List CreatTreeData(){
		List site = (List) EccTreeControl.treeViewer.getInput();
		List<SiteViewEcc> s0=new ArrayList<SiteViewEcc>();
		s0.addAll(site);
		SiteViewEcc siteview =(SiteViewEcc) s0.get(0);
		List<GroupModle> list=siteview.getList();
		siteview=new SiteViewEcc();
		siteview.setList(getChild(list));
		List ss=new ArrayList();
		ss.add(siteview);
		return ss;
	}
	
	public static List<GroupModle> getChild(List<GroupModle> list){
		List<MonitorModle> monitors;
		for(int n=0;n<list.size();n++){
			monitors=new ArrayList<MonitorModle>();
			GroupModle group=list.get(n);
			List<MonitorModle> map=new ArrayList<MonitorModle>();
			String groupId=group.getBo().get_RecId();
			ICollection ico=FileTools.getBussCollection("Groups", groupId, "Ecc");
			IEnumerator ienum=ico.GetEnumerator();
			while(ienum.MoveNext()){
				BusinessObject bo=(BusinessObject) ienum.get_Current();
				MonitorModle monitor=new MonitorModle(bo);
				if(bo.GetField("Machine").get_NativeValue().toString()==null ||bo.GetField("Machine").get_NativeValue().equals("")){
					monitors.add(monitor);
				}else{
					map.add(monitor);
				}
			}
			List<MachineModle> machines=group.getMachines();
			if(machines!=null && machines.size()>0){
				for(int i=0;i<machines.size();i++){
					MachineModle m=machines.get(i);
					List<MonitorModle> mio=new ArrayList<MonitorModle>();
					String id=m.getBo().get_RecId();
					for(int j=0;j<map.size();j++){
						MonitorModle mo=map.get(j);
						if(mo.getBo().GetField("Machine").get_NativeValue().toString().equals(id)){
							mio.add(mo);
							map.remove(j);
							j--;
						}
					}
					m.setMonitors(mio);
					machines.remove(i);
					machines.add(i, m);
				}
			}
			group.setGroups(getChild(group.getGroups()));
			group.setMachines(machines);
			group.setMonitors(monitors);
			list.remove(n);
			list.add(n, group);
			
		}
		return list;
	}
}
