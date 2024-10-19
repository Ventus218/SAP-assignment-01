package sap.ass01.solution.frontend.user;

import javax.swing.*;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import io.vertx.core.json.jackson.DatabindCodec;
import sap.ass01.solution.frontend.model.*;

public class EBikeUserApp {

    public static void main(String[] args) {
        DatabindCodec.mapper().registerModule(new Jdk8Module());
        HTTPAPIs apis = new HTTPAPIsImpl("localhost", 8080);
        SwingUtilities.invokeLater(() -> new LoginView(apis));
    }
}
