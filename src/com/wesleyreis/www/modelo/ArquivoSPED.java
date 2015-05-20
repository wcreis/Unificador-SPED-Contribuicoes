package com.wesleyreis.www.modelo;

/**
 * Classe Simples para Armazenar Informações sobre o Arquivo
 * a ser trabalhado.
 * Implemeta Comparable, para utilizar o Método CompareTo.
 * 
 * @author Wesley Reis
 *
 */
public class ArquivoSPED implements Comparable<ArquivoSPED>{
	private String nomeArquivo;
	private String caminhoDoArquivo;
	private StringBuilder sbArquivo;
	private String sCOD_EST;
	private String sCNPJ;
	
	public String getNomeArquivo() {
		return nomeArquivo;
	}
	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}
	public String getCaminhoDoArquivo() {
		return caminhoDoArquivo;
	}
	public void setCaminhoDoArquivo(String caminhoDoArquivo) {
		this.caminhoDoArquivo = caminhoDoArquivo;
	}
	
	public StringBuilder getSbArquivo() {
		return sbArquivo;
	}
	public void setSbArquivo(StringBuilder sbArquivo) {
		this.sbArquivo = sbArquivo;
	}
	
	public String getsCOD_EST() {
		return sCOD_EST;
	}
	public void setsCOD_EST(String sCOD_EST) {
		this.sCOD_EST = sCOD_EST;
	}
	public String getsCNPJ() {
		return sCNPJ;
	}
	public void setsCNPJ(String sCNPJ) {
		this.sCNPJ = sCNPJ;
	}
	@Override
	public String toString() {
		return "Nome do Arquivo: " + nomeArquivo
				+ ", Código do Estabelecimento: " + sCOD_EST + ", CNPJ: "+sCNPJ;
	}
	@Override
	public int compareTo(ArquivoSPED o) {
		if(this.sCNPJ.compareToIgnoreCase(o.getsCNPJ())!=0){
			return 1;
		}
		return 0;
	}
	
	
	
}
