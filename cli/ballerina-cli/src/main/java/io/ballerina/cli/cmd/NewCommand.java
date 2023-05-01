/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.cli.cmd;

import io.ballerina.cli.BLauncherCmd;
import io.ballerina.cli.launcher.BLauncherException;
import io.ballerina.projects.util.ProjectConstants;
import io.ballerina.projects.util.ProjectUtils;
import org.wso2.ballerinalang.util.RepoUtils;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static io.ballerina.cli.cmd.CommandUtil.checkPackageFilesExists;
import static io.ballerina.cli.cmd.CommandUtil.initPackageFromCentral;
import static io.ballerina.cli.cmd.Constants.NEW_COMMAND;
import static io.ballerina.projects.util.ProjectUtils.guessPkgName;

/**
 * New command for creating a ballerina project.
 *
 * @since 2.0.0
 */
@CommandLine.Command(name = NEW_COMMAND, description = "Create a new Ballerina project")
public class NewCommand implements BLauncherCmd {

    private PrintStream errStream;
    private boolean exitWhenFinish;
    Path homeCache = RepoUtils.createAndGetHomeReposPath();

    @CommandLine.Parameters
    public List<String> argList;

    @CommandLine.Option(names = {"--help", "-h"}, hidden = true)
    private boolean helpFlag;

    @CommandLine.Option(names = {"--template", "-t"}, description = "Acceptable values: [main, service, lib] " +
            "default: default")
    public String template = "default";

    public NewCommand() {
        this.errStream = System.err;
        this.exitWhenFinish = true;
        CommandUtil.initJarFs();
    }

    public NewCommand(PrintStream errStream, boolean exitWhenFinish) {
        this.errStream = errStream;
        this.exitWhenFinish = exitWhenFinish;
        CommandUtil.initJarFs();
    }

    public NewCommand(PrintStream errStream, boolean exitWhenFinish, Path customHomeCache) {
        this.errStream = errStream;
        this.exitWhenFinish = exitWhenFinish;
        CommandUtil.initJarFs();
        this.homeCache = customHomeCache;
    }

    @Override
    public void execute() {
        // If help flag is given print the help message.
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(NEW_COMMAND);
            errStream.println(commandUsageInfo);
            return;
        }

        // Check if the project path is given
        if (null == argList) {
            CommandUtil.printError(errStream,
                    "project path is not provided.",
                    "bal new <project-path>",
                    true);
            CommandUtil.exitError(this.exitWhenFinish);
            return;
        }
        // Check if one argument is given and not more than one argument.
        if (!(1 == argList.size())) {
            CommandUtil.printError(errStream,
                    "too many arguments",
                    "bal new <project-path>",
                    true);
            CommandUtil.exitError(this.exitWhenFinish);
            return;
        }

        Path packagePath = Paths.get(argList.get(0));
        Path absolutePackagePath = packagePath;
        if (Files.exists(packagePath.toAbsolutePath().normalize())) {
            absolutePackagePath = packagePath.toAbsolutePath().normalize();
        }

        CommandUtil.setPrintStream(errStream);
        Path packageDirectory;
        String packageName;

        // Check if the given path is a valid path
        if (Files.exists((absolutePackagePath))) {
            // If the given path is a ballerina project, fail the command.
            if (ProjectUtils.isBallerinaProject(absolutePackagePath)) {
                CommandUtil.printError(errStream,
                        "directory is already a Ballerina project.",
                        null,
                        false);
                CommandUtil.exitError(this.exitWhenFinish);
                return;
            }
            checkPackageFilesExists(absolutePackagePath);
            packageDirectory = absolutePackagePath;
            packageName = absolutePackagePath.getFileName().toString();
        } else {
            if (absolutePackagePath.getParent() == null) {
                CommandUtil.printError(errStream,
                        "destination '" + absolutePackagePath + "' does not exist.",
                        "bal new <project-path>",
                        true);
                CommandUtil.exitError(this.exitWhenFinish);
                return;
            }
            packageDirectory = absolutePackagePath.getParent();
            packageName = absolutePackagePath.getFileName().toString();
            // Check if the parent directory path is a valid path
            if (!Files.exists((packageDirectory))) {
                CommandUtil.printError(errStream,
                        "destination '" + absolutePackagePath + "' does not exist.",
                        "bal new <project-path>",
                        true);
                CommandUtil.exitError(this.exitWhenFinish);
                return;
            }
            // If the parent directory is a ballerina project, fail the command.
            if (ProjectUtils.isBallerinaProject(packageDirectory)) {
                CommandUtil.printError(errStream,
                        "parent directory is already a Ballerina project.",
                        null,
                        false);
                CommandUtil.exitError(this.exitWhenFinish);
                return;
            }
        }

