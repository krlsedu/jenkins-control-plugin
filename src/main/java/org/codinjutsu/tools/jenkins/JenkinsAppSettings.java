/*
 * Copyright (c) 2013 David Boissier
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

package org.codinjutsu.tools.jenkins;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.codinjutsu.tools.jenkins.model.Build;
import org.codinjutsu.tools.jenkins.model.BuildStatusEnum;
import org.jetbrains.annotations.NotNull;

@State(
        name = "Jenkins.Application.Settings",
        storages = {
                @Storage("jenkinsSettings.xml")
        }
)
public class JenkinsAppSettings implements PersistentStateComponent<JenkinsAppSettings.State> {

    public static final String DUMMY_JENKINS_SERVER_URL = "http://dummyjenkinsserver";
    public static final int DEFAULT_BUILD_DELAY = 0;
    public static final int DEFAULT_BUILD_RETRY = 0;
    public static final int RESET_PERIOD_VALUE = 0;

    private State myState = new State();

    public static JenkinsAppSettings getSafeInstance(Project project) {
        JenkinsAppSettings settings = project.getService(JenkinsAppSettings.class);
        return settings != null ? settings : new JenkinsAppSettings();
    }

    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(State state) {
        XmlSerializerUtil.copyBean(state, myState);
    }

    public String getServerUrl() {
        return ConfigFile.get("serverUrl");
    }

    public void setServerUrl(String serverUrl) {
        myState.setServerUrl(serverUrl);
        ConfigFile.set("serverUrl", serverUrl);
    }

    public boolean isServerUrlSet() {
        final String serverUrl = myState.getServerUrl();
        return StringUtils.isNotEmpty(serverUrl) && !DUMMY_JENKINS_SERVER_URL.equals(serverUrl);
    }

    public int getBuildDelay() {
        return ConfigFile.getInteger("delay");
    }

    public void setDelay(int delay) {
        myState.setDelay(delay);
        ConfigFile.set("delay", delay);
    }

    public int getJobRefreshPeriod() {
        return ConfigFile.getInteger("jobRefreshPeriod");
    }

    public void setJobRefreshPeriod(int jobRefreshPeriod) {
        myState.setJobRefreshPeriod(jobRefreshPeriod);
        ConfigFile.set("jobRefreshPeriod", jobRefreshPeriod);
    }

    public int getRssRefreshPeriod() {
        return ConfigFile.getInteger("rssRefreshPeriod");
    }

    public void setRssRefreshPeriod(int rssRefreshPeriod) {
        myState.setRssRefreshPeriod(rssRefreshPeriod);
        ConfigFile.set("rssRefreshPeriod", rssRefreshPeriod);
    }

    public String getSuffix() {
        return ConfigFile.getString("suffix");
    }

    public void setSuffix(String suffix) {
        myState.setSuffix(suffix);
        ConfigFile.set("suffix", suffix);
    }

    private RssSettings getRssSettings() {
        return myState.getRssSettings();
    }

    public boolean shouldDisplaySuccessOrStable() {
        return getRssSettings().isDisplaySuccessOrStable();
    }

    public boolean shouldDisplayFailOrUnstable() {
        return getRssSettings().isDisplayUnstableOrFail();
    }

    public boolean shouldDisplayAborted() {
        return getRssSettings().isDisplayAborted();
    }

    public void setIgnoreSuccessOrStable(boolean ignoreSucessOrStable) {
        getRssSettings().setDisplaySuccessOrStable(ignoreSucessOrStable);
        ConfigFile.set("ignoreSucessOrStable", ignoreSucessOrStable);
    }

    public void setDisplayUnstableOrFail(boolean displayUnstableOrFail) {
        getRssSettings().setDisplayUnstableOrFail(displayUnstableOrFail);
        ConfigFile.set("displayUnstableOrFail", displayUnstableOrFail);
    }

    public void setDisplayAborted(boolean displayAborted) {
        getRssSettings().setDisplayAborted(displayAborted);
        ConfigFile.set("displayAborted", displayAborted);
    }

    public boolean shouldDisplayOnLogEvent(Build build) {
        BuildStatusEnum buildStatus = build.getStatus();
        if (BuildStatusEnum.SUCCESS.equals(buildStatus) || BuildStatusEnum.STABLE.equals(buildStatus)) {
            return shouldDisplaySuccessOrStable();
        }
        if (BuildStatusEnum.FAILURE.equals(buildStatus) || BuildStatusEnum.UNSTABLE.equals(buildStatus)) {
            return shouldDisplayFailOrUnstable();
        }
        if (BuildStatusEnum.ABORTED.equals(buildStatus)) {
            return shouldDisplayAborted();
        }

        return false;
    }

    public int getNumBuildRetries() {
        return ConfigFile.getInteger("numBuildRetries");
    }

    public void setNumBuildRetries(int numBuildRetries) {
        myState.setNumBuildRetries(numBuildRetries);
        ConfigFile.set("numBuildRetries", numBuildRetries);
    }

    public boolean isUseGreenColor() {
        return ConfigFile.getBoolean("useGreenColor");
    }

    public void setUseGreenColor(boolean useGreenColor) {
        myState.setUseGreenColor(useGreenColor);
        ConfigFile.set("useGreenColor", useGreenColor);
    }

    public boolean isShowAllInStatusbar() {
        return ConfigFile.getBoolean("showAllInStatusbar");
    }

    public void setShowAllInStatusbar(boolean showAllInStatusbar) {
        myState.setShowAllInStatusbar(showAllInStatusbar);
        ConfigFile.set("showAllInStatusbar", showAllInStatusbar);
    }

    public boolean isAutoLoadBuilds() {
        return ConfigFile.getBoolean("autoLoadBuilds");
    }

    public void setAutoLoadBuilds(boolean autoLoadBuilds) {
        myState.setAutoLoadBuilds(autoLoadBuilds);
        ConfigFile.set("autoLoadBuilds", autoLoadBuilds);
    }

    @NotNull
    public DoubleClickAction getDoubleClickAction() {
        return myState.getDoubleClickAction();
    }

    public void setDoubleClickAction(@NotNull DoubleClickAction doubleClickAction) {
        myState.setDoubleClickAction(doubleClickAction);
        ConfigFile.set("doubleClickAction", doubleClickAction);
    }

    public boolean isShowLogIfTriggerBuild() {
        return ConfigFile.getBoolean("showLogIfTriggerBuild");
    }

    public void setShowLogIfTriggerBuild(boolean showLogIfTriggerBuild) {
        myState.setShowLogIfTriggerBuild(showLogIfTriggerBuild);
        ConfigFile.set("showLogIfTriggerBuild", showLogIfTriggerBuild);
    }

    @Data
    public static class State {

        private String serverUrl = DUMMY_JENKINS_SERVER_URL;
        private int delay = DEFAULT_BUILD_DELAY;
        private int jobRefreshPeriod = RESET_PERIOD_VALUE;
        private int rssRefreshPeriod = RESET_PERIOD_VALUE;
        private String suffix = "";

        private int numBuildRetries = DEFAULT_BUILD_RETRY;
        private RssSettings rssSettings = new RssSettings();
        private boolean useGreenColor = false;
        private boolean showAllInStatusbar = false;
        private boolean autoLoadBuilds = false;
        private DoubleClickAction doubleClickAction = DoubleClickAction.DEFAULT;
        private boolean showLogIfTriggerBuild = true;
    }

    @Data
    public static class RssSettings {
        private boolean displaySuccessOrStable = true;
        private boolean displayUnstableOrFail = true;
        private boolean displayAborted = true;
    }
}
