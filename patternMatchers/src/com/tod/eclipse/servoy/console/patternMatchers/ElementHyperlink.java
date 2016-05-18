package com.tod.eclipse.servoy.console.patternMatchers;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.console.IHyperlink;

import com.servoy.eclipse.designer.editor.BaseVisualFormEditor;
import com.servoy.eclipse.designer.editor.BaseVisualFormEditorDesignPage;
import com.servoy.eclipse.model.ServoyModelFinder;
import com.servoy.eclipse.ui.util.EditorUtil;
import com.servoy.j2db.FlattenedSolution;
import com.servoy.j2db.persistence.AbstractRepository;
import com.servoy.j2db.persistence.BaseComponent;
import com.servoy.j2db.persistence.Form;
import com.servoy.j2db.persistence.IPersist;
import com.servoy.j2db.persistence.IPersistVisitor;
import com.servoy.j2db.util.UUID;

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
		UUID persistUUID = null;
		
		if (f == null) {
			return;
		}
		
		IEditorPart ep = EditorUtil.openFormDesignEditor(f);
		
		if (this.element == null) {
			return;
		}
		
		if (this.element.startsWith("<")) {
			persistUUID = UUID.fromString(this.element.substring(1, this.element.length() - 1));
		} else {
			String elementName = this.element.substring(1);
			IPersist persist = (IPersist) fs.getFlattenedForm(f).acceptVisitor(new IPersistVisitor() {
				public Object visit(IPersist o) {
					if (o instanceof BaseComponent) {
						BaseComponent bc = (BaseComponent) o;
						if (elementName.equals(bc.getName())) {
							return bc;
						}
					}
					return IPersistVisitor.CONTINUE_TRAVERSAL;
				}
			});
			if (persist != null) {
				persistUUID = persist.getUUID();
			}
		}

		if (persistUUID == null) {
			return;
		}
		
		BaseVisualFormEditor fe = (BaseVisualFormEditor) ep;
		((BaseVisualFormEditorDesignPage)fe.getGraphicaleditor()).showPersist(AbstractRepository.searchPersist(f, persistUUID));
	}
}