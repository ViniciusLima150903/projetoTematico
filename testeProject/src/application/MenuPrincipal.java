package application;

import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utilidadesFx.FxLoaderBean;
import utilidadesFx.FxRunner;

public class MenuPrincipal extends Application {

	private Stage stage;

	@FXML
	private Button btnClose;

	@FXML
	private void actLogin() {

		new TelaFxLogin(stage);
	}

	@FXML
	private void actCadItem() {
		TelaFxPopupsInfo.exibirPopupMensagem(stage, 1, "Tela ainda não convertida");

	}

	@FXML
	private void actRelatorio() {
		TelaFxPopupsInfo.exibirPopupMensagem(stage, 1, "Tela ainda não convertida");

	}

	@FXML
	private void actRegistroLucro() {
		TelaFxPopupsInfo.exibirPopupMensagem(stage, 1, "Tela ainda não convertida");

	}

	@FXML
	private void actCadCliente() {
		TelaFxPopupsInfo.exibirPopupMensagem(stage, 1, "Tela ainda não convertida");

	}

	@FXML
	private void actCadFuncionario() {
		TelaFxPopupsInfo.exibirPopupMensagem(stage, 1, "Tela ainda não convertida");

	}

	@FXML
	private void actManuseioInvetario() {
		TelaFxPopupsInfo.exibirPopupMensagem(stage, 1, "Tela ainda não convertida");

	}

	@FXML
	private void actAlterarInvetario() {
		TelaFxPopupsInfo.exibirPopupMensagem(stage, 1, "Tela ainda não convertida");
	}

	@FXML
	private void close() {
		Platform.setImplicitExit(false);
		FxRunner.runLater(() -> {
			stage.close();
			System.exit(0);
		});
	}

	@FXML
	private void buscaProduto() {

	}

	public static void runLater(Runnable run) {
		if (!Platform.isFxApplicationThread())
			Platform.runLater(run);
		else
			run.run();
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void gerenciarEventosTeclado(KeyEvent event) {

		if (event == null)
			return;
		// ignora o TAB
		if (event.getCode().equals(KeyCode.TAB))
			return;

		if (event.getCode().equals(KeyCode.ESCAPE)) {
			close();
			return;
		}
		if (event.getCode().equals(KeyCode.F8)) {
			buscaProduto();
			return;
		}
	}

	protected void addEvents() throws Exception {
	}

	protected void defaultValues() {
		FontIcon iconClose = new FontIcon(FontAwesome.TIMES);
		iconClose.setIconSize(14);
		btnClose.setGraphic(iconClose);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = null;
		try {
			FxLoaderBean bean = FxRunner.loadFromXml(MenuPrincipal.this.getClass(), MenuPrincipal.this);
			root = bean.getRoot();
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n" + e.getCause());
			return;
		}

		double width = 1920.0;
		double height = 1080.0;

		Scene scene = root.getScene();

		if (scene == null)
			scene = new Scene(root, width, height);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage = new Stage();

		scene.setFill(Color.TRANSPARENT);
		scene.addEventHandler(KeyEvent.KEY_RELEASED, MenuPrincipal.this::gerenciarEventosTeclado);
		stage.setScene(scene);

		stage.initModality(Modality.WINDOW_MODAL);
		stage.initStyle(StageStyle.TRANSPARENT);
		addEvents();
		defaultValues();

		stage.setTitle("consulta de produto");
		this.stage = stage;
		stage.show();
	}

}
