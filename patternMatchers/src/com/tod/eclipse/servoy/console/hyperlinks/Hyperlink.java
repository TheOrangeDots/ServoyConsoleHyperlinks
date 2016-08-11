/*
The MIT License (MIT)

Copyright (c) 2016 The Orange Dots

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.tod.eclipse.servoy.console.hyperlinks;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.ide.IGotoMarker;

import com.servoy.eclipse.model.ServoyModelFinder;
import com.servoy.eclipse.model.repository.SolutionSerializer;
import com.servoy.eclipse.ui.util.EditorUtil;
import com.servoy.j2db.FlattenedSolution;
import com.servoy.j2db.persistence.AbstractRepository;
import com.servoy.j2db.persistence.BaseComponent;
import com.servoy.j2db.persistence.Form;
import com.servoy.j2db.persistence.IPersist;
import com.servoy.j2db.persistence.IPersistVisitor;
import com.servoy.j2db.util.Pair;
import com.servoy.j2db.util.UUID;

public class Hyperlink implements IHyperlink {

	private String form;
	private String element;
	
	/**
	 * Constructor for ElementHyperlink.
	 */
	public Hyperlink(String form, String element) {
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
			final String elementName = this.element.substring(1);
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
		
		IPersist persist = AbstractRepository.searchPersist(f, persistUUID);
		IGotoMarker gm = (IGotoMarker) ep.getAdapter(IGotoMarker.class);
		
		IMarker marker = null;
		try {
			Pair<String, String> pathPair = SolutionSerializer.getFilePath(persist, true);
			Path path = new Path(pathPair.getLeft() + pathPair.getRight());
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			if (file.exists()) {
				marker = file.createMarker("com.tod.console.hyperlink2selection.helpermarker");
				HashMap<String, String> attributes = new HashMap<String, String>();
				attributes.put("elementUuid", persist.getUUID().toString());
				marker.setAttributes(attributes);
				gm.gotoMarker(marker);
			}
		} catch (CoreException e) {
			System.out.println("Failure selecting element");
			e.printStackTrace();
		} finally {
			if (marker != null)
				try {
					marker.delete();
				} catch (CoreException e) {
					e.printStackTrace();
				}
		}
	}
}