package application;

import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import componentesCustom.TextFieldCustom;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utilidadesFx.FxLoaderBean;
import utilidadesFx.FxRunner;

public class TelaFxLogin {

	@FXML
	private Button botaoClose;

	@FXML
	private TextFieldCustom<Void> fieldUsuario;

	@FXML
	private TextFieldCustom<Void> fieldSenha;

	private Stage stage;

	@FXML
	private void close() {
		Platform.setImplicitExit(false);
		FxRunner.runLater(() -> {
			stage.close();
		});
	}

	@FXML
	private void limpar() {
	}

	@FXML
	private void recLogin() {
	}

	@FXML
	private void login() {
	}

	public TelaFxLogin(Stage parentScreen) {
		Platform.setImplicitExit(false);
		FxRunner.runAndWait(() -> {
			try {
				Parent root = null;
				try {
					FxLoaderBean bean = FxRunner.loadFromXml(TelaFxLogin.this.getClass(), TelaFxLogin.this);
					root = bean.getRoot();
				} catch (Exception e) {
					System.out.println(e.getMessage() + "\n" + e.getCause().toString());
					return;
				}

				double width = 1920.0;
				double height = 1080.0;

				Scene scene = root.getScene();

				if (scene == null)
					scene = new Scene(root, width, height);

				stage = new Stage();

				scene.setFill(Color.TRANSPARENT);
				scene.addEventHandler(KeyEvent.KEY_RELEASED, this::gerenciarEventosTeclado);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				stage.setScene(scene);

				stage.initModality(Modality.WINDOW_MODAL);
				stage.initStyle(StageStyle.TRANSPARENT);

				stage.initOwner(parentScreen);
				this.defaultValues();

				stage.setTitle("Tela de login");
				stage.showAndWait();
			} catch (Exception e) {
				System.out.println(e.getMessage() + "\n" + e.getCause());
				return;
			}
		});
	}

	private void defaultValues() {

		FontIcon iconClose = new FontIcon(FontAwesome.TIMES);
		iconClose.setIconSize(14);
		botaoClose.setGraphic(iconClose);
		fieldUsuario.requestFocus();
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
	}
}
