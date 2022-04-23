module com.example.brains_cloud_client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires io.netty.transport;
    requires io.netty.codec;

    opens com.example.brains_cloud_client to javafx.fxml;
    exports com.example.brains_cloud_client;
}