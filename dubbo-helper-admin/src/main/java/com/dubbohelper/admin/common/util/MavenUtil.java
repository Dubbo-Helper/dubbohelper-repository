package com.dubbohelper.admin.common.util;

import com.dubbohelper.admin.dto.MavenDataDTO;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.eclipse.aether.version.Version;

import java.util.List;

/**
 * maven工具类
 *
 * @author lijinbo
 */
public class MavenUtil {

    /**
     * 建立RepositorySystem
     *
     * @return RepositorySystem
     */
    private static RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        return locator.getService(RepositorySystem.class);
    }

    /**
     * 建立RepositorySystemSession
     *
     * @param system RepositorySystem
     * @return RepositorySystemSession
     */
    private static RepositorySystemSession newSession(RepositorySystem system, String target) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        /*"target/local-repo" */
        LocalRepository localRepo = new LocalRepository(target);
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

        return session;
    }

    /**
     * 从指定maven地址下载指定jar包
     *
     * @param dto maven-jar包的定位（groupId:artifactId:version)
     * @throws ArtifactResolutionException
     */
    public static void downLoad(MavenDataDTO dto) throws ArtifactResolutionException {
        String groupId = dto.getGroupId();
        String artifactId = dto.getArtifactId();
        String version = dto.getVersion();
        String repositoryUrl = dto.getRepository();
        String target = dto.getTarget();
        String username = dto.getUsername();
        String password = dto.getPassword();

        RepositorySystem repoSystem = newRepositorySystem();
        RepositorySystemSession session = newSession(repoSystem, target);
        RemoteRepository central = null;
        if (username == null && password == null) {
            central = new RemoteRepository.Builder("central", "default", repositoryUrl).build();
        } else {
            Authentication authentication = new AuthenticationBuilder().addUsername(username).addPassword(password).build();
            central = new RemoteRepository.Builder("central", "default", repositoryUrl).setAuthentication(authentication).build();
        }

        //下载一个jar包
        Artifact artifact = new DefaultArtifact(groupId + ":" + artifactId + ":" + version);
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.addRepository(central);
        artifactRequest.setArtifact(artifact);
        repoSystem.resolveArtifact(session, artifactRequest);
    }

    /**
     * 根据groupId和artifactId获取所有版本列表
     *
     * @param dto MavenDataDTO对象，包括基本信息
     * @return version列表
     * @throws VersionRangeResolutionException
     */
    public static List<Version> getAllVersions(MavenDataDTO dto) throws VersionRangeResolutionException {
        String groupId = dto.getGroupId();
        String artifactId = dto.getArtifactId();
        String repositoryUrl = dto.getRepository();
        String target = dto.getTarget();
        String username = dto.getUsername();
        String password = dto.getPassword();

        RepositorySystem repoSystem = newRepositorySystem();
        RepositorySystemSession session = newSession(repoSystem, target);
        RemoteRepository central = null;
        if (username == null && password == null) {
            central = new RemoteRepository.Builder("central", "default", repositoryUrl).build();
        } else {
            Authentication authentication = new AuthenticationBuilder().addUsername(username).addPassword(password).build();
            central = new RemoteRepository.Builder("central", "default", repositoryUrl).setAuthentication(authentication).build();
        }
        Artifact artifact = new DefaultArtifact(groupId + ":" + artifactId + ":[0,)");
        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact(artifact);
        rangeRequest.addRepository(central);
        VersionRangeResult rangeResult = repoSystem.resolveVersionRange(session, rangeRequest);
        return rangeResult.getVersions();
    }
}
