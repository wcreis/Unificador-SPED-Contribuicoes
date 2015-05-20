package com.wesleyreis.www.visao;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import com.wesleyreis.www.controle.LerArquivoTxt;
import com.wesleyreis.www.controle.Unifica;
import com.wesleyreis.www.modelo.ArquivoSPED;
import com.wesleyreis.www.visao.util.Format;
import com.wesleyreis.www.visao.util.ListModelArquivos;

public class Main implements Observer{

	private JFrame frmUnificadorEfd;
	private JInternalFrame internalFrame;
	private JDesktopPane desktopPane;
	private JPanel pnlGeral;
	private JPanel pnlArquivos;
	private JPanel pnlArquivoMartriz;
	private JLabel label_1;
	private JTextField txtFileMatriz;
	private JButton btnSearchFileMatriz;
	private JFileChooser jFileChooser;
	private FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivo TXT", "txt");
	private JTextField txtFileFilial;
	private JButton btnSearchArquivosFiliais;
	private JList<ArquivoSPED> listArquivos;
	private ListModelArquivos listModel;
	private JLabel lblCaminhoArquivoFilial;

	private LerArquivoTxt leitor;
	private Unifica unificador;
	private ArquivoSPED arquivoMatriz;
	private List<ArquivoSPED> arquivosFiliais = new ArrayList<>();
	private JPanel pnlArquivosFiliais;
	private JLabel lblNumeroInsert;
	private int numeroArquivosFiliais=1;
	private File ultimoArquivoAberto;
	private JButton btnOk;
	private JButton btnJuntarArquivos;
	private JLabel lblMsgArquivo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frmUnificadorEfd.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmUnificadorEfd = new JFrame();
		frmUnificadorEfd.setTitle("Unificador EFD - Contribui\u00E7\u00F5es");
		frmUnificadorEfd.setType(Type.UTILITY);
		frmUnificadorEfd.setResizable(false);
		frmUnificadorEfd.setBounds(100, 100, 800, 600);
		frmUnificadorEfd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmUnificadorEfd.getContentPane().setLayout(null);


