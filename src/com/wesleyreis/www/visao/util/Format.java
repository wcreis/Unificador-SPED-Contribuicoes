package com.wesleyreis.www.visao.util;
/**
 * Classe utilitaria, para formatacoes
 * @author Wesley Reis
 *
 */
public abstract class Format {

	/**
	 * Formata um CNPJ 00000000000000 para 00.000.000/0000-00
	 * @param String CNPJ 00000000000000
	 * @return 00.000.000/0000-00
	 */
	public static String sNumberToCNPJ(String sCNPJ){
		if(sCNPJ.length()==14){
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<14;i++){
				switch (i){
				case 1: case  4:
					sb.append(sCNPJ.charAt(i));
					sb.append(".");
					break;
				case 7:
					sb.append(sCNPJ.charAt(i));
					sb.append("/");
					break;
				case 11:
					sb.append(sCNPJ.charAt(i));
					sb.append("-");
					break;
				default:
					sb.append(sCNPJ.charAt(i));
				}
			}
			sCNPJ = sb.toString();
		}
		return sCNPJ;
	}
}
