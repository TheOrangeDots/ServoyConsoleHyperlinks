package com.tod.eclipse.servoy.console.patternMatchers;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.servoy.eclipse.designer.editor.BaseVisualFormEditor;
import com.servoy.eclipse.designer.editor.BaseVisualFormEditorDesignPage;
import com.servoy.eclipse.model.ServoyModelFinder;
import com.servoy.eclipse.model.repository.SolutionSerializer;
import com.servoy.eclipse.ui.util.EditorUtil;
import com.servoy.j2db.FlattenedSolution;
import com.servoy.j2db.persistence.BaseComponent;
import com.servoy.j2db.persistence.Form;
import com.servoy.j2db.persistence.IPersist;
import com.servoy.j2db.persistence.IPersistVisitor;
import com.servoy.j2db.util.Debug;
import com.servoy.j2db.util.Pair;

public class ElementHyperlink implements IHyperlink {

	private String form;
	private String element;
	
	/**
	 * Constructor for ElementHyperlink.
	 */
	public ElementHyperlink(String form, String element) {
		this.form = form;
		this.element = element;
	}
	
	@Override
	public void linkEntered() {
	}

	@Override
	public void linkExited() {
	}

	@Override
	public void linkActivated() {
		FlattenedSolution fs = ServoyModelFinder.getServoyModel().getActiveProject().getEditingFlattenedSolution();
		Form f = fs.getForm(form);
		
		if (f == null) {
			return;
		}
		
		IEditorPart ep = EditorUtil.openFormDesignEditor(f);
		
		if (this.element == null) {
			return;
		}

		IPersist persist = (IPersist) fs.getFlattenedForm(f).acceptVisitor(new IPersistVisitor() {
			public Object visit(IPersist o) {
				if (o instanceof BaseComponent) {
					BaseComponent bc = (BaseComponent) o;
					if (element.equals(bc.getName())) {
						return bc;
					}
				}
				return IPersistVisitor.CONTINUE_TRAVERSAL;
			}
		});

		if (persist == null) {
			return;
		}
	
		BaseVisualFormEditor fe = (BaseVisualFormEditor) ep;
		((BaseVisualFormEditorDesignPage)fe.getGraphicaleditor()).showPersist(persist);

		//Better alternative that doesn't depend on including Servoy specific classes, but it doesn't work...
//		GraphicalViewer gv = (GraphicalViewer) ((GraphicalEditor)ep).getAdapter(GraphicalViewer.class);
//		Object editPart = gv.getRootEditPart().getViewer().getEditPartRegistry().get(persist);
//		
//		if (editPart instanceof EditPart) {
//			// select the marked element
//			gv.setSelection(new StructuredSelection(editPart));
//			gv.reveal((EditPart)editPart);
//		}
		
		
		
//		IGotoMarker gm = (IGotoMarker) fe.getAdapter(IGotoMarker.class);
//		
//		IMarker marker = null;
//		try {
//			Pair<String, String> pathPair = SolutionSerializer.getFilePath(persist, true);
//			Path path = new Path(pathPair.getLeft() + pathPair.getRight());
//			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
//			if (file.exists()) {
//				marker = file.createMarker("com.tod.console.hyperlink2selection.helpermarker");
//				HashMap<String, String> attributes = new HashMap<String, String>();
//				attributes.put("elementUuid", persist.getUUID().toString());
//				marker.setAttributes(attributes);
//				//IDE.gotoMarker(ep, marker);
//				gm.gotoMarker(marker);
//			}
//		} catch (CoreException e) {
//			Debug.error("Failure selecting element", e);
//		} finally {
//			if (marker != null)
//				try {
//					marker.delete();
//				} catch (CoreException e) {
//					// ignore
//				}
//		}
	}
}