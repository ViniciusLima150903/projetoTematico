package componentesCustom;

import java.awt.Color;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import utilidadesFx.FxRunner;

public class TextFieldCustom<T> extends VBox {

//	private static MLogger							log	= MLogger.getLogger(NLFXTextField.class);

	@FXML
	private Label lblTitle;

	@FXML
	private TextField txtField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private HBox fieldBox;

	@FXML
	private FontIcon iconKeyboard;

	@FXML
	private FontIcon iconClearField;

	@FXML
	private Button btnAction;

	@FXML
	private Button btnClear;

	@FXML
	private FontIcon icon;

	private List<NLFXTextFieldFocusLostEvent> onfocusListeners;

	/**
	 * Properidades
	 */
	private SimpleStringProperty title;
	private SimpleStringProperty content;
	private SimpleStringProperty tooltip;
	private SimpleObjectProperty<Paint> focusedColor;
	private SimpleObjectProperty<Paint> currentBorderColor;
	private SimpleBooleanProperty isAlfanumerico;
	private SimpleIntegerProperty maxLength;
	private SimpleBooleanProperty isPinpad;
	private SimpleBooleanProperty isCampoSenha;
	private SimpleObjectProperty<TipoDadoPinpad> tipoDadoPinpad;
	private SimpleBooleanProperty isNllov;
	private SimpleBooleanProperty isSelectAllOnFocus;
	private SimpleObjectProperty<TipoMascara> tipoMascara;
	private SimpleBooleanProperty isExibeTeclado;
	private SimpleBooleanProperty showClearButton;
	private SimpleBooleanProperty callChangeOnClear;

	private String lastTextOnclik = null;

	private boolean disableClearButton;

	private Runnable afterClear;

	private ContentType contentType;

	public TextField getTxtField() {
		if (isCampoSenha())
			return passwordField;

		return txtField;
	}

	/**
	 *
	 * Enum utilizado para identificar as chamadas do pinpad que serão feitas
	 *
	 */
	public enum TipoDadoPinpad {
		CPF
	}

	public enum TipoMascara {
		DINHEIRO, TELEFONE, CELULAR, CPF, DATA, CEP
	}

	/**
	 * Informa o tipo de caracter que será aceito pelo campo
	 * <ul>
	 * <li>ANY: Qualquer tipo</li>
	 * <li>ALPHA: Apenas letras</li>
	 * <li>INTEGER: Apenas números inteiros</li>
	 * <li>DECIMAL: Números com ponto flutuantes</li>
	 * <li>DATE: Data</li>
	 * <li>REGEX: Por expressão regular</li>
	 * </ul>
	 */
	public enum ContentType {
		ANY, ALPHA, INTEGER, DECIMAL, DATE, REGEX
	};

