/*
 * Copyright 2014, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.page.projects.projectsettings;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.zanata.page.projects.ProjectBasePage;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the Project General Settings tab.
 *
 * @author Damian Jansen
 * <a href="mailto:djansen@redhat.com">djansen@redhat.com</a>
 */
@Slf4j
public class ProjectGeneralTab extends ProjectBasePage {

    private By projectIdField = By.id("settings-general-form:slugField");
    private By projectNameField = By.id("settings-general-form:nameField:name");
    private By descriptionField = By.id("settings-general-form:descriptionField:description");
    private By projectTypeList = By.id("project-types");
    private By homepageField = By.id("settings-general-form:homePageField:homePage");
    private By repoField = By.id("settings-general-form:repoField:repo");
    private By archiveButton = By.id("settings-general-form:button-archive-project");
    private By unarchiveButton = By.id("settings-general-form:button-unarchive-project");
    private By lockProjectButton = By.id("settings-general-form:button-lock-project");
    private By unlockProjectButton = By.id("settings-general-form:button-unlock-project");
    private By updateButton = By.id("settings-general-form:button-update-settings");

    public ProjectGeneralTab(WebDriver driver) {
        super(driver);
    }

    /**
     * Get the project ID
     *
     * @return project ID string
     */
    public String getProjectId() {
        return waitForWebElement(projectIdField).getAttribute("value");
    }

    /**
     * Enter a new name for the project. Removes any existing text.
     *
     * @param projectName new project name
     * @return new Project General Settings page
     */
    public ProjectGeneralTab enterProjectName(final String projectName) {
        log.info("Enter project name {}", projectName);
        waitForWebElement(projectNameField).clear();
        waitForWebElement(projectNameField).sendKeys(projectName);
        defocus(projectNameField);
        return new ProjectGeneralTab(getDriver());
    }

    /**
     * Enter a new description for the project. Removes any existing text.
     * @param projectDescription new project description
     * @return new Project General Settings page
     */
    public ProjectGeneralTab enterDescription(String projectDescription) {
        log.info("Enter project description {}", projectDescription);
        waitForWebElement(descriptionField).clear();
        waitForWebElement(descriptionField).sendKeys(projectDescription);
        defocus(descriptionField);
        return new ProjectGeneralTab(getDriver());
    }

    /**
     * Select a new type for the project.
     * Searches by display name.
     *
     * @param projectType new project type
     * @return new Project General Settings page
     */
    public ProjectGeneralTab selectProjectType(String projectType) {
        log.info("Click Project type {}", projectType);
        assert getProjectTypes().containsKey(projectType);
        WebElement projectTypeButton = getProjectTypes().get(projectType);
        scrollIntoView(projectTypeButton);
        projectTypeButton.click();
        return new ProjectGeneralTab(getDriver());
    }

    /**
     * Query the currently selected project type.
     * @return String project type selected
     */
    public String getSelectedProjectType() {
        log.info("Query selected project type");
        Map<String, WebElement> projectTypes = getProjectTypes();
        for (String type : projectTypes.keySet()) {
            if (projectTypes.get(type)
                    .findElement(By.tagName("input"))
                    .isSelected()) {
                return type;
            }
        }
        return "None";
    }

    // Return a map of project type to div container
    private Map<String, WebElement> getProjectTypes() {
        Map<String, WebElement> types = new HashMap<String, WebElement>();
        for (WebElement projectTypeRow : waitForWebElement(projectTypeList)
                .findElements(By.tagName("li"))) {
            String label = projectTypeRow.findElement(By.tagName("label"))
                    .getText();
            String meta = projectTypeRow.findElement(By.className("txt--meta"))
                    .getText();
            types.put(label.substring(0, label.indexOf(meta)).trim(),
                    projectTypeRow.findElement(By.xpath(".//div")));
        }
        return types;
    }

    /**
     * Query for availability of the Archive This Project button.
     * Only Administrators can use this feature.
     * @return button available true/false
     */
    public boolean isArchiveButtonAvailable() {
        log.info("Query is Archive button displayed");
        return getDriver().findElements(archiveButton).size() > 0;
    }

    /**
     * Press the "Archive this project" button
     * @return new Project General Settings page
     */
    public ProjectGeneralTab archiveProject() {
        log.info("Click Archive this project");
        clickElement(archiveButton);
        return new ProjectGeneralTab(getDriver());
    }

    /**
     * Press the "Unarchive this project" button
     * @return new Project General Settings page
     */
    public ProjectGeneralTab unarchiveProject() {
        log.info("Click Unarchive this project");
        clickElement(unarchiveButton);
        return new ProjectGeneralTab(getDriver());
    }

    /**
     * Press the "Make this project read only" button
     * @return new Project General Settings page
     */
    public ProjectGeneralTab lockProject() {
        log.info("Click Make this project read only");
        clickElement(lockProjectButton);
        return new ProjectGeneralTab(getDriver());
    }

    /**
     * Press the "Make this project writable" button
     * @return new Project General Settings page
     */
    public ProjectGeneralTab unlockProject() {
        log.info("Click Make this project writable");
        clickElement(unlockProjectButton);
        return new ProjectGeneralTab(getDriver());
    }

    /**
     * Enter a new home page url for the project. Removes any existing text.
     * @param homepage new project home page
     * @return new Project General Settings page
     */
    public ProjectGeneralTab enterHomePage(String homepage) {
        log.info("Enter home page {}", homepage);
        waitForWebElement(homepageField).clear();
        waitForWebElement(homepageField).sendKeys(homepage);
        return new ProjectGeneralTab(getDriver());
    }

    /**
     * Enter a new repository url for the project. Removes any existing text.
     * @param repo new project description
     * @return new Project General Settings page
     */
    public ProjectGeneralTab enterRepository(String repo) {
        log.info("Enter repository {}", repo);
        waitForWebElement(repoField).clear();
        waitForWebElement(repoField).sendKeys(repo);
        return new ProjectGeneralTab(getDriver());
    }

    /**
     * Press the "Update general settings" button
     * @return new Project General Settings page
     */
    public ProjectGeneralTab updateProject() {
        log.info("Click Update general settings");
        scrollIntoView(waitForWebElement(updateButton));
        clickAndCheckErrors(waitForWebElement(updateButton));
        return new ProjectGeneralTab(getDriver());
    }
}