		/**
		 * Inicializa a Variável de Localização de Arquivos
		 */
		jFileChooser = new JFileChooser();
		jFileChooser.setFileFilter(filter);
		jFileChooser.setAcceptAllFileFilterUsed(false); //Aceita Apenas os Arquivos do Filtro

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (UnsupportedLookAndFeelException e1) {
				e1.printStackTrace();
			}
		}


		desktopPane = new JDesktopPane();
		desktopPane.setBounds(0, 0, 794, 571);
		frmUnificadorEfd.getContentPane().add(desktopPane);

		internalFrame = new JInternalFrame("");
		((BasicInternalFrameUI)internalFrame.getUI()).setNorthPane(null); //Ocultar o painel superior 
		internalFrame.setVisible(true);
		internalFrame.setBorder(UIManager.getBorder("TitledBorder.border"));
		internalFrame.setMaximizable(true);
		internalFrame.setFrameIcon(null);
		internalFrame.setBounds(6, 6, 782, 559);
		desktopPane.add(internalFrame);
		internalFrame.getContentPane().setLayout(null);

		pnlGeral = new JPanel();
		pnlGeral.setBounds(6, 16, 750, 493);
		internalFrame.getContentPane().add(pnlGeral);
		pnlGeral.setLayout(null);

		pnlArquivos = new JPanel();
		pnlArquivos.setBounds(6, 6, 750, 487);
		pnlGeral.add(pnlArquivos);
		pnlArquivos.setLayout(null);

		pnlArquivoMartriz = new JPanel();
		pnlArquivoMartriz.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Arquivo da Matriz", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(59, 59, 59)));
		pnlArquivoMartriz.setBounds(0, 0, 744, 60);
		pnlArquivos.add(pnlArquivoMartriz);
		pnlArquivoMartriz.setLayout(null);

		label_1 = new JLabel("Escolha o Arquivo da Matriz:");
		label_1.setBounds(6, 19, 181, 30);
		label_1.setHorizontalAlignment(SwingConstants.LEFT);
		label_1.setFont(new Font("SansSerif", Font.PLAIN, 14));
		pnlArquivoMartriz.add(label_1);

		txtFileMatriz = new JTextField();
		txtFileMatriz.setBackground(Color.WHITE);
		txtFileMatriz.setEnabled(false);
		txtFileMatriz.setEditable(false);
		txtFileMatriz.setFont(new Font("SansSerif", Font.PLAIN, 14));
		txtFileMatriz.setHorizontalAlignment(SwingConstants.LEFT);
		txtFileMatriz.setBounds(199, 19, 480, 30);
		pnlArquivoMartriz.add(txtFileMatriz);
		txtFileMatriz.setColumns(10);

		btnSearchFileMatriz = new JButton("");
		btnSearchFileMatriz.setBounds(691, 19, 30, 30);
		pnlArquivoMartriz.add(btnSearchFileMatriz);
		btnSearchFileMatriz.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String caminho = "";


				int retorno = jFileChooser.showSaveDialog(internalFrame); // showSaveDialog retorna um inteiro , e ele ira determinar que o chooser será para salvar.
				if (retorno==JFileChooser.APPROVE_OPTION){

					String nomeArquivo = jFileChooser.getSelectedFile().getName();
					caminho = jFileChooser.getSelectedFile().getAbsolutePath();  // o getSelectedFile pega o arquivo e o getAbsolutePath retorna uma string contendo o endereço.
					txtFileMatriz.setText(nomeArquivo);
					arquivoMatriz = new ArquivoSPED();
					processaArquivoMatriz(caminho);
					ultimoArquivoAberto = new File(jFileChooser.getSelectedFile().getAbsolutePath());  
					jFileChooser.setCurrentDirectory(ultimoArquivoAberto);
					habilitaBotoes(0);
				}
			}
		});
		btnSearchFileMatriz.setIcon(new ImageIcon(Main.class.getResource("/images/search-icon.png")));

		listModel = new ListModelArquivos();

		pnlArquivosFiliais = new JPanel();
		pnlArquivosFiliais.setLayout(null);
		pnlArquivosFiliais.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Arquivo(s) da(s) Filia(l)(is)", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(59, 59, 59)));
		pnlArquivosFiliais.setBounds(0, 72, 744, 313);
		pnlArquivos.add(pnlArquivosFiliais);

		JLabel lblEscolhaOsArquivos = new JLabel("Escolha o Arquivo da Filial - ");
		lblEscolhaOsArquivos.setBounds(6, 19, 180, 30);
		pnlArquivosFiliais.add(lblEscolhaOsArquivos);
		lblEscolhaOsArquivos.setHorizontalAlignment(SwingConstants.LEFT);
		lblEscolhaOsArquivos.setFont(new Font("SansSerif", Font.PLAIN, 14));

		lblNumeroInsert = new JLabel("1");
		lblNumeroInsert.setHorizontalAlignment(SwingConstants.CENTER);
		lblNumeroInsert.setFont(new Font("SansSerif", Font.PLAIN, 14));
		lblNumeroInsert.setBounds(183, 20, 21, 30);
		pnlArquivosFiliais.add(lblNumeroInsert);

		txtFileFilial = new JTextField();
		txtFileFilial.setBackground(Color.WHITE);
		txtFileFilial.setEnabled(false);
		txtFileFilial.setEditable(false);
		txtFileFilial.setBounds(201, 19, 410, 30);
		pnlArquivosFiliais.add(txtFileFilial);
		txtFileFilial.setHorizontalAlignment(SwingConstants.LEFT);
		txtFileFilial.setColumns(10);



		btnSearchArquivosFiliais = new JButton("");
		btnSearchArquivosFiliais.setEnabled(false);
		btnSearchArquivosFiliais.setBounds(623, 19, 30, 30);
		pnlArquivosFiliais.add(btnSearchArquivosFiliais);
		btnSearchArquivosFiliais.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String caminho = "";

				int retorno = jFileChooser.showSaveDialog(internalFrame); // showSaveDialog retorna um inteiro , e ele ira determinar que o chooser será para salvar.
				if (retorno==JFileChooser.APPROVE_OPTION){
					String nomeArquivo = jFileChooser.getSelectedFile().getName();
					caminho = jFileChooser.getSelectedFile().getAbsolutePath();  // o getSelectedFile pega o arquivo e o getAbsolutePath retorna uma string contendo o endereço.
					txtFileFilial.setText(nomeArquivo);
					lblCaminhoArquivoFilial.setText(caminho);
					habilitaBotoes(1);
				}
			}
		});
		btnSearchArquivosFiliais.setIcon(new ImageIcon(Main.class.getResource("/images/search-icon.png")));

		lblCaminhoArquivoFilial = new JLabel("");
		lblCaminhoArquivoFilial.setBounds(0, 0, 0, 0);
		pnlArquivosFiliais.add(lblCaminhoArquivoFilial);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(16, 61, 717, 246);
		pnlArquivosFiliais.add(scrollPane);

		listArquivos = new JList<ArquivoSPED>(listModel);
		listArquivos.setVisibleRowCount(15);
		listArquivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(listArquivos);

		btnOk = new JButton("ADD");
		btnOk.setEnabled(false);
		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(lblCaminhoArquivoFilial.getText()!="" && txtFileFilial.getText()!=""){
					processaArquivoFilial(lblCaminhoArquivoFilial.getText());
				}else{
					JOptionPane.showMessageDialog(null, "É preciso localizar um arquivo primeiro!!!", "Arquivo não Informado!!!", 1);
				}
			}
		});
		btnOk.setBounds(665, 19, 68, 29);
		pnlArquivosFiliais.add(btnOk);
		
				btnJuntarArquivos = new JButton("Gerar Arquivo \u00DAnico");
				btnJuntarArquivos.setBounds(273, 439, 193, 28);
				pnlArquivos.add(btnJuntarArquivos);
				btnJuntarArquivos.setEnabled(false);
				
				lblMsgArquivo = new JLabel("");
				lblMsgArquivo.setHorizontalAlignment(SwingConstants.CENTER);
				lblMsgArquivo.setBounds(10, 391, 734, 36);
				pnlArquivos.add(lblMsgArquivo);
				btnJuntarArquivos.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						processaUnificacao();
					}
				});
	}

	private void processaArquivoMatriz(String caminho) {
		leitor = new LerArquivoTxt(caminho,0,this);
		Thread tMatriz = new Thread(leitor);
		tMatriz.setName("ProcessaArquivoMatriz");
		tMatriz.run();
	}

	private void atualizaTelaMatriz(){
		String[] linhas = arquivoMatriz.getSbArquivo().toString().split("(\r\n|\n)", -1);
		for (String string : linhas) {

			if(string.substring(0, 6).compareToIgnoreCase("|0140|")==0){
				String[] linha = string.split("(\\|)", -1);
				arquivoMatriz.setsCOD_EST(linha[2]);
				arquivoMatriz.setsCNPJ(linha[4]);
				txtFileMatriz.setText("Código do Estabelecimento: "+arquivoMatriz.getsCOD_EST()+", CNPJ: "+Format.sNumberToCNPJ(arquivoMatriz.getsCNPJ()));
				System.out.println("Código do Estabelecimento: "+arquivoMatriz.getsCOD_EST()+", CNPJ: "+Format.sNumberToCNPJ(arquivoMatriz.getsCNPJ()));
				break;
			}
		}
	}

	private void processaArquivoFilial(String caminho){
		leitor = new LerArquivoTxt(caminho,1,this);
		Thread tFilial = new Thread(leitor);
		tFilial.setName("ProcessaArquivoFilial");
		tFilial.run();
	}

	private void atualizaTelaFilial(ArquivoSPED arquivoFilial){
		String[] linhas = arquivoFilial.getSbArquivo().toString().split("(\r\n|\n)", -1);
		for (String string : linhas) {
			if(string.substring(0, 6).compareToIgnoreCase("|0140|")==0){
				String[] linha = string.split("(\\|)", -1);
				arquivoFilial.setsCOD_EST(linha[2]);
				arquivoFilial.setsCNPJ(linha[4]);

				if(arquivoFilial.compareTo(arquivoMatriz)!=0){ //Verifica se o arquivo atual é o Diferente do Arquivo da Matriz.

					boolean arquivoExiste = false;
					for(ArquivoSPED arquivo:arquivosFiliais){
						if(arquivo.compareTo(arquivoFilial)==0){//Verifica se o Arquivo atual ja consta na lista.
							arquivoExiste = true;
							JOptionPane.showMessageDialog(this.frmUnificadorEfd, "O Arquivo desta Filial já foi importado", "Arquivo da Repetido", 0);
							break;
						}
					}

					if(!arquivoExiste){
						arquivosFiliais.add(arquivoFilial);

						numeroArquivosFiliais++;
						lblNumeroInsert.setText(""+numeroArquivosFiliais);
						arquivoFilial.setNomeArquivo(txtFileFilial.getText());
						arquivoFilial.setCaminhoDoArquivo(lblCaminhoArquivoFilial.getText());
						listModel.addElemment(arquivoFilial);
						habilitaBotoes(0);
					}

				}else{
					JOptionPane.showMessageDialog(this.frmUnificadorEfd, "Este Arquivo é o mesmo que o da Matriz", "Arquivo da Matriz", 0);
				}
				break;
			}
		}
	}
	
	private void processaUnificacao(){
		unificador = new Unifica(arquivoMatriz, arquivosFiliais, this,jFileChooser.getCurrentDirectory());
		Thread tUnificador = new Thread(unificador);
		tUnificador.setName("ThreadUnificador");
		tUnificador.run();
	}

	/**
	 * Aqui implemento os Padrões Observer, Alta Coesao (High Cohesion).
	 *
	 * Após a Leitura e Carregamento do Arquivo, a Thread informa que terminou
	 * para que o Observer entre e possa pegar o Resultado.
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		switch ((int)arg1){
		case 0://Matriz
			arquivoMatriz.setSbArquivo(leitor.getSbArquivoTotal());
			atualizaTelaMatriz();
			break;
		case 1://Filiais
			ArquivoSPED arquivoFilial = new ArquivoSPED();
			arquivoFilial.setSbArquivo(leitor.getSbArquivoTotal());
			atualizaTelaFilial(arquivoFilial);
			break;
		case 2://Arquivo Unificado
			JOptionPane.showMessageDialog(this.frmUnificadorEfd, "Arquivo Unificado com Sucesso.");
			lblMsgArquivo.setText("Arquivo Salvo na Pasta: \n"+jFileChooser.getCurrentDirectory().toString() );
			break;
		default:
		}
	}

	/**
	 * Controla a o Metodo Enable dos Botoes
	 * para Guiar o Usuario, nas tarefas, e evitar erros.
	 * @param index
	 */
	public void habilitaBotoes(int index){
		switch (index){
		case 0://Matriz ok, libera o SearchFilial
			btnSearchFileMatriz.setEnabled(false);
			btnSearchArquivosFiliais.setEnabled(true);
			btnOk.setEnabled(false);
			break;
		case 1://SearchFilial ok, libera o ADD e o Unificador
			btnOk.setEnabled(true);
			btnJuntarArquivos.setEnabled(true);
			break;
		}

	}
}
