package com.wesleyreis.www.controle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.wesleyreis.www.modelo.ArquivoSPED;
/**
 * Classe Responsavel por Percorrer todos os arquivos e unifica-los.
 * @author Wesley Reis
 *
 */
public class Unifica extends Observable implements Runnable{

	private ArquivoSPED arquivoMatriz;
	private List<ArquivoSPED> arquivosFiliais;
	private StringBuilder sbArquivoUnificado;
	private File currentDiretory;
	private static String REG_INI_PRIM_PARTE_FILIAL = "|0140|";
	private static String REG_FIM_PRIM_PARTE_FILIAL = "|0990|";
	private static String REG_INI_SEG_PARTE_MATRIZ =  "|A001|";
	private static String REG_INI_SEG_PARTE_FILIAL =  "|C010|";
	private static String REG_FIM_SEG_PARTE =         "|C990|";
	private static String REG_INI_ULTIMA_PARTE =      "|D001|";
	private static String REG_FIM_ULTIMA_PARTE =      "|9999|";

	/**
	 * Construtor Recebendo as Dependencias para o Arquivo.
	 * @param ArquivoSPED arquivoMatriz
	 * @param List<ArquivoSPED> arquivosFiliais
	 * @param Observer observador
	 */
	public Unifica(ArquivoSPED arquivoMatriz, List<ArquivoSPED> arquivosFiliais, Observer observador,File currentDiretory){
		this.arquivoMatriz = arquivoMatriz;
		this.arquivosFiliais = arquivosFiliais;
		addObserver(observador);
		this.currentDiretory = currentDiretory;
	}

	/**
	 * Inicia o Processo e ao término
	 * notifica os observadores e as Threads
	 * que estejam aguardando.
	 */
	@Override
	public synchronized void run() {
		inicializaContadores();
		unificarArquivos();

		setChanged();
		notifyObservers(2);
		notifyAll();//Notifica que ja terminou seu processamento.
	}

	private void inicializaContadores() {
		Contadores.QTD_LIN_REG_0990 = 0;
		Contadores.QTD_LIN_REG_C990 = 0;
		Contadores.QTD_LIN_REG_9999 = 0;
	}

	/**
	 * Controla o fluxo de Extração e geração do novo Arquivo.
	 */
	private void unificarArquivos(){
		sbArquivoUnificado = new StringBuilder();
		String[] linhasMatriz = arquivoMatriz.getSbArquivo().toString().split("(\r\n|\n)", -1);

		//Primeiro
		_1_LinhasPrimParteMatriz(linhasMatriz);

		//Segundo
		List<String[]> linhasArquivoFiliais = new ArrayList<String[]>();
		_2_LinhasPrimParteFiliais(linhasArquivoFiliais);
		//Atualiza o Registro |0990|QTD_LIN_REG_0|
		sbArquivoUnificado.append("|0990|"+ Contadores.QTD_LIN_REG_0990 +"|\n");
		Contadores.QTD_LIN_REG_9999++;

		//Terceiro
		_3_LinhasSegParteMatriz(linhasMatriz);

		//Quarto
		_4_LinhasSegParteFiliais(linhasArquivoFiliais);
		//Atualiza o Registro |C990|QTD_LIN_REG_0|
		sbArquivoUnificado.append("|C990|"+ Contadores.QTD_LIN_REG_C990 +"|\n");
		Contadores.QTD_LIN_REG_9999++;

		//Quinto
		_5_LinhasParteFinal(linhasMatriz);

		//Atualiza o Registro |C990|QTD_LIN_REG_0|
		sbArquivoUnificado.append("|9999|"+ Contadores.QTD_LIN_REG_9999 +"|");
		String[] linhasArquivoUnico = sbArquivoUnificado.toString().split("(\r\n|\n)", -1);

		//Sexto
		_6_setSaveArquivoUnificado(linhasArquivoUnico);

	}

