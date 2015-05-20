package com.wesleyreis.www.controle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Classe Respons�vel por Ler e retornar um StringBuilder do Conte�do de um arquivo Texto.
 * 
 * Esta Classe Implementa os padr�es: Baixo acoplamento(Low Coupling), n�o fazendo chamadas externas
 * a outras Classes mut�veis.
 * Tamb�m o Padr�o Observer ou Publisher-Subscriber ou Event Generator e Dependents, 
 * que s�o sinonimos encontrados nas documenta��es.
 * 
 * @author Wesley Reis
 *
 */
public class LerArquivoTxt extends Observable  implements Runnable{
	
	private static String caminhoArquivo="";
	private static StringBuilder sbArquivoTotal;
	private int tipoArquivo;
	
	/**
	 * 
	 * @param String: caminhoArquivo
	 * @param int: tipoArquivo (para ser Usado com o padr�o Observer)
	 * @param Observer: observador (Objeto que deseja acompanhar o processamento da Classe)
	 */
	public LerArquivoTxt(String caminhoArquivo, int tipoArquivo, Observer observador){
		LerArquivoTxt.caminhoArquivo = caminhoArquivo;
		this.tipoArquivo = tipoArquivo;
		addObserver(observador);
	}

	@Override
	public synchronized void run(){
		try {
			getStringArquivoTXT();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setChanged();
		notifyObservers(this.tipoArquivo);
		notifyAll();//Notifica que ja terminou seu processamento.
	}
	/**
	 * @return StringBuilder
	 * @throws IOException
	 */
	private static StringBuilder getStringArquivoTXT() throws IOException{
		
		File arq = new File(caminhoArquivo);
		sbArquivoTotal = new StringBuilder();
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		
		try {
			fileReader = new FileReader(arq);

			bufferedReader =	new BufferedReader(fileReader);
			String linhaLida="";
			while ( ( linhaLida = bufferedReader.readLine() ) != null) {
				sbArquivoTotal.append(linhaLida);
				sbArquivoTotal.append("\n");
			}
			fileReader.close();
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fileReader!=null){
				fileReader.close();
			}
			if(bufferedReader!=null){
				bufferedReader.close();
			}
		}

		return sbArquivoTotal;
	}
	/**
	 * 
	 * @return StringBuilder
	 */
	public StringBuilder getSbArquivoTotal() {
		return sbArquivoTotal;
	}
}
