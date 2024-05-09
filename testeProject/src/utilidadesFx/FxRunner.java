package utilidadesFx;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.print.DocFlavor.URL;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class FxRunner {
	private static StringBuilder custonStyle = null;

	private FxRunner() {
		super();
	}

	/**
	 * Executa o {@link Runnable} na thread de aplicação do JavaFX e aguarda
	 * finalização.
	 *
	 * @param action o {@link Runnable} a ser executado
	 * @throws NullPointerException caso {@code action} seja {@code null}
	 */
	public static void runAndWait(final Runnable action) {
		if (action == null)
			throw new NullPointerException("action");

		// executa de forma síncronza na thread do JavaFX
		if (Platform.isFxApplicationThread()) {
			action.run();
			return;
		}

		// enfileira na thread do JavaFX e espera ser finalizado
		final CountDownLatch doneLatch = new CountDownLatch(1);
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				try {
					action.run();
				} finally {
					doneLatch.countDown();
				}
			}
		});

		try {
			doneLatch.await();
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Roda eventos na thread do JavaFX
	 *
	 * @param run ação para executar
	 *
	 */
	public static void runLater(Runnable run) {
		if (!Platform.isFxApplicationThread())
			Platform.runLater(run);
		else
			run.run();
	}

	/**
	 * Seleciona todo o texto de uma WebView e retorna
	 */
	public static String getWebViewText(WebView view) {
		if (view.getEngine().getDocument() == null) {
			return "";
		}

		// script em js para selecionar o texto da WebView
		StringBuilder selectText = new StringBuilder();
		selectText.append("(selectAll = function() { ").append("\n");
		selectText.append("  var range = document.createRange();").append("\n");
		selectText.append("  range.setStart(document.body, 0);").append("\n");
		selectText.append("  range.setEnd(document.body, 1);").append("\n");
		selectText.append("  window.getSelection().addRange(range);").append("\n");
		selectText.append("  return window.getSelection();").append("\n");
		selectText.append("}) ()");

		String text = view.getEngine().executeScript(selectText.toString()).toString();
		view.getEngine().executeScript("window.getSelection().empty()");
		return text;
	}

	/**
	 * Retorna o HTML que está sendo exibido de uma WebView
	 *
	 * @param webview
	 * @return código html da webView
	 */
	public static String getWebViewHTML(WebView webview) {
		if (webview.getEngine().getDocument() == null) {
			return "";
		}

		return (String) webview.getEngine().executeScript("document.documentElement.outerHTML");
	}

	/**
	 * Faz a ação do botão de scroll de uma tabela para cima
	 *
	 * @param table
	 */
	public static void subirTabela(TableView<?> table) {
		if (table.getItems() == null || table.getItems().isEmpty()) {
			return;
		}

		TableViewSkin<?> ts = (TableViewSkin<?>) table.getSkin();
		VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(1);
		int idx = vf.getFirstVisibleCell().getIndex(); // index do primeiro item visível

		// se o index for maior que zero, não é o primeiro item da lista
		if (idx > 0) {
			idx--;
			table.scrollTo(idx);
		}
	}

	/**
	 * Faz a ação do botão de scroll de uma tabela para baixo
	 *
	 * @param table
	 */
	public static void descerTabela(TableView<?> table) {
		if (table.getItems() == null || table.getItems().isEmpty()) {
			return;
		}

		TableViewSkin<?> ts = (TableViewSkin<?>) table.getSkin();
		VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(1);
		int idxUltimoItem = vf.getLastVisibleCell().getIndex(); // pega o index do último item visível

		// se o index for menor que o tamanho da lista, não é o último item da lista
		if (idxUltimoItem < table.getItems().size()) {
			int idx = vf.getFirstVisibleCell().getIndex() + 1;
			table.scrollTo(idx);
		}
	}

	/**
	 * Faz a ação do botão de scroll de uma lista para cima
	 *
	 * @param list
	 */
	public static void subirLista(ListView<?> list) {
		if (list.getItems() == null || list.getItems().isEmpty()) {
			return;
		}

		ListViewSkin<?> ts = (ListViewSkin<?>) list.getSkin();
		VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
		int idx = vf.getFirstVisibleCell().getIndex(); // index do primeiro item visível

		// se o index for maior que zero, não é o primeiro item da lista
		if (idx > 0) {
			idx--;
			list.scrollTo(idx);
		}
	}

	/**
	 * Faz a ação do botão de scroll de uma lista para baixo
	 *
	 * @param list
	 */
	public static void descerLista(ListView<?> list) {
		if (list.getItems() == null || list.getItems().isEmpty()) {
			return;
		}

		ListViewSkin<?> ts = (ListViewSkin<?>) list.getSkin();
		VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
		int idxUltimoItem = vf.getLastVisibleCell().getIndex(); // pega o index do último item visível

		// se o index for menor que o tamanho da lista, não é o último item da lista
		if (idxUltimoItem < list.getItems().size()) {
			int idx = vf.getFirstVisibleCell().getIndex() + 1;
			list.scrollTo(idx);
		}
	}

	public static FxLoaderBean loadFromXml(Object contoller) throws IOException {
		return loadFromXml(contoller.getClass(), contoller);
	}

	public static <T> FxLoaderBean loadFromXml(Class<T> klass, Object contoller) throws IOException {
		return loadFromXml(klass, contoller, true);
	}

	public static <T> FxLoaderBean loadFromXml(Class<T> klass, Object contoller, boolean cores) throws IOException {

		java.net.URL desXml = klass.getResource(klass.getSimpleName() + ".fxml");

		FXMLLoader loader = new FXMLLoader(desXml);
		if (contoller != null)
			loader.setController(contoller);
		Parent root;
		if (cores)
			root = load(loader);
		else
			root = (Parent) loader.load();

		return new FxLoaderBean(loader, root, loader.getController(), desXml);
	}

	/**
	 * Fax carrega a tela e adiciona as variáveis CSS.
	 * 
	 * @param loader
	 * @return
	 * @throws IOException
	 */
	public static Parent load(FXMLLoader loader) throws IOException {

		Parent root = (Parent) loader.load();
		return root;
	}

	/**
	 * -app-color: #1F8BEF; -app-color-0a: #1F8BEF0a; -app-color-14: #1F8BEF14;
	 * -app-color-1e: #1F8BEF1e; -app-color-28: #1F8BEF28; -app-color-32: #1F8BEF32;
	 * -app-color-3c: #1F8BEF3c; -app-color-46: #1F8BEF46; -app-color-50: #1F8BEF50;
	 * -app-color-5a: #1F8BEF5a; -app-color-64: #1F8BEF64; -app-color-6e: #1F8BEF6e;
	 * -app-color-78: #1F8BEF78; -app-color-82: #1F8BEF82; -app-color-8c: #1F8BEF8c;
	 * -app-color-96: #1F8BEF96; -app-color-a0: #1F8BEFa0; -app-color-aa: #1F8BEFaa;
	 * -app-color-b4: #1F8BEFb4; -app-color-be: #1F8BEFbe; -app-color-c8: #1F8BEFc8;
	 * -app-color-d2: #1F8BEFd2; -app-color-dc: #1F8BEFdc; -app-color-e6: #1F8BEFe6;
	 * -app-color-f0: #1F8BEFf0; -app-color-fa: #1F8BEFfa;
	 * 
	 * 
	 * @return
	 */

	public static void showStage(Stage stage, boolean armazenaWindow) {
		if (stage == null)
			return;

		stage.show();
	}

	public static void showStageAndWait(Stage stage, boolean armazenaWindow) {
		if (stage == null)
			return;

		stage.showAndWait();
	}

}
