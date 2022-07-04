package com.example.model;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class NameTableComposite extends Composite {
	protected Shell parentShell;
	
	private List<byte[]> nesFormattedNameTables;
	
	public NameTableComposite(Composite parent, int style, Shell parentShell) {
		super(parent, style);
		this.parentShell = parentShell;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
