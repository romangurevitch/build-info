package org.jfrog.build.extractor.clientConfiguration.util;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.jfrog.build.api.Dependency;
import org.jfrog.build.api.dependency.BuildDependency;
import org.jfrog.build.api.dependency.DownloadableArtifact;
import org.jfrog.build.api.dependency.pattern.PatternType;
import org.jfrog.build.api.search.AqlSearchResult;
import org.jfrog.build.api.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Tamirh on 25/04/2016.
 */
public class AqlDependenciesHelper implements DependenciesHelper {

    private DependenciesDownloader downloader;
    private Log log;
    private String artifactoryUrl;
    private String target;

    public AqlDependenciesHelper(DependenciesDownloader downloader, String artifactoryUrl, String target, Log log) {
        this.downloader = downloader;
        this.log = log;
        this.artifactoryUrl = artifactoryUrl;
        this.target = target;
    }

    @Override
    public List<Dependency> retrievePublishedDependencies(String resolvePattern)
            throws IOException, InterruptedException {
        if (StringUtils.isBlank(resolvePattern)) {
            return Collections.emptyList();
        }
        log.info("Beginning to resolve Build Info published dependencies.");
        List<Dependency> dependencies = downloader.download(collectArtifactsToDownload(resolvePattern));
        log.info("Finished resolving Build Info published dependencies.");
        return dependencies;
    }

    @Override
    public void setFlatDownload(boolean flat){
        this.downloader.setFlatDownload(flat);
    }

    private Set<DownloadableArtifact> collectArtifactsToDownload(String aql) {
        Set<DownloadableArtifact> downloadableArtifacts = Sets.newHashSet();
        try{
            AqlSearchResult aqlSearchResult = downloader.getClient().searchArtifactsByAql(aql);
            List<AqlSearchResult.SearchEntry> searchResults = aqlSearchResult.getResults();
            for (AqlSearchResult.SearchEntry searchEntry : searchResults) {
                downloadableArtifacts.add(new DownloadableArtifact(StringUtils.stripEnd(artifactoryUrl, "/") + "/" + searchEntry.getRepo(), target, searchEntry.getPath() + "/" + searchEntry.getName(), "", "", PatternType.NORMAL));
            }
        } catch (IOException e){
            log.info("Failed to execute aql search");
        } finally {
            return downloadableArtifacts;
        }
    }

    public void setArtifactoryUrl(String artifactoryUrl) {
        this.artifactoryUrl = artifactoryUrl;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getArtifactoryUrl() {
        return artifactoryUrl;
    }
}
