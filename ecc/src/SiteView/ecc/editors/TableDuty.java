package SiteView.ecc.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import SiteView.ecc.Control.DutyDetailContentProvider;
import SiteView.ecc.Control.DutyDetailLabelProvider;
import SiteView.ecc.Control.TableDutyContentProvider;
import SiteView.ecc.Control.TableDutyLabelProvider;
import SiteView.ecc.Modle.DetailModel;
import SiteView.ecc.Modle.TableModle;
import SiteView.ecc.data.DutyDetailInfor;
import SiteView.ecc.data.TableDutyInfor;
import SiteView.ecc.dialog.AddDutyDetail;
import SiteView.ecc.dialog.AddTableDuty;
import Siteview.Api.BusinessObject;
import Siteview.Api.BusinessObjectCollection;
import Siteview.Windows.Forms.ConnectionBroker;



public class TableDuty extends EditorPart{
	public static String ID = "SiteView.ecc.editors.TableDuty";
	public static TableViewer TableViewer;//第一个表单
	public Table table;
	public static TableItem tableItem;//第一个表单的第一行
	public static TableViewer TableViewer1;//第二个表单
	public static TableItem tableItem1;//第二个表单的第一行
	public Table table_1;
	public Button btnNewButton_1;
	public String type;
	public BusinessObject bo1;
	public TableDuty(){
		
	}
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		this.setSite(site);// 设置site
		this.setInput(input);// 设置输入的IEditorInput对象
		this.setPartName(input.getName());// 设置编辑器上方显示的名称
		
	}
	@Override
	public boolean isDirty() {
		return false;
	}
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		sashForm.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NORMAL));
		
		Composite composite = new Composite(sashForm, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		composite.setLayout(new FormLayout());
		
		Button button = new Button(composite, SWT.NONE);//第一个添加按钮
		button.setAlignment(SWT.LEFT);
		FormData fd_button = new FormData();
		fd_button.top = new FormAttachment(0);
		fd_button.left = new FormAttachment(0);
		button.setLayoutData(fd_button);
		button.setText("\u6DFB\u52A0");
		button.addSelectionListener(new SelectionAdapter(){//添加按钮事件
			public void widgetSelected(SelectionEvent e){
				AddTableDuty addTableDuty=new AddTableDuty(null);
				addTableDuty.open();
			}
		});
		
		Button button_1 = new Button(composite, SWT.NONE);
		button_1.setAlignment(SWT.LEFT);
		FormData fd_button_1 = new FormData();
		fd_button_1.top = new FormAttachment(button, 0, SWT.TOP);
		fd_button_1.left = new FormAttachment(button, 6);
		button_1.setLayoutData(fd_button_1);
		button_1.setText("\u5220\u9664");
		button_1.addSelectionListener(new SelectionAdapter(){//控制第一个表单的删除按钮
			public void widgetSelected(SelectionEvent e){//删除按钮监听事件
				TableModle tm=(TableModle) tableItem.getData();
				BusinessObject bo=tm.getBo();
				bo.DeleteObject(ConnectionBroker.get_SiteviewApi());
				TableDutyInfor.list.remove(tm);
				TableViewer.setInput(TableDutyInfor.list);
				TableViewer.refresh();
				
			}
		});
		
		Button button_2 = new Button(composite, SWT.NONE);//第三个按钮刷新
		button_2.setAlignment(SWT.LEFT);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableViewer.refresh();
			}
		});
		FormData fd_button_2 = new FormData();
		fd_button_2.top = new FormAttachment(button, 0, SWT.TOP);
		fd_button_2.left = new FormAttachment(button_1, 6);
		button_2.setLayoutData(fd_button_2);
		button_2.setText("\u5237\u65B0");
		
		Button btnNewButton = new Button(composite, SWT.NONE);//第四个帮助按钮
		btnNewButton.setAlignment(SWT.LEFT);
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.top = new FormAttachment(button, 0, SWT.TOP);
		fd_btnNewButton.left = new FormAttachment(button_2, 6);
		btnNewButton.setLayoutData(fd_btnNewButton);
		btnNewButton.setText("\u5E2E\u52A9");
		
		Label lblNewLabel_1 = new Label(sashForm, SWT.NONE);
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_1.setText("\u503C\u73ED\u8868\u8BBE\u7F6E\u8BE6\u7EC6\u4FE1\u606F");
		
		Composite composite_1 = new Composite(sashForm, SWT.NONE);
		composite_1.setLayout(new FillLayout());
		composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		
		
        Composite composite_3 = new Composite(sashForm, SWT.NONE);
		
	    btnNewButton_1 = new Button(composite_3, SWT.NONE);//添加按钮
		btnNewButton_1.setBounds(27, 10, 47, 22);
		btnNewButton_1.setText("\u6DFB\u52A0");
		btnNewButton_1.setEnabled(false);
		btnNewButton_1.addSelectionListener(new SelectionAdapter(){//添加按钮事件
			public void widgetSelected(SelectionEvent e){
				AddDutyDetail addDutyDetail=new AddDutyDetail(null,type,bo1);
				addDutyDetail.open();
			}
		});
		
	    TableViewer =  new TableViewer(composite_1, SWT.MULTI
				 |SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.CHECK);//值班表设置详细信息表
	    table = TableViewer.getTable();
		table.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				tableItem=(TableItem) e.item;
				btnNewButton_1.setEnabled(true);//添加按钮可以使用
				tableItem.setChecked(true);//复选框被选中
				TableModle tm=(TableModle) tableItem.getData();
				bo1=tm.getBo();
				type=bo1.GetField("DutyTableType").get_NativeValue().toString();//得到选中的对象的类型
				BusinessObject bo=AddDutyDetail.getBo();
				System.out.println("a:"+bo);
				IEnumerator ienum=bo1.get_Relationships().GetEnumerator();
		    	if(ienum!=null){
		    		System.out.println("1");
		    		while(ienum.MoveNext()){
		    			System.out.println("2");
		    			if(ienum.get_Current()!=null){
		    				System.out.println("3");
		    				TableViewer1.setContentProvider(new DutyDetailContentProvider());
							TableViewer1.setLabelProvider(new DutyDetailLabelProvider());
							TableViewer1.setInput(DutyDetailInfor.getDutyDetailInfor(bo));
		    			}
		    		}
		    	}	
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				tableItem=(TableItem) e.item;
				tableItem.setChecked(false);
			}
		});
		
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("\u503C\u73ED\u8868");
		
		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText("\u7C7B\u578B");
		
		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_2.setWidth(96);
		tblclmnNewColumn_2.setText("\u63CF\u8FF0");
		
		TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_3.setWidth(100);
		tblclmnNewColumn_3.setText("\u7F16\u8F91");
		
		
		
		
		Button btnNewButton_2 = new Button(composite_3, SWT.NONE);//控制第二个表单的删除按钮
		btnNewButton_2.setBounds(86, 10, 47, 22);
		btnNewButton_2.setText("\u5220\u9664");
		btnNewButton_2.addSelectionListener(new SelectionAdapter(){//删除按钮监听事件
			public void widgetSelected(SelectionEvent e){
				DetailModel dm=(DetailModel) tableItem1.getData();
				BusinessObject bo=dm.getBo();
				bo.DeleteObject(ConnectionBroker.get_SiteviewApi());
				TableDutyInfor.list.remove(dm);
				TableViewer1.setInput(TableDutyInfor.list);
				TableViewer1.refresh();
				
			}
		});
		
		Label lblNewLabel_2 = new Label(sashForm, SWT.NONE);
		lblNewLabel_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_2.setText("\u503C\u73ED\u4FE1\u606F:");
		
		Composite composite_2 = new Composite(sashForm, SWT.NONE);
		composite_2.setLayout(new FillLayout());
		composite_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		
		TableViewer1 =  new TableViewer(composite_2, SWT.MULTI//值班信息表
				 |SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		table_1 = TableViewer1.getTable();
		table_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		table_1.setHeaderVisible(true);
		table_1.setLinesVisible(true);
		table_1.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				tableItem1=(TableItem) e.item;
			}
			public void widgetDefaultSelected(SelectionEvent e) {
	
			}
		});
		
		
		TableColumn tblclmnNewColumn_4 = new TableColumn(table_1, SWT.NONE);
		tblclmnNewColumn_4.setWidth(100);
		tblclmnNewColumn_4.setText("\u63A5\u6536\u544A\u8B66\u624B\u673A\u53F7\u7801");
		
		TableColumn tblclmnNewColumn_5 = new TableColumn(table_1, SWT.NONE);
		tblclmnNewColumn_5.setWidth(100);
		tblclmnNewColumn_5.setText("\u63A5\u6536\u544A\u8B66\u90AE\u7BB1");
		
		TableColumn tblclmnNewColumn_6 = new TableColumn(table_1, SWT.NONE);
		tblclmnNewColumn_6.setWidth(100);
		tblclmnNewColumn_6.setText("\u65E5\u671F");
		
		TableColumn tblclmnNewColumn_7 = new TableColumn(table_1, SWT.NONE);
		tblclmnNewColumn_7.setWidth(100);
		tblclmnNewColumn_7.setText("\u5F00\u59CB\u65F6\u95F4");
		
		TableColumn tblclmnNewColumn_8 = new TableColumn(table_1, SWT.NONE);
		tblclmnNewColumn_8.setWidth(100);
		tblclmnNewColumn_8.setText("\u7ED3\u675F\u65F6\u95F4");
		
		TableColumn tblclmnNewColumn_9 = new TableColumn(table_1, SWT.NONE);
		tblclmnNewColumn_9.setText("\u7F16\u8F91");
		tblclmnNewColumn_9.setWidth(100);
		sashForm.setWeights(new int[] {25, 17, 180, 39, 19, 179});
		
		TableViewer.setContentProvider(new TableDutyContentProvider());
		TableViewer.setLabelProvider(new TableDutyLabelProvider());
		TableViewer.setInput(TableDutyInfor.getTableDutyInfor());
		

	
		
		
	}
	
	@Override
	public void setFocus() {
		
		
	}
	@Override
	public void doSave(IProgressMonitor monitor) {
		
	}

	@Override
	public void doSaveAs() {
		
	}
}
