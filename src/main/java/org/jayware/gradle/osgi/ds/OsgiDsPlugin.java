/**
 * Copyright (C) 2016 Elmar Schug <elmar.schug@jayware.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jayware.gradle.osgi.ds;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;

public class OsgiDsPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().configure(SourceSetContainer.class, sourceSets -> sourceSets.configureEach(sourceSet -> {
            String taskName = sourceSet.getTaskName("generate", "DeclarativeServicesDescriptors");
            TaskProvider<GenerateDeclarativeServicesDescriptorsTask> generateTask = project.getTasks().register(taskName, GenerateDeclarativeServicesDescriptorsTask.class, task -> {
                task.setDescription("Generate OSGi Declarative Services XML descriptors for " + sourceSet.getName() + " classes");
                task.getInputFiles().from(sourceSet.getOutput().getClassesDirs());
                task.getClasspath().from(sourceSet.getRuntimeClasspath());
                task.getOutputDirectory().set(project.getLayout().getBuildDirectory().dir("generated/resources/osgi-ds/" + sourceSet.getName()));
            });
            sourceSet.getOutput().getGeneratedSourcesDirs().plus(project.fileTree(generateTask));
        }));
    }
}
