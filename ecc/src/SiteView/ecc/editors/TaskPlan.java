package SiteView.ecc.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import SiteView.ecc.Activator;
import SiteView.ecc.Control.TaskPlanContentProvider;
import SiteView.ecc.Control.TaskPlanLabelProvider;
import SiteView.ecc.Modle.AbsoluteTimeModel;
import SiteView.ecc.Modle.RelativeTimeModel;
import SiteView.ecc.Modle.TimeQuantumModel;
import SiteView.ecc.data.TaskInfo;
import SiteView.ecc.view.EccTreeControl;

public class TaskPlan extends EditorPart {
	public static final String ID="SiteView.ecc.editors.TaskPlan";
	private Table table;
	private TableItem tableItem;
	public static AbsoluteTimeInput ati = new AbsoluteTimeInput();

	public TaskPlan() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		this.setSite(site);
		this.setInput(input);
		this.setPartName(input.getName());

	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		
		Composite composite = new Composite(sashForm, SWT.NONE);
		composite.setBackground(EccTreeControl.color);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Label label = new Label(composite, SWT.NONE);
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		label.setText("\u4EFB\u52A1\u8BA1\u5212");
		
		Composite composite_1 = new Composite(sashForm, SWT.NONE);
		composite_1.setBackground(EccTreeControl.color);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TableViewer tableViewer = new TableViewer(composite_1, SWT.MULTI | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setBackground(EccTreeControl.color);
		table.setHeaderVisible(true);
		table.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				TableItem tableItem=(TableItem)e.item;
				IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();  
				IEditorPart editor = page.findEditor(ati); 
				if(editor==null){
					if(tableItem.getText().equals("绝对时间任务计划")){
						try {
							page.openEditor(new AbsoluteTimeInput("absolute"), AbsoluteTime.absoluteID);
						} catch (PartInitException e1) {
							e1.printStackTrace();
						}
					}else if(tableItem.getText().equals("时间段任务计划")){
						try {
							page.openEditor(new AbsoluteTimeInput("quantum"),  AbsoluteTime.absoluteID);
						} catch (PartInitException e1) {
							e1.printStackTrace();
						}
					}else if(tableItem.getText().equals("相对时间任务计划")){
						try {
							page.openEditor(new AbsoluteTimeInput("ralative"),  AbsoluteTime.absoluteID);
						} catch (PartInitException e1) {
							e1.printStackTrace();
						}
					}
				}else{
					 if(tableItem.getText().equals("绝对时间任务计划")){
						 AbsoluteTime.name="absolute";
						 ((AbsoluteTime)editor).createTable();
						 ((AbsoluteTime)editor).createLabel();
					 }else if(tableItem.getText().equals("时间段任务计划")){
						 AbsoluteTime.name="quantum";
						 ((AbsoluteTime)editor).createTable();
						 ((AbsoluteTime)editor).createLabel();
					}else if(tableItem.getText().equals("相对时间任务计划")){
						 AbsoluteTime.name="ralative";
						 ((AbsoluteTime)editor).createTable();
						 ((AbsoluteTime)editor).createLabel();
					}
				}
				
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.LEFT);
		tblclmnNewColumn.setText("\u540D\u79F0");
		tblclmnNewColumn.setWidth(150);
		
		TableColumn tableColumn = new TableColumn(table, SWT.LEFT);
		tableColumn.setText("\u8BF4\u660E");
		tableColumn.setWidth(650);
		sashForm.setWeights(new int[] {1, 40});

		tableViewer.setContentProvider(new TaskPlanContentProvider());
		tableViewer.setLabelProvider(new TaskPlanLabelProvider());
		tableViewer.setInput(TaskInfo.getTaskInfo());
	}

	@Override
	public void setFocus() {

	}

}
