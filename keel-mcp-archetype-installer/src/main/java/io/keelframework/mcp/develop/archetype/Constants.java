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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Constants {

    private Constants() {
    }

    /**
     * Validates project names.
     *
     * <p>The name:</p>
     * <ul>
     *     <li>must start with a lowercase letter,</li>
     *     <li>may contain lowercase letters, numbers and hyphens,</li>
     *     <li>cannot end with a hyphen.</li>
     * </ul>
     */

    public static final String PROJECT_REGEX =
            "^[a-z][0-9a-z\\-]*(?<!\\-)$";

    public static final String OSNAME = "os.name";
    public static final String WINDOWS = "Windows";

    private static final String[] WIN_RESERVED_WORDS = {
            "CON", "PRN", "AUX", "NUL",
            "COM1", "COM2", "COM3", "COM4", "COM5",
            "COM6", "COM7", "COM8", "COM9",
            "LPT1", "LPT2", "LPT3", "LPT4", "LPT5",
            "LPT6", "LPT7", "LPT8", "LPT9"
    };

    public static final List<String> WINDOWS_RESERVED_WORDS_LIST =
            Collections.unmodifiableList(Arrays.asList(WIN_RESERVED_WORDS));

    /**
     * Maven command used to generate a Keel MCP Server project.
     *
     * <p>The archetype is resolved at runtime from the configured Maven
     * repository using its Maven coordinates. No archetype JAR or POM is
     * embedded in the installer.</p>
     *
     * <p>Placeholders ({@code %s}), in order:</p>
     * <ol>
     *     <li>archetypeVersion</li>
     *     <li>microName</li>
     *     <li>micro</li>
     *     <li>domainName</li>
     *     <li>domain</li>
     *     <li>version</li>
     * </ol>
     *
     * <p>The archetype derives {@code artifactId}, {@code groupId} and
     * {@code package} from the supplied properties.</p>
     */
    static final String UNFORMATTED_MVN_COMMAND =
            "mvn archetype:generate"
                    + " -DarchetypeGroupId=io.keelframework.mcp"
                    + " -DarchetypeArtifactId=keel-mcp-archetype"
                    + " -DgroupId=%s"
                    + " -DarchetypeVersion=%s"
                    + " -DmicroName=%s"
                    + " -Dmicro=%s"
                    + " -DdomainName=%s"
                    + " -Ddomain=%s"
                    + " -Dversion=%s"
                    + " -DinteractiveMode=false"
                    + " -Dtransport=mvc";

    public static final String NOARGUMENTS_ERROR_MESSAGE =
            "The Keel MCP installer requires three arguments: "
                    + "<microName>, <version> and <domainName>. Example:\n\t"
                    + "mcp-poc-insurance 1.0.0 insurance";

    public static final String ONE_ARGUMENT_ERROR_MESSAGE =
            "Missing arguments <version> and <domainName>. "
                    + "Please provide them to proceed.";

    public static final String TWO_ARGUMENTS_ERROR_MESSAGE =
            "Missing argument <domainName>. "
                    + "Please provide it to proceed.";

    public static final String INVALID_MICRONAME_MESSAGE =
            "The microservice name may only contain lowercase letters, "
                    + "numbers and hyphens, must start with a lowercase letter "
                    + "and cannot end with a hyphen "
                    + "(regex: " + PROJECT_REGEX + ")";

    public static final String RESERVED_WORDS_ERROR_MESSAGE =
            "The microservice name cannot be a reserved Windows OS name.";


    public static final String THREE_ARGUMENTS_ERROR_MESSAGE =
            "Missing argument <groupId>. Please provide it to proceed.";

    public static final String GROUP_ID_REGEX = "^[a-zA-Z][a-zA-Z0-9.]*$";

    public static final String INVALID_GROUPID_MESSAGE =
            "The groupId may only contain letters, numbers and dots, "
                    + "and it must start with a letter (regex: " + GROUP_ID_REGEX + ")";
}