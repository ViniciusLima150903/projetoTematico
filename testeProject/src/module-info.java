module testeProject {
	requires javafx.controls;
	requires javafx.fxml;
	requires org.kordamp.ikonli.javafx;
	requires org.kordamp.ikonli.fontawesome;
	requires org.kordamp.ikonli.core;
	requires org.eclipse.fx.ide.css.jfx8;
	requires java.desktop;
	requires javafx.web;
	requires org.apache.commons.lang3;
	requires javafx.graphics;

	exports componentesCustom;

	opens componentesCustom to javafx.fxml;
	opens application to javafx.graphics, javafx.fxml;
}