	/**
	 * Extrai as Linhas comecando no Registro |0000| ao Registro anterior ao |0990|
	 * do Arquivo da Matriz.
	 * @param linhasMatriz
	 * @throws NumberFormatException
	 */
	private void _1_LinhasPrimParteMatriz(String[] linhasMatriz)
			throws NumberFormatException {
		for (String linhaMatriz : linhasMatriz) {
			if(linhaMatriz.substring(0, 6).compareToIgnoreCase("|0990|")!=0){ //Percorrerá do Registro |0000| até o Registro anterior ao |0990|.
				sbArquivoUnificado.append(linhaMatriz+"\n");
				Contadores.QTD_LIN_REG_9999++;
			}else{
				String[] linha = linhaMatriz.split("(\\|)", -1);
				Contadores.QTD_LIN_REG_0990 = Integer.parseInt(linha[2]);
				Contadores.QTD_LIN_REG_9999++;
				break;
			}
		}
	}

	/**
	 * Extrai as Linhas comecando no Registro |0140| ao Registro anterior ao |0990|
	 * dos Arquivos das Filiais.
	 * @param List<String[]> linhasArquivoFiliais
	 */
	private void _2_LinhasPrimParteFiliais(
			List<String[]> linhasArquivoFiliais) {
		for(ArquivoSPED arquivo: arquivosFiliais){
			String[] linhasFilial = arquivo.getSbArquivo().toString().split("(\r\n|\n)", -1);
			linhasArquivoFiliais.add(linhasFilial);
		}

		for(String[] linhasFilial : linhasArquivoFiliais){
			int indexInicioPrimParteFilial = 0;
			int indexFimPrimParteFilial = 0;
			for(int i=0; i<linhasFilial.length-1;i++){
				String linhaFilial = linhasFilial[i];
				if(linhaFilial.substring(0, 6).compareToIgnoreCase(REG_INI_PRIM_PARTE_FILIAL)==0){
					System.out.println(linhaFilial);
					indexInicioPrimParteFilial = i;
				}
				if(linhaFilial.substring(0, 6).compareToIgnoreCase(REG_FIM_PRIM_PARTE_FILIAL)==0){
					indexFimPrimParteFilial = i;
					//String[] linha = linhaFilial.split("(\\|)", -1);
					Contadores.QTD_LIN_REG_0990 += (indexFimPrimParteFilial-indexInicioPrimParteFilial);
				}

			}	
			for(int j=indexInicioPrimParteFilial; j<indexFimPrimParteFilial; j++){
				sbArquivoUnificado.append(linhasFilial[j]+"\n");
				Contadores.QTD_LIN_REG_9999++;
			}
		}
	}

	/**
	 * Extrai as Linhas comecando no Registro |A001| ao Registro anterior ao |C990|
	 * do Arquivo da Matriz.
	 * @param linhasMatriz
	 * @throws NumberFormatException
	 */
	private void _3_LinhasSegParteMatriz(String[] linhasMatriz)
			throws NumberFormatException {
		//Segunda Parte do Aquivo Matriz
		int indexInicioSegParteMatriz = 0;
		int indexFimSegParteMatriz = 0;
		for(int i=0; i<linhasMatriz.length-1;i++){
			String linhaMatriz = linhasMatriz[i];
			if(linhaMatriz.substring(0, 6).compareToIgnoreCase(REG_INI_SEG_PARTE_MATRIZ)==0){
				indexInicioSegParteMatriz = i;
			}
			if(linhaMatriz.substring(0, 6).compareToIgnoreCase(REG_FIM_SEG_PARTE)==0){
				indexFimSegParteMatriz = i;
				String[] linha = linhaMatriz.split("(\\|)", -1);
				Contadores.QTD_LIN_REG_C990 += Integer.parseInt(linha[2]);
			}

		}

		for(int j=indexInicioSegParteMatriz; j<indexFimSegParteMatriz; j++){
			sbArquivoUnificado.append(linhasMatriz[j]+"\n");
			Contadores.QTD_LIN_REG_9999++;
		}
	}

