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

import org.apache.felix.scrplugin.Options;
import org.apache.felix.scrplugin.SCRDescriptorException;
import org.apache.felix.scrplugin.SCRDescriptorFailureException;
import org.apache.felix.scrplugin.SCRDescriptorGenerator;
import org.apache.felix.scrplugin.Source;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.gradle.api.plugins.JavaPlugin.COMPILE_CONFIGURATION_NAME;


public class GenerateDeclarativeServicesDescriptorsTask
extends DefaultTask
{
    private final Project project;
    private final Logger logger;

    @OutputDirectory
    File outputDirectory;

    @InputFiles
    FileCollection input;

    public GenerateDeclarativeServicesDescriptorsTask()
    {
        project = getProject();
        logger = project.getLogger();

        outputDirectory = new File(project.getBuildDir(), "/tmp/osgi-ds");
        getOutputs().dir(outputDirectory);
    }

    @TaskAction
    public void generateDeclarativeServicesDescriptors()
    throws SCRDescriptorException, SCRDescriptorFailureException, MalformedURLException
    {
        final Options scrOptions = createOptions();
        final org.apache.felix.scrplugin.Project scrProject = createProject();

        final SCRDescriptorGenerator scrGenerator = new SCRDescriptorGenerator(new GradleSCRDescriptorGeneratorLoggerAdapter(logger));
        scrGenerator.setOptions(scrOptions);
        scrGenerator.setProject(scrProject);

        scrGenerator.execute();
    }

    private org.apache.felix.scrplugin.Project createProject()
    throws MalformedURLException
    {
        final org.apache.felix.scrplugin.Project scrProject = new org.apache.felix.scrplugin.Project();
        final List<URL> dependenciesAsUrl = new ArrayList<>();
        final List<File> dependenciesAsFile = new ArrayList<>();
        final List<Source> sources = new ArrayList<>();

        project.getConfigurations().getByName(COMPILE_CONFIGURATION_NAME).getResolvedConfiguration().getResolvedArtifacts().forEach(artifact ->
        {
            try
            {
                final File file = artifact.getFile();
                dependenciesAsFile.add(file);
                dependenciesAsUrl.add(file.toURI().toURL());
                logger.info("dependency add: {}", file);
            }
            catch (MalformedURLException e)
            {
                logger.error("Failed to add dependency!", e);
            }
        });

        input.forEach(dir ->
        {
            final FileTree tree = project.fileTree(dir);
            final Path root = dir.toPath();

            tree.filter(file -> file.getName().endsWith(".class")).forEach(file ->
            {
                final String path = root.relativize(file.toPath()).toString();
                final String className = path.replace(File.separatorChar, '.').replace(".class", "");

                logger.debug("Source [{}, {}]",className, file.toString());

                sources.add(new ScrSource(className, file));
            });

            try
            {
                dependenciesAsUrl.add(dir.toURI().toURL());
                dependenciesAsFile.add(dir);
            }
            catch (MalformedURLException e)
            {
                logger.error("Failed to add dir to list of dependencies!", e);
            }
        });

        final URL[] dependencies = new URL[dependenciesAsUrl.size()];
        dependenciesAsUrl.toArray(dependencies);

        scrProject.setClassLoader(new URLClassLoader(dependencies, this.getClass().getClassLoader()));
        scrProject.setDependencies(dependenciesAsFile);
        scrProject.setSources(sources);

        return scrProject;
    }

    private Options createOptions() {

        final Options scrOptions = new Options();
        scrOptions.setOutputDirectory(outputDirectory);
        scrOptions.setGenerateAccessors(true);
        scrOptions.setStrictMode(false);
        scrOptions.setSpecVersion(null);

        return scrOptions;
    }

    private static class ScrSource
    implements Source
    {
        private final String myClassName;
        private final File mySourceFile;

        private ScrSource(String className, File sourceFile)
        {
            myClassName = className;
            mySourceFile = sourceFile;
        }

        @Override
        public String getClassName()
        {
            return myClassName;
        }

        @Override
        public File getFile()
        {
            return mySourceFile;
        }
    }
}