        // Check if the command is executed inside a ballerina project
        Path projectRoot = ProjectUtils.findProjectRoot(packageDirectory);
        if (projectRoot != null) {
            CommandUtil.printError(errStream,
                    "directory is already within a Ballerina project :" +
                            projectRoot.resolve(ProjectConstants.BALLERINA_TOML),
                    null,
                    false);
            CommandUtil.exitError(this.exitWhenFinish);
            return;
        }

        if (!ProjectUtils.validateNameLength(packageName)) {
            CommandUtil.printError(errStream,
                                   "invalid package name : '" + packageName + "' :\n" +
                                           "Maximum length of package name is 256 characters.",
                                   null,
                                   false);
            CommandUtil.exitError(this.exitWhenFinish);
            return;
        }

        if (!ProjectUtils.validatePackageName(packageName)) {
            packageName = ProjectUtils.guessPkgName(packageName, template);
            errStream.println("package name is derived as '" + packageName
                    + "'. Edit the Ballerina.toml to change it.");
            errStream.println();
        }

        try {
            // check if the template matches with one of the inbuilt template types
            if (CommandUtil.getTemplates().contains(template)) {
                // create package with inbuilt template
                if (Files.exists((absolutePackagePath))) {
                    CommandUtil.checkTemplateFilesExists(template, absolutePackagePath);
                }
                CommandUtil.initPackageByTemplate(absolutePackagePath, packageName, template);
            } else {
                Path balaCache = homeCache.resolve(ProjectConstants.REPOSITORIES_DIR)
                        .resolve(ProjectConstants.CENTRAL_REPOSITORY_CACHE_NAME)
                        .resolve(ProjectConstants.BALA_DIR_NAME);
                initPackageFromCentral(balaCache, absolutePackagePath, packageName, template);
            }
        } catch (AccessDeniedException e) {
            CommandUtil.printError(errStream,
                    "error occurred while creating project : " + "Insufficient Permission : " + e.getMessage(),
                    null,
                    false);
            CommandUtil.exitError(this.exitWhenFinish);
        } catch (BLauncherException e) {
            if (Files.exists(absolutePackagePath)) {
                try {
                    Files.delete(absolutePackagePath);
                } catch (IOException ignored) {
                }
            }
            CommandUtil.printError(errStream, e.getDetailedMessages().get(0),
                    null,
                    false);
            CommandUtil.exitError(this.exitWhenFinish);
        } catch (IOException | URISyntaxException e) {
            CommandUtil.printError(errStream,
                    "error occurred while creating project : " + e.getMessage(),
                    null,
                    false);
            CommandUtil.exitError(this.exitWhenFinish);
        }
        if (Files.exists(absolutePackagePath)) {
            errStream.println("Created new package '" + guessPkgName(packageName, template)
                    + "' at " + absolutePackagePath + ".");
        }
        if (this.exitWhenFinish) {
            Runtime.getRuntime().exit(0);
        }
        return;
    }

    @Override
    public String getName() {
        return NEW_COMMAND;
    }

    @Override
    public void printLongDesc(StringBuilder out) {
        out.append("create a new Ballerina project");
    }

    @Override
    public void printUsage(StringBuilder out) {
        out.append("  bal new <project-path> \n");
    }

    @Override
    public void setParentCmdParser(CommandLine parentCmdParser) {
    }

}