	/**
	 * Extrai as Linhas comecando no Registro |C010| ao Registro anterior ao |C990|
	 * dos Arquivos das Filiais.
	 * @param linhasArquivoFiliais
	 */
	private void _4_LinhasSegParteFiliais(List<String[]> linhasArquivoFiliais) {
		//Segunda Parte do Arquivo Filiais
		for(String[] linhasFilial : linhasArquivoFiliais){
			int indexInicioSegParteFilial = 0;
			int indexFimSegParteFilial = 0;
			for(int i=0; i<linhasFilial.length-1;i++){
				String linhaFilial = linhasFilial[i];
				if(linhaFilial.substring(0, 6).compareToIgnoreCase(REG_INI_SEG_PARTE_FILIAL)==0){
					indexInicioSegParteFilial = i;
				}
				if(linhaFilial.substring(0, 6).compareToIgnoreCase(REG_FIM_SEG_PARTE)==0){
					indexFimSegParteFilial = i;
					Contadores.QTD_LIN_REG_C990 += indexFimSegParteFilial-indexInicioSegParteFilial;
				}
			}
			for(int j=indexInicioSegParteFilial; j<indexFimSegParteFilial; j++){
				sbArquivoUnificado.append(linhasFilial[j]+"\n");
				Contadores.QTD_LIN_REG_9999++;
			}
		}
	}

	/**
	 * Extrai as Linhas comecando no Registro |D001| ao Registro anterior ao |9999|
	 * do Arquivo da Matriz.
	 * @param linhasMatriz
	 */
	private void _5_LinhasParteFinal(String[] linhasMatriz) {
		//Finaliza a Juncao dos Registros
		int indexInicioUltimaParte = 0;
		int indexFimUltimaParte = 0;
		for(int i=0; i<linhasMatriz.length-1;i++){
			String linhaMatriz = linhasMatriz[i];
			if(linhaMatriz.substring(0, 6).compareToIgnoreCase(REG_INI_ULTIMA_PARTE)==0){
				indexInicioUltimaParte = i;
			}
			if(linhaMatriz.substring(0, 6).compareToIgnoreCase(REG_FIM_ULTIMA_PARTE)==0){
				indexFimUltimaParte = i;
			}
		}

		for(int j=indexInicioUltimaParte; j<indexFimUltimaParte; j++){
			sbArquivoUnificado.append(linhasMatriz[j]+"\n");
			Contadores.QTD_LIN_REG_9999++;
		}
	}

	/**
	 * Salva o novo Arquivo no Disco do Computador, junto aos
	 * Arquivos Originais, com o nome: EFD-CONTRIBUICOES-UNICO.txt.
	 * @param String[] linhasArquivoUnico
	 */
	private void _6_setSaveArquivoUnificado(String[] linhasArquivoUnico){

		File arquivoUnificado = new File(this.currentDiretory,"EFD-CONTRIBUICOES-UNICO.txt");
		
		try {
			if(arquivoUnificado.exists()){
				arquivoUnificado.delete();
			}
			
			if(arquivoUnificado.createNewFile()){
				FileWriter fileW = new FileWriter (arquivoUnificado);
				BufferedWriter fr = null;
				fr = new BufferedWriter(fileW);

				for(String linhas :linhasArquivoUnico){
					try {
						fr.write(linhas);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					try {
						fr.newLine();
					} catch (IOException e) {
						e.printStackTrace();
					}//passa para a proxima linha
				}
				//Salva as Alterações
				try {
					fr.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}  
				//Fecha a escrita no Arquivo
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e3) {
			e3.printStackTrace();
		}
	}

	public String[] getArquivoUnificado(){
		String[] linhasArquivoUnico = sbArquivoUnificado.toString().split("(\r\n|\n)", -1);

		return linhasArquivoUnico;
	}




}
