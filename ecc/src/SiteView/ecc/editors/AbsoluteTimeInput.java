package SiteView.ecc.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class AbsoluteTimeInput implements IEditorInput {
	private String name="";

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "绝对时间任务计划";
	}

	@Override
	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolTipText() {
		// TODO Auto-generated method stub
		return "";
	}
	
	@Override  
	 public boolean equals(Object obj) {  
	    if(null == obj) return false;  
	              
	    if(!(obj instanceof AbsoluteTimeInput)) return false;  
	              
	    if(!getName().equals(((AbsoluteTimeInput)obj).getName())) return false;  
	              
	    return true;  
	  } 

}
