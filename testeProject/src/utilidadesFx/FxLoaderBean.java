package utilidadesFx;

import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class FxLoaderBean {
	private FXMLLoader	loader;

	private Parent		root;

	private Object		controller;

	private URL			urlXml;

	public FxLoaderBean() {
		super();
	}

	public FxLoaderBean(FXMLLoader loader, Parent root, Object controler, URL urlXml) {
		super();
		this.loader = loader;
		this.root = root;
		this.controller = controler;
		this.urlXml = urlXml;
	}

	public FXMLLoader getLoader() {
		return loader;
	}

	public void setLoader(FXMLLoader loader) {
		this.loader = loader;
	}

	public Parent getRoot() {
		return root;
	}

	public void setRoot(Parent root) {
		this.root = root;
	}

	public Object getController() {
		return controller;
	}

	public void setController(Object controler) {
		this.controller = controler;
	}

	public URL getUrlXml() {
		return urlXml;
	}

	public void setUrlXml(URL urlXml) {
		this.urlXml = urlXml;
	}
}
