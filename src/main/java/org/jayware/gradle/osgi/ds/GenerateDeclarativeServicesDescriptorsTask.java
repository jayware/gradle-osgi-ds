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
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.gradle.api.plugins.JavaPlugin.COMPILE_CONFIGURATION_NAME;


public class GenerateDeclarativeServicesDescriptorsTask
extends DefaultTask
{
    private final Logger log = LoggerFactory.getLogger(GenerateDeclarativeServicesDescriptorsTask.class);

    private final Project project;

    @OutputDirectory
    File outputDirectory;

    @InputFiles
    FileCollection input;

    public GenerateDeclarativeServicesDescriptorsTask()
    {
        project = getProject();

        outputDirectory = new File(project.getBuildDir(), "/tmp/osgi-ds");
        getOutputs().dir(outputDirectory);
    }

    @TaskAction
    public void generateDeclarativeServicesDescriptors()
    throws SCRDescriptorException, SCRDescriptorFailureException, MalformedURLException
    {
        final Options scrOptions = createOptions();
        final org.apache.felix.scrplugin.Project scrProject = createProject();

        final SCRDescriptorGenerator scrGenerator = new SCRDescriptorGenerator(new GradleSCRDescriptorGeneratorLoggerAdapter(log));
        scrGenerator.setOptions(scrOptions);
        scrGenerator.setProject(scrProject);

        scrGenerator.execute();
    }

    private org.apache.felix.scrplugin.Project createProject()
    {
        final org.apache.felix.scrplugin.Project scrProject = new org.apache.felix.scrplugin.Project();
        final Set<URL> dependenciesAsUrl = new HashSet<>();
        final Set<File> dependenciesAsFile = new HashSet<>();
        final List<Source> sources = new ArrayList<>();
        final Configuration compileConfiguration = project.getConfigurations().getByName(COMPILE_CONFIGURATION_NAME);

        compileConfiguration.getResolvedConfiguration().getResolvedArtifacts().forEach(artifact ->
        {
            try
            {
                final File file = artifact.getFile();
                dependenciesAsFile.add(file);
                dependenciesAsUrl.add(file.toURI().toURL());
                log.info("dependency added: {}", file);
            }
            catch (MalformedURLException e)
            {
                throw new GradleException(getName() + " failed!", e);
            }
        });

        input.forEach(dir ->
        {
            final AtomicBoolean addDirAsDependencies = new AtomicBoolean(false);
            final FileTree tree = project.fileTree(dir);
            final Path root = dir.toPath();

            tree.filter(file -> file.getName().endsWith(".class")).forEach(file ->
            {
                final String path = root.relativize(file.toPath()).toString();
                final String className = path.replace(File.separatorChar, '.').replace(".class", "");

                log.debug("Source [{}, {}]",className, file.toString());

                sources.add(new ScrSource(className, file));

                try
                {
                    dependenciesAsUrl.add(dir.toURI().toURL());
                    dependenciesAsFile.add(dir);
                }
                catch (MalformedURLException e)
                {
                    throw new GradleException(getName() + " failed!", e);
                }
            });
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
        scrOptions.setGenerateAccessors(false);
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