	public TextFieldCustom() {

		FXMLLoader loader = new FXMLLoader(getFxmlPath(this.getClass()));
		loader.setRoot(this);
		loader.setController(this);

		try {
			FxRunner.load(loader);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static URL getFxmlPath(Class<?> clazz) {
		return clazz.getResource(clazz.getSimpleName() + ".fxml");
	}

	@FXML
	private void initialize() {
		title = new SimpleStringProperty(this, "title");
//		setColor(javafx.scene.paint.Color.web("#87CEFA"));
		content = new SimpleStringProperty(this, "content");
		tooltip = new SimpleStringProperty(this, "tooltip-text");
		isAlfanumerico = new SimpleBooleanProperty(true);
		maxLength = new SimpleIntegerProperty(0);
		isPinpad = new SimpleBooleanProperty(false);
		isNllov = new SimpleBooleanProperty(false);
		isSelectAllOnFocus = new SimpleBooleanProperty(true);
		tipoDadoPinpad = new SimpleObjectProperty<TipoDadoPinpad>(this, "tipoDadoPinpad");
		tipoMascara = new SimpleObjectProperty<TipoMascara>(this, "tipoMascara");
		isCampoSenha = new SimpleBooleanProperty(false);
		isExibeTeclado = new SimpleBooleanProperty(true);
		showClearButton = new SimpleBooleanProperty(true);
		callChangeOnClear = new SimpleBooleanProperty(true);

		bindProperties();
		addListeners();
		if (TipoMascara.DINHEIRO.equals(tipoMascara.get())) {
			showClearButton.set(false);
		}

		btnClear.setDisable(disableClearButton);
	}

	private void bindProperties() {
		lblTitle.textProperty().bind(title);

		txtField.textProperty().bindBidirectional((content));
		txtField.promptTextProperty().bind(tooltip);
		txtField.visibleProperty().bind(isCampoSenha.not());
		txtField.managedProperty().bind(isCampoSenha.not());

		passwordField.textProperty().bindBidirectional((content));
		passwordField.promptTextProperty().bind(tooltip);
		passwordField.visibleProperty().bind(isCampoSenha);
		passwordField.managedProperty().bind(isCampoSenha);

		iconClearField.setIconCode(FontAwesome.TIMES_CIRCLE);
//		icon.iconColorProperty().bind(focusedColor);

		btnClear.visibleProperty().bind(showClearButton);
		btnClear.managedProperty().bind(showClearButton);
		btnAction.visibleProperty().bind(isPinpad);
		btnAction.managedProperty().bind(isPinpad);
	}

	private void selectOnFocus() {
		if (getIsSelectAllOnFocus())
			getTxtField().selectAll();
		else
			getTxtField().selectEnd();
	}

	public static boolean isDecimal(String input) {
		if (input == null)
			return false;
		String rex = "^[-+]?\\d*[.]?\\d+|^[-+]?\\d+[,.]?\\d*";
		return Pattern.matches(rex, input);
	}

	private void addListeners() {
		ChangeListener<Boolean> focusChange = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
					Boolean newPropertyValue) {
				if (newPropertyValue) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
//							lblTitle.textFillProperty().set(javafx.scene.paint.Color.web("##87CEFA"));
//							currentBorderColor.set(javafx.scene.paint.Color.web("##87CEFA"));
							selectOnFocus();
						}
					});
				} else {
//					lblTitle.textFillProperty().set(javafx.scene.paint.Color.web("#888888"));
//					currentBorderColor.set(javafx.scene.paint.Color.web("#888888"));
					lastTextOnclik = null;
				}
			}
		};

		// se não for field alfanumérico, restringe os caracteres que podem ser
		// digitados
		ChangeListener<String> writeListener = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				if (StringUtils.isBlank(""))
					// Verifica se o caractere digitado é valido para teclados numéricos
					if (!isAlfanumerico() && (tipoMascara == null || tipoMascara.get() == null)) {
						if (StringUtils.isBlank(oldValue)) {

							txtField.textProperty().set((newValue));
							setContent(newValue);
						}
					} else if (!StringUtils.isBlank(newValue)) {

						if (maxLength.get() > 0 && newValue != null && newValue.length() > maxLength.get()) {
							txtField.textProperty().set((oldValue));
							setContent(oldValue);
							return;
						}

						if (tipoMascara != null && tipoMascara.get() != null) {
							switch (tipoMascara.get()) {
							case DINHEIRO:

								String valor = tratarMacaraDinheiro(oldValue, newValue);

								// Atualiza valor final no campo
								txtField.textProperty().set((valor));
								setContent(valor);
								break;
							case CEP:

								String cep = "";

								if (newValue.trim().length() == 8) {
									cep = newValue;

									// Se contém 8 dígitos e também o hífen.. provavelmente o CEP estava completo
									// (com a
									// máscara) mas algum dígito foi apagado (por exemplo o último), então o hífen é
									// removido
									if (cep.contains("-")) {
										cep = cep.replace("-", "");
									} else {
										// Se contém 8 dígitos e não tem o hífen.. então insere o hífen.
										String novoCep = "";
										final int posicaoHifen = 4;

										for (int i = 0; i < cep.length(); i++) {
											novoCep += cep.charAt(i);

											// Adiciona o hífen
											if (i == posicaoHifen) {
												novoCep += "-";
											}
										}

										cep = novoCep;
									}
								} else if (newValue.trim().length() > 9) {
									// Se possui mais que 9 dígitos (incluíndo o hífen), então ignora o último
									// dígito.
									cep = newValue;
									cep = cep.substring(0, 9);
								} else {
									cep = newValue;
								}
								// Atualiza valor final no campo
								txtField.textProperty().set(cep);
								setContent(cep);

								break;

							case CPF:

								String cpf = null;

								if (cpf.trim().length() > 11) {
									cpf = cpf.substring(0, 11);
								}
								// Atualiza valor final no campo
								txtField.textProperty().set(cpf);
								setContent(cpf);

								break;
							case TELEFONE:
							case CELULAR:

								String telefone = newValue.replaceAll("[^0-9]", "");

								final boolean telefoneFixo = telefone.trim().length() == 10;
								final boolean celular = telefone.trim().length() == 11;

								if (telefoneFixo || celular) {

									String novoTelefone = "";

									for (int i = 0; i < telefone.length(); i++) {

										if (i == 0) {
											novoTelefone = "(";
										}

										novoTelefone += telefone.charAt(i);

										if (i == 1) {
											novoTelefone += ") ";
										} else if ((telefoneFixo && i == 5) || (celular && i == 6)) {
											novoTelefone += "-";
										}
									}

									telefone = novoTelefone;
								} else if (telefone.trim().length() > 11) {
									telefone = oldValue.replaceAll("[^0-9]", "");
								}

								txtField.textProperty().set(telefone);
								setContent(telefone);

								break;

							case DATA:

								String data = newValue;
								String value = data;
								if (data.trim().length() < 11) {
									value = value.replaceAll("[^0-9]", "");
									value = value.replaceFirst("(\\d{2})(\\d)", "$1/$2");
									value = value.replaceFirst("(\\d{2})\\/(\\d{2})(\\d)", "$1/$2/$3");
								} else if (data.trim().length() >= 11) {
									value = oldValue.replaceAll("[^0-9]", "");
								}
								data = value;
								txtField.textProperty().set(data);
								setContent(data);

								break;

							default:
								setContent(newValue);
								break;
							}
						}
					}
			}

			private String tratarMacaraDinheiro(String oldValue, String newValue) {
				String plainText = newValue.replaceAll("[^0-9]", "");

				if (newValue.trim().startsWith("0,"))
					plainText = plainText.substring(1, plainText.length());

				while (plainText.length() < 3) {
					plainText = "0" + plainText;
				}

				StringBuilder builder = new StringBuilder(plainText);
				builder.insert(plainText.length() - 2, ".");

				Double vlr = Double.parseDouble(builder.toString());

				NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

				// Remove R$
				DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) format).getDecimalFormatSymbols();
				decimalFormatSymbols.setCurrencySymbol("");
				((DecimalFormat) format).setDecimalFormatSymbols(decimalFormatSymbols);

				// Formata valor final
				return format.format(vlr).trim();
			}
		};

		txtField.setOnMouseClicked(e -> {
			if (txtField == null || txtField.getText() == null)
				return;
			if (!(txtField.getText().equals(lastTextOnclik))) {
				lastTextOnclik = txtField.getText();
				txtField.selectAll();
				Platform.runLater(() -> {
					txtField.selectAll();
				});
			}
		});

		txtField.focusedProperty().addListener(focusChange);
		txtField.textProperty().addListener(writeListener);

		passwordField.focusedProperty().addListener(focusChange);
		passwordField.textProperty().addListener(writeListener);
	}

	@FXML
	private void clearField() {
		this.txtField.clear();
		if (getCallChangeOnClear()) {
			KeyEvent newEventPressed = new KeyEvent(KeyEvent.KEY_PRESSED, " ", " ", KeyCode.ENTER, false, false, false,
					false);
			Event.fireEvent(this.txtField, newEventPressed);
		}

		if (afterClear != null)
			afterClear.run();

	}

	public void toggleTitulo() {
		double height = this.fieldBox.getHeight();
		this.lblTitle.setVisible(!this.lblTitle.isVisible());
		this.lblTitle.setManaged(!this.lblTitle.isManaged());
		this.fieldBox.setMaxHeight(height);
	}

	public void toggleTituloAjustandoTamanho() {
		this.lblTitle.setVisible(!this.lblTitle.isVisible());
		this.lblTitle.setManaged(!this.lblTitle.isManaged());
	}

	/* PROPRIEDADES */

	// título

	public SimpleStringProperty getTitleProperty() {
		return title;
	}

	public String getTitle() {
		return title.get();
	}

	public void setTitle(String title) {
		this.title.set(title);
	}

	// conteúdo (digitado no textfield)

	public SimpleStringProperty getContentProperty() {
		return content;
	}

	public String getContent() {
		if (this.content.get() == null) {
			this.content.set("");
		}
		return this.content.get();
	}

	public void setContent(String content) {
		this.content.set(content);
	}

	public String getConteudoSemMascara() {

		if (getContent().isEmpty())
			return "";

		final String conteudoOriginal = getContent();
		String conteudoSemMascara = conteudoOriginal;

		if (tipoMascara != null && tipoMascara.get() != null) {

			switch (tipoMascara.get()) {
			case DINHEIRO:
				conteudoSemMascara = conteudoSemMascara.replaceAll("[^0-9]", "");
				break;
			case CEP:
				conteudoSemMascara = conteudoSemMascara.replaceAll("[^0-9]", "");
				break;
			case TELEFONE:
				conteudoSemMascara = conteudoSemMascara.replaceAll("[^0-9]", "");
				break;

			case DATA:
				conteudoSemMascara = conteudoSemMascara.replaceAll("[^0-9]", "");
				break;

			default:
				conteudoSemMascara = getContent();
				break;
			}

			return conteudoSemMascara;
		} else {
			return conteudoSemMascara;
		}
	}

	// tooltip

	public SimpleStringProperty getTootltipProperty() {
		return this.tooltip;
	}

	public String getTooltip() {
		return this.tooltip.get();
	}

	public void setTooltip(String tooltip) {
		this.tooltip.set(tooltip);
	}

	// cor

	public Paint getColor() {
		return focusedColor.get();
	}

	public void setColor(Paint color) {
		this.focusedColor.set(color);
	}

	public SimpleObjectProperty<Paint> colorProperty() {
		return focusedColor;
	}

	// alfanumerico

	public boolean isAlfanumerico() {
		return isAlfanumerico.get();
	}

	public void setAlfanumerico(boolean bool) {
		isAlfanumerico.set(bool);
	}

	public SimpleBooleanProperty isAlfanumericoProperty() {
		return isAlfanumerico;
	}

	// pinpad

	public boolean isPinpad() {
		return isPinpad.get();
	}

	public void setPinpad(boolean bool) {
		isPinpad.set(bool);
	}

	public SimpleBooleanProperty isPinpadProperty() {
		return isPinpad;
	}

	// NLLOV

	public boolean isNllov() {
		return isNllov.get();
	}

	public void setNllov(boolean bool) {
		isNllov.set(bool);
		// Executa novamente para reatualizar o componente
		bindProperties();
	}

	// tipo de dado do pinpad

	public TipoDadoPinpad getTipoDadoPinpad() {
		return tipoDadoPinpad.get();
	}

	public void setTipoDadoPinpad(TipoDadoPinpad tipo) {
		tipoDadoPinpad.set(tipo);
	}

	public SimpleObjectProperty<TipoDadoPinpad> tipoDadoPinpadProperty() {
		return tipoDadoPinpad;
	}

	// tipo da mascara

	public TipoMascara getTipoMascara() {
		return tipoMascara.get();
	}

	public void setTipoMascara(TipoMascara tipo) {
		tipoMascara.set(tipo);
		if (TipoMascara.DINHEIRO.equals(tipo)) {
			showClearButton.set(false);
		}
	}

	public SimpleObjectProperty<TipoMascara> tipoMascaraProperty() {
		return tipoMascara;
	}

	// tipo do requestFocus no text field
	public boolean getIsSelectAllOnFocus() {
		return isSelectAllOnFocus.get();
	}

	public void setIsSelectAllOnFocus(boolean isSelectAllOnFocus) {
		this.isSelectAllOnFocus.set(isSelectAllOnFocus);
	}

	public SimpleBooleanProperty isIsSelectAllOnFocus() {
		return isSelectAllOnFocus;
	}

	// isCampoSenha

	public boolean isCampoSenha() {
		return isCampoSenha.get();
	}

	public void setCampoSenha(boolean bool) {
		isCampoSenha.set(bool);
	}

	public SimpleBooleanProperty isCampoSenhaProperty() {
		return isCampoSenha;
	}

	// indExibeTeclado

	public boolean isExibeTeclado() {
		return isExibeTeclado.get();
	}

	public void setExibeTeclado(boolean bool) {
		isExibeTeclado.set(bool);
	}

	public SimpleBooleanProperty isExibeTecladoProperty() {
		return isExibeTeclado;
	}

	// onAction (cada componente de field pode ter um onAction diferente)

	public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
		return txtField.onActionProperty();
	}

	public final EventHandler<ActionEvent> getOnAction() {
		return txtField.getOnAction();
	}

	public final void setOnAction(EventHandler<ActionEvent> value) {
		txtField.setOnAction(value);
	}

	public interface NLFXTextFieldFocusLostEvent extends Function<TextFieldCustom, Boolean> {
		@Override
		public Boolean apply(TextFieldCustom no);
	}

	public void addFocusLostListerner(NLFXTextFieldFocusLostEvent listner) {
		onfocusListeners.remove(listner);
		onfocusListeners.add(listner);
	}

	public void removeFocusLostListerner(NLFXTextFieldFocusLostEvent listner) {
		onfocusListeners.remove(listner);
	}

	public interface AfterConsultaLov extends Runnable {

	}

	public void desativaComponenteTodo(boolean disable) {
		getTxtField().setDisable(disable);
	}

	public Integer getMaxLength() {
		return maxLength.get();
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength.set(maxLength);
	}

	public boolean isShowClearButton() {
		return showClearButton.get();
	}

	public void setShowClearButton(boolean show) {
		this.showClearButton.set(show);
	}

	public boolean getCallChangeOnClear() {
		return callChangeOnClear.get();
	}

	public void setCallChangeOnClear(boolean call) {
		this.callChangeOnClear.set(call);
	}

	public void setDisableClearButton(boolean disable) {
		disableClearButton = disable;
		btnClear.setDisable(disableClearButton);
	}

	public void requestFocus() {
		getTxtField().requestFocus();
	}

	/**
	 * Permite executar alguma ação na tela pai ao limpar dados do campo
	 * 
	 * @param afterClear
	 */
	public void setAfterClear(Runnable afterClear) {
		this.afterClear = afterClear;
	}

	public BigDecimal getBigDecimalValue() {
		try {
			return new BigDecimal(getContent().replace(",", "."));
		} catch (Exception e) {
			return null;
		}
	}

	public void setEditable(boolean value) {
		getTxtField().setEditable(value);
	}

	public Integer getIntValue() {
//		if (StringUtils.isEmpty(getContent()))
//			return null;

		try {
			return Integer.valueOf(getContent());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public void selectAll() {
		getTxtField().selectAll();
	}
}
