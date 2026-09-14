package io.keelframework.mcp.develop.archetype;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import static io.keelframework.mcp.develop.archetype.Constants.WINDOWS_RESERVED_WORDS_LIST;

public class MvnArchetype {

    // Uso esperado: <nombreMicro> <versionMicro> <nombreProyectoOpenShift>
    private static final int ARG_MICRO_NAME = 0;
    private static final int ARG_VERSION = 1;
    private static final int ARG_DOMAIN_NAME = 2;

    public static void main(String[] args) throws InterruptedException, IOException {

        String sistemaOperativo = System.getProperty(Constants.OSNAME);

        // Version del arquetipo a generar. Se resuelve en runtime contra
        // Nexus por coordenadas Maven — no se embebe ningun jar/pom.

        try {
            validateArguments(args);

            String microName = args[ARG_MICRO_NAME];
            String version = args[ARG_VERSION];
            String domainName = args[ARG_DOMAIN_NAME];
            String archetypeVersion = readArchetypeVersion();


            String micro = sanitizeForPackage(microName);
            String domain = sanitizeForPackage(domainName);

            String groupId =
                    "io.keelframework.mcp." + domain + "." + micro;

            String command = String.format(
                    Constants.UNFORMATTED_MVN_COMMAND,
                    archetypeVersion,
                    microName,
                    micro,
                    domainName,
                    domain,
                    groupId,
                    version
            );

            int exitCode = runCommand(
                    sistemaOperativo,
                    command,
                    System.getProperty("user.dir")
            );
            if (exitCode != 0) {
                throw new IllegalStateException("Archetype process not successful");
            }
            System.exit(exitCode);

        } catch (IllegalArgumentException e) {
            System.out.println(" ! " + e.getMessage());
        } catch (IOException e) {
            System.out.println("No se ha podido generar el proyecto desde el arquetipo");
        }

    }

    private static int runCommand(String sistemaOperativo, String command, String workingDir)
            throws IOException, InterruptedException {

        ProcessBuilder builder = new ProcessBuilder();

        if (sistemaOperativo.startsWith(Constants.WINDOWS)) {
            builder.command("cmd.exe", "/c", command);
        } else {
            builder.command("sh", "-c", command);
        }

        builder.directory(new File(workingDir));
        Process process = builder.start();
        StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);

        return process.waitFor();
    }

    private static String readArchetypeVersion() throws IOException {

        Properties properties = new Properties();

        try (InputStream in = MvnArchetype.class.getClassLoader().getResourceAsStream("archetype.properties")) {

            if (in == null) {
                throw new IOException("No se ha encontrado archetype.properties en el classpath");
            }

            properties.load(in);
        }

        String version = properties.getProperty("archetype.version");

        if (version == null || version.trim().isEmpty()) {
            throw new IOException("La propiedad archetype.version no está definida en archetype.properties");
        }

        return version;
    }

    /**
     * Convierte un nombre válido de proyecto (permite guiones y mayúsculas,
     * PROJECT_REGEX) en un código válido para package Java y para las
     * properties micro/domain del arquetipo: minúsculas, sin guiones. Si el
     * resultado empezara por dígito, lo prefija para evitar un identificador
     * Java inválido.
     */
    private static String sanitizeForPackage(String value) {

        String sanitized = value.replace("-", "").toLowerCase();

        if (sanitized.isEmpty()) {
            throw new IllegalArgumentException("El valor '" + value + "' queda vacío al eliminar los guiones");
        }

        if (Character.isDigit(sanitized.charAt(0))) {
            sanitized = "p" + sanitized;
        }

        return sanitized;
    }

    private static void validateArguments(String[] args) {

        if (args == null || args.length == 0 || args[ARG_MICRO_NAME] == null || args[ARG_MICRO_NAME].trim().isEmpty()) {
            throw new IllegalArgumentException(Constants.NOARGUMENTS_ERROR_MESSAGE);
        }

        if (args.length == 1 || args[ARG_VERSION] == null || args[ARG_VERSION].trim().isEmpty()) {
            throw new IllegalArgumentException(Constants.ONE_ARGUMENT_ERROR_MESSAGE);
        }

        if (args.length == 2 || args[ARG_DOMAIN_NAME] == null || args[ARG_DOMAIN_NAME].trim().isEmpty()) {
            throw new IllegalArgumentException(Constants.TWO_ARGUMENTS_ERROR_MESSAGE);
        }
        validateFormatArguments(args);

    }

    private static void validateFormatArguments(String[] args) {

        if (!args[ARG_MICRO_NAME].matches(Constants.PROJECT_REGEX)) {
            throw new IllegalArgumentException(Constants.INVALID_MICRONAME_MESSAGE);
        }

        if (WINDOWS_RESERVED_WORDS_LIST.contains(args[ARG_MICRO_NAME].toUpperCase())) {
            throw new IllegalArgumentException(Constants.RESERVED_WORDS_ERROR_MESSAGE);
        }

    }

}
