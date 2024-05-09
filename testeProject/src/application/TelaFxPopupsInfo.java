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

public class TelaFxPopupsInfo {

	@FXML
	private Label labelInfo;

	@FXML
	private FontIcon iconTela;

	@FXML
	private Button botaoClose;

	@FXML
	private VBox painelTelaPopups;

	@FXML
	private TextFieldCustom<Void> fieldTeste;

	private Stage stage;

	@FXML
	private void close() {
		Platform.setImplicitExit(false);
		FxRunner.runLater(() -> {
			stage.close();
		});
	}

	public static void exibirPopupMensagem(Stage parentScreen, int valueIcon, String messageTela) {
		TelaFxPopupsInfo tela = new TelaFxPopupsInfo();
		tela.showTelaPopups(parentScreen, valueIcon, messageTela);
	}

	private void showTelaPopups(Stage parentScreen, int valueIcon, String messageTela) {
		Platform.setImplicitExit(false);
		FxRunner.runAndWait(() -> {
			try {
				Parent root = null;
				try {
					FxLoaderBean bean = FxRunner.loadFromXml(TelaFxPopupsInfo.this.getClass(), TelaFxPopupsInfo.this);
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
				this.defaultValues(valueIcon, messageTela);

				stage.setTitle("Tela de popups");
				stage.showAndWait();
			} catch (Exception e) {
				System.out.println(e.getMessage() + "\n" + e.getCause());
				return;
			}
		});
	}

	private void defaultValues(int valueIcon, String messageTela) {
		if (valueIcon == 1) {
			iconTela.setIconCode(FontAwesome.INFO);
			iconTela.setIconColor(Paint.valueOf("#87CEFA"));
		} else {
			iconTela.setIconColor(Paint.valueOf("#FF0000"));
			iconTela.setIconCode(FontAwesome.WARNING);
		}

		iconTela.setIconSize(20);

		FontIcon iconClose = new FontIcon(FontAwesome.TIMES);
		iconClose.setIconSize(14);
		botaoClose.setGraphic(iconClose);

		labelInfo.setText(messageTela);

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
