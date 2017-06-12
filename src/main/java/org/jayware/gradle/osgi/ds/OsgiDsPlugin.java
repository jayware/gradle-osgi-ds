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
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.api.tasks.compile.AbstractCompile;

import java.io.File;


public class OsgiDsPlugin
implements Plugin<Project>
{
    public static final String GENERATE_DESCRIPTORS_TASK_NAME = "generateDeclarativeServicesDescriptors";

    @Override
    public void apply(Project project)
    {
        final GenerateDeclarativeServicesDescriptorsTask task = project.getTasks().create(GENERATE_DESCRIPTORS_TASK_NAME, GenerateDeclarativeServicesDescriptorsTask.class);
        final AbstractArchiveTask archiveTask = (AbstractArchiveTask) project.getTasks().findByName("jar");

        task.setGroup(BasePlugin.BUILD_GROUP);
        task.setDescription("Generates OSGi Declarative Services XML descriptors");
        task.setOutputDirectory(new File(project.getBuildDir(), "/tmp/osgi-ds"));

        project.getTasks().withType(AbstractCompile.class).forEach(compileTask -> 
        {
        	if(!compileTask.getName().toLowerCase().contains("test")){ // exclude testCompile
	            task.getInput().add(project.files(compileTask));
	            task.mustRunAfter(compileTask);
        	}
        });

        if (archiveTask != null)
        {
            archiveTask.from(task);
        }
    }
}
