package com.wesleyreis.www.visao.util;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import com.wesleyreis.www.modelo.ArquivoSPED;

public class ListModelArquivos extends AbstractListModel<ArquivoSPED> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7097335892895572216L;
	
	private List<ArquivoSPED> listOfFiles;
	
	public ListModelArquivos(){
		listOfFiles = new ArrayList<ArquivoSPED>();
	}

	@Override
	public ArquivoSPED getElementAt(int arg0) {
		return listOfFiles.get(arg0);
	}

	@Override
	public int getSize() {
		return listOfFiles.size();
	}
	
	public void addElemment(ArquivoSPED alement){
		this.listOfFiles.add(alement);
		super.fireContentsChanged(this, this.listOfFiles.size() - 1, this.listOfFiles.size() - 1);
		super.fireIntervalAdded(this, this.listOfFiles.size() - 1, this.listOfFiles.size() - 1);
	}
	
	public void removeElemment(ArquivoSPED alement){
		if(this.listOfFiles.remove(alement)){
			super.fireContentsChanged(this, this.listOfFiles.size(), this.listOfFiles.size());
		}
	}

}
