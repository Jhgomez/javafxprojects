module fxlauncher.lib {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires javafx.web;
    uses fxlauncher.UIProvider;
    requires java.xml.bind;

    requires org.glassfish.jaxb.runtime;

    opens fxlauncher to java.xml.bind;

    exports fxlauncher;
}