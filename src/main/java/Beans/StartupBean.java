package Beans;

import DAO.Conexion;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.sql.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class StartupBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupBean.class);

    @PostConstruct
    public void init() {
        LOGGER.info("Inicializando StartupBean: Verificando conexión a la base de datos...");

        // Solo verifica sin cerrar la conexión
        try (Connection conn = Conexion.getConexion()) {
            if (conn != null && !conn.isClosed()) {
                LOGGER.info("Conexión a la base de datos verificada correctamente al inicio.");
            } else {
                LOGGER.error("No se pudo verificar la conexión a la base de datos en el inicio.");
            }
        } catch (Exception e) {
            LOGGER.error("Error al verificar conexión en el inicio", e);
        }
    }
}
