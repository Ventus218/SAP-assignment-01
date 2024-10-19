package sap.ass01.solution.frontend.admin;

import javax.swing.*;

import io.vertx.core.json.jackson.DatabindCodec;
import sap.ass01.solution.frontend.model.HTTPAPIs;
import sap.ass01.solution.frontend.model.HTTPAPIsImpl;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class EBikeAdminApp {

    public static void main(String[] args) {
        DatabindCodec.mapper().registerModule(new Jdk8Module());
        HTTPAPIs apis = new HTTPAPIsImpl("localhost", 8080);
        SwingUtilities.invokeLater(() -> new AdminControlPanelView(new AdminControlPanelViewModel(apis)));
    }
}
