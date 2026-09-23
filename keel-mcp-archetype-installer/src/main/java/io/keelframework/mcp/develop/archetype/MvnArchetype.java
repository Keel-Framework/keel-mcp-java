/*
 * Copyright 2026 Keel Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    private static final int ARG_GROUP_ID = 3;


    public static void main(String[] args) throws InterruptedException, IOException {

        String sistemaOperativo = System.getProperty(Constants.OSNAME);

        // Version del arquetipo a generar. Se resuelve en runtime contra
        // Nexus por coordenadas Maven — no se embebe ningun jar/pom.

        try {
            validateArguments(args);

            String microName = args[ARG_MICRO_NAME];
            String version = args[ARG_VERSION];
            String domainName = args[ARG_DOMAIN_NAME];
            String groupId = args[ARG_GROUP_ID];
            String archetypeVersion = readArchetypeVersion();


            String micro = sanitizeForPackage(microName);
            String domain = sanitizeForPackage(domainName);

            String command = String.format(
                    Constants.UNFORMATTED_MVN_COMMAND,
                    groupId,
                    archetypeVersion,
                    microName,
                    micro,
                    domainName,
                    domain,
                    version
            );
            System.out.println("DEBUG: " + command);
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
            System.out.println("Failed to generate the project from the archetype.");
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
                throw new IOException("archetype.properties not found on the classpath.");
            }

            properties.load(in);
        }

        String version = properties.getProperty("archetype.version");

        if (version == null || version.trim().isEmpty()) {
            throw new IOException("The archetype.version property is not defined in archetype.properties.");
        }

        return version;
    }

    /**
     * Converts a valid project name (hyphens and uppercase letters allowed,
     * PROJECT_REGEX) into a valid code for Java packages and the archetype's
     * micro/domain properties: lowercase and without hyphens. If the result
     * starts with a digit, it is prefixed to avoid an invalid Java identifier.
     */
    private static String sanitizeForPackage(String value) {

        String sanitized = value.replace("-", "").toLowerCase();

        if (sanitized.isEmpty()) {
            throw new IllegalArgumentException("The value is empty after removing hyphens.");
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
        if (args.length == 3 || args[ARG_GROUP_ID] == null || args[ARG_GROUP_ID].trim().isEmpty()) {
            throw new IllegalArgumentException(Constants.THREE_ARGUMENTS_ERROR_MESSAGE);
        }
        validateFormatArguments(args);

    }

    private static void validateFormatArguments(String[] args) {

        if (!args[ARG_MICRO_NAME].matches(Constants.PROJECT_REGEX)) {
            throw new IllegalArgumentException(Constants.INVALID_MICRONAME_MESSAGE);
        }
        if (!args[ARG_GROUP_ID].matches(Constants.GROUP_ID_REGEX)) {
            throw new IllegalArgumentException(Constants.INVALID_GROUPID_MESSAGE);
        }

        if (WINDOWS_RESERVED_WORDS_LIST.contains(args[ARG_MICRO_NAME].toUpperCase())) {
            throw new IllegalArgumentException(Constants.RESERVED_WORDS_ERROR_MESSAGE);
        }

    }

}
