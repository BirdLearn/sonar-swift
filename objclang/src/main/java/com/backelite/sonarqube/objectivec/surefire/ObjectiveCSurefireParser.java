/**
 * Swift SonarQube Plugin - Objective-C module - Enables analysis of Swift and Objective-C projects into SonarQube.
 * Copyright © 2015 Backelite (${email})
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.backelite.sonarqube.objectivec.surefire;

import com.backelite.sonarqube.commons.surefire.BaseSurefireParser;
import com.google.common.collect.ImmutableList;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by gillesgrousset on 06/01/15.
 */
public class ObjectiveCSurefireParser extends BaseSurefireParser {

    public ObjectiveCSurefireParser(FileSystem fileSystem, ResourcePerspectives resourcePerspectives, SensorContext context) {
        super(fileSystem, resourcePerspectives, context);
    }

    @Nullable
    public Resource getUnitTestResource(String classname) {

        String fileName = classname.replace('.', '/') + ".m";

        InputFile inputFile = fileSystem.inputFile(fileSystem.predicates().hasPath(fileName));

        /*
         * Most xcodebuild JUnit parsers don't include the path to the class in the class field, so search for it if it
         * wasn't found in the root.
         */
        if (inputFile == null) {
            List<InputFile> files = ImmutableList.copyOf(fileSystem.inputFiles(fileSystem.predicates().and(
                    fileSystem.predicates().hasType(InputFile.Type.TEST),
                    fileSystem.predicates().matchesPathPattern("**/" + fileName.replace("_", "+")))));

            if (files.isEmpty()) {
                LOGGER.info("Unable to locate test source file {}", fileName);
            } else {
                /*
                 * Lazily get the first file, since we wouldn't be able to determine the correct one from just the
                 * test class name in the event that there are multiple matches.
                 */
                inputFile = files.get(0);
            }
        }

        return inputFile == null ? null : context.getResource(inputFile);
    }

}
